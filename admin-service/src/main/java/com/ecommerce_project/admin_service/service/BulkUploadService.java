package com.ecommerce_project.admin_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce_project.admin_service.dto.bulk.BulkUploadResponseDTO;
import com.ecommerce_project.admin_service.dto.bulk.BulkUploadRowDTO;

@Service
public class BulkUploadService {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    /*
      IMPORTANT:
      These paths should match the APIs available in your Product Service.

      If your Product Service Swagger shows:
      /admin/categories
      /admin/products
      /admin/variants
      then keep these as they are.

      If your Product Service Swagger shows:
      /api/categories
      /api/products
      /api/variants
      then change these three constants.
    */
    private static final String CATEGORY_API = "/admin/categories";
    private static final String PRODUCT_API = "/admin/products";
    private static final String VARIANT_API = "/admin/variants";

    public BulkUploadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BulkUploadResponseDTO uploadProducts(MultipartFile file, String token, String userName) {

        BulkUploadResponseDTO response = new BulkUploadResponseDTO();

        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            /*
              Expected Excel columns:

              A - Parent Category Name
              B - Category Name
              C - Product Name
              D - Brand Name
              E - Description
              F - Main Image Key
              G - SKU
              H - Color
              I - Size
              J - Price
            */

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    response.increaseRowsSkipped();
                    continue;
                }

                BulkUploadRowDTO rowDTO = readRow(row);

                if (!isValidRow(rowDTO)) {
                    response.increaseRowsSkipped();
                    continue;
                }

                Long finalCategoryId = getFinalCategoryId(
                        rowDTO,
                        token,
                        userName,
                        response
                );

                ProductResult productResult = getOrCreateProduct(
                        finalCategoryId,
                        rowDTO.getProductName(),
                        rowDTO.getBrandName(),
                        rowDTO.getDescription(),
                        rowDTO.getMainImageKey(),
                        token,
                        userName
                );

                if (productResult.created) {
                    response.increaseProductsCreated();
                } else {
                    response.increaseProductsReused();
                }

                boolean variantCreated = createVariantIfNotExists(
                        productResult.productId,
                        rowDTO.getSku(),
                        rowDTO.getColor(),
                        rowDTO.getSize(),
                        rowDTO.getPrice(),
                        token,
                        userName
                );

                if (variantCreated) {
                    response.increaseVariantsCreated();
                } else {
                    response.increaseDuplicateVariantsSkipped();
                }
            }

            workbook.close();

            response.buildSuccessMessage();
            return response;

        } catch (Exception e) {
            return new BulkUploadResponseDTO("Bulk upload failed: " + e.getMessage());
        }
    }

    private BulkUploadRowDTO readRow(Row row) {

        BulkUploadRowDTO dto = new BulkUploadRowDTO();

        dto.setParentCategoryName(getCellValue(row.getCell(0)));
        dto.setCategoryName(getCellValue(row.getCell(1)));
        dto.setProductName(getCellValue(row.getCell(2)));
        dto.setBrandName(getCellValue(row.getCell(3)));
        dto.setDescription(getCellValue(row.getCell(4)));
        dto.setMainImageKey(getCellValue(row.getCell(5)));
        dto.setSku(getCellValue(row.getCell(6)));
        dto.setColor(getCellValue(row.getCell(7)));
        dto.setSize(getCellValue(row.getCell(8)));

        String priceText = getCellValue(row.getCell(9));

        if (!priceText.isBlank()) {
            dto.setPrice(new BigDecimal(priceText));
        }

        return dto;
    }

    private boolean isValidRow(BulkUploadRowDTO row) {

        if (row.getCategoryName() == null || row.getCategoryName().isBlank()) {
            return false;
        }

        if (row.getProductName() == null || row.getProductName().isBlank()) {
            return false;
        }

        if (row.getSku() == null || row.getSku().isBlank()) {
            return false;
        }

        if (row.getPrice() == null || row.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        return true;
    }

    private Long getFinalCategoryId(
            BulkUploadRowDTO row,
            String token,
            String userName,
            BulkUploadResponseDTO response
    ) {

        Long finalCategoryId;

        /*
          If parent category is present:
          1. create/reuse parent category
          2. create/reuse child category under parent
          3. use child category ID for product
        */
        if (row.getParentCategoryName() != null && !row.getParentCategoryName().isBlank()) {

            CategoryResult parentCategory = getOrCreateCategory(
                    row.getParentCategoryName(),
                    null,
                    token,
                    userName
            );

            if (parentCategory.created) {
                response.increaseCategoriesCreated();
            } else {
                response.increaseCategoriesReused();
            }

            CategoryResult childCategory = getOrCreateCategory(
                    row.getCategoryName(),
                    parentCategory.categoryId,
                    token,
                    userName
            );

            if (childCategory.created) {
                response.increaseCategoriesCreated();
            } else {
                response.increaseCategoriesReused();
            }

            finalCategoryId = childCategory.categoryId;

        } else {

            CategoryResult category = getOrCreateCategory(
                    row.getCategoryName(),
                    null,
                    token,
                    userName
            );

            if (category.created) {
                response.increaseCategoriesCreated();
            } else {
                response.increaseCategoriesReused();
            }

            finalCategoryId = category.categoryId;
        }

        return finalCategoryId;
    }

    private String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            double value = cell.getNumericCellValue();

            if (value == Math.floor(value)) {
                return String.valueOf((long) value);
            }

            return String.valueOf(value);
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }

        if (cell.getCellType() == CellType.FORMULA) {
            try {
                return cell.getStringCellValue().trim();
            } catch (Exception e) {
                return String.valueOf(cell.getNumericCellValue());
            }
        }

        return "";
    }

    private String makeSlug(String text) {
        return text.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    private HttpHeaders getHeaders(String token, String userName) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        headers.set("X-User-Name", userName);

        return headers;
    }

    /* ================= CATEGORY ================= */

    private CategoryResult getOrCreateCategory(
            String categoryName,
            Long parentCategoryId,
            String token,
            String userName
    ) {

        String slug = makeSlug(categoryName);

        List<Map<String, Object>> categories = getAllCategories(token, userName);

        for (Map<String, Object> category : categories) {

            String existingSlug = getStringValue(category, "slug");

            if (existingSlug.equalsIgnoreCase(slug)) {

                Long id = getLongValue(category, "categoryId");

                return new CategoryResult(id, false);
            }
        }

        Map<String, Object> body = new HashMap<>();

        body.put("name", categoryName);
        body.put("slug", slug);
        body.put("parentId", parentCategoryId);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, getHeaders(token, userName));

        ResponseEntity<Map> apiResponse = restTemplate.exchange(
                productServiceUrl + CATEGORY_API,
                HttpMethod.POST,
                request,
                Map.class
        );

        Object responseBody = apiResponse.getBody();

        if (responseBody == null) {
            throw new RuntimeException("Category create API returned empty response.");
        }

        Map<String, Object> bodyMap = apiResponse.getBody();

        Long id = getLongValue(bodyMap, "categoryId");

        return new CategoryResult(id, true);
    }

    private List<Map<String, Object>> getAllCategories(String token, String userName) {

        HttpEntity<Void> request =
                new HttpEntity<>(getHeaders(token, userName));

        ResponseEntity<Map[]> response = restTemplate.exchange(
                productServiceUrl + CATEGORY_API,
                HttpMethod.GET,
                request,
                Map[].class
        );

        if (response.getBody() == null) {
            return new ArrayList<>();
        }

        return Arrays.asList(response.getBody());
    }

    /* ================= PRODUCT ================= */

    private ProductResult getOrCreateProduct(
            Long categoryId,
            String productName,
            String brandName,
            String description,
            String mainImageKey,
            String token,
            String userName
    ) {

        List<Map<String, Object>> products = getAllProducts(token, userName);

        for (Map<String, Object> product : products) {

            String existingName = getStringValue(product, "name");
            Long existingCategoryId = getProductCategoryId(product);

            /*
              Product can have same name under different category.
              But same product name under same final category should be reused.
            */
            if (existingName.equalsIgnoreCase(productName)
                    && existingCategoryId != null
                    && existingCategoryId.equals(categoryId)) {

                Long id = getLongValue(product, "productId");

                return new ProductResult(id, false);
            }
        }

        Map<String, Object> body = new HashMap<>();

        body.put("categoryId", categoryId);
        body.put("brandName", brandName);
        body.put("name", productName);
        body.put("description", description);
        body.put("mainImageKey", mainImageKey);
        body.put("isActive", true);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, getHeaders(token, userName));

        ResponseEntity<Map> apiResponse = restTemplate.exchange(
                productServiceUrl + PRODUCT_API,
                HttpMethod.POST,
                request,
                Map.class
        );

        if (apiResponse.getBody() == null) {
            throw new RuntimeException("Product create API returned empty response.");
        }

        Map<String, Object> bodyMap = apiResponse.getBody();

        Long id = getLongValue(bodyMap, "productId");

        return new ProductResult(id, true);
    }

    private List<Map<String, Object>> getAllProducts(String token, String userName) {

        HttpEntity<Void> request =
                new HttpEntity<>(getHeaders(token, userName));

        ResponseEntity<Map[]> response = restTemplate.exchange(
                productServiceUrl + PRODUCT_API,
                HttpMethod.GET,
                request,
                Map[].class
        );

        if (response.getBody() == null) {
            return new ArrayList<>();
        }

        return Arrays.asList(response.getBody());
    }

    /* ================= VARIANT ================= */

    private boolean createVariantIfNotExists(
            Long productId,
            String sku,
            String color,
            String size,
            BigDecimal price,
            String token,
            String userName
    ) {

        List<Map<String, Object>> variants = getAllVariants(token, userName);

        for (Map<String, Object> variant : variants) {

            String existingSku = getStringValue(variant, "sku");

            if (existingSku.equalsIgnoreCase(sku)) {
                return false;
            }
        }

        Map<String, Object> body = new HashMap<>();

        body.put("productId", productId);
        body.put("sku", sku);
        body.put("color", color);
        body.put("size", size);
        body.put("price", price);
        body.put("isActive", true);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, getHeaders(token, userName));

        restTemplate.exchange(
                productServiceUrl + VARIANT_API,
                HttpMethod.POST,
                request,
                String.class
        );

        return true;
    }

    private List<Map<String, Object>> getAllVariants(String token, String userName) {

        HttpEntity<Void> request =
                new HttpEntity<>(getHeaders(token, userName));

        ResponseEntity<Map[]> response = restTemplate.exchange(
                productServiceUrl + VARIANT_API,
                HttpMethod.GET,
                request,
                Map[].class
        );

        if (response.getBody() == null) {
            return new ArrayList<>();
        }

        return Arrays.asList(response.getBody());
    }

    /* ================= SAFE VALUE HELPERS ================= */

    private String getStringValue(Map<String, Object> map, String key) {

        Object value = map.get(key);

        if (value == null) {
            return "";
        }

        return String.valueOf(value);
    }

    private Long getLongValue(Map<String, Object> map, String key) {

        Object value = map.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }

        if (value instanceof Long) {
            return (Long) value;
        }

        if (value instanceof Double) {
            return ((Double) value).longValue();
        }

        return Long.valueOf(String.valueOf(value));
    }

    private Long getProductCategoryId(Map<String, Object> product) {

        /*
          Case 1:
          Product response directly has categoryId.
        */
        if (product.get("categoryId") != null) {
            return getLongValue(product, "categoryId");
        }

        /*
          Case 2:
          Product response has nested category object like:
          {
             "category": {
                "categoryId": 1
             }
          }
        */
        Object categoryObject = product.get("category");

        if (categoryObject instanceof Map) {

            Map<String, Object> categoryMap = (Map<String, Object>) categoryObject;

            return getLongValue(categoryMap, "categoryId");
        }

        return null;
    }

    /* ================= SMALL RESULT CLASSES ================= */

    private static class CategoryResult {

        Long categoryId;
        boolean created;

        CategoryResult(Long categoryId, boolean created) {
            this.categoryId = categoryId;
            this.created = created;
        }
    }

    private static class ProductResult {

        Long productId;
        boolean created;

        ProductResult(Long productId, boolean created) {
            this.productId = productId;
            this.created = created;
        }
    }
}