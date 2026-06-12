package com.ecommerce_project.admin_service.service;

import java.math.BigDecimal;
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

@Service
public class ExcelUploadService {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public ExcelUploadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String readExcelAndCreateProducts(MultipartFile file, String token, String userName) {

        int createdCategories = 0;
        int reusedCategories = 0;

        int createdProducts = 0;
        int reusedProducts = 0;

        int createdVariants = 0;
        int skippedVariants = 0;

        int skippedRows = 0;

        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            /*
              Expected Excel columns:

              0 - Parent Category Name
              1 - Category Name / Child Category Name
              2 - Product Name
              3 - Brand Name
              4 - Description
              5 - Main Image Key
              6 - SKU
              7 - Color
              8 - Size
              9 - Price
            */

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    skippedRows++;
                    continue;
                }

                String parentCategoryName = getCellValue(row.getCell(0));
                String categoryName = getCellValue(row.getCell(1));
                String productName = getCellValue(row.getCell(2));
                String brandName = getCellValue(row.getCell(3));
                String description = getCellValue(row.getCell(4));
                String mainImageKey = getCellValue(row.getCell(5));
                String sku = getCellValue(row.getCell(6));
                String color = getCellValue(row.getCell(7));
                String size = getCellValue(row.getCell(8));
                String priceText = getCellValue(row.getCell(9));

                if (categoryName.isBlank()
                        || productName.isBlank()
                        || sku.isBlank()
                        || priceText.isBlank()) {

                    skippedRows++;
                    continue;
                }

                BigDecimal price = new BigDecimal(priceText);

                Long finalCategoryId;

                /*
                  If parent category is present:
                  1. Check/create parent category.
                  2. Check/create child category under parent.
                  3. Product will be created under child category.
                */
                if (!parentCategoryName.isBlank()) {

                    CategoryResult parentCategory = getOrCreateCategory(
                            parentCategoryName,
                            null,
                            token,
                            userName
                    );

                    if (parentCategory.created) {
                        createdCategories++;
                    } else {
                        reusedCategories++;
                    }

                    CategoryResult childCategory = getOrCreateCategory(
                            categoryName,
                            parentCategory.categoryId,
                            token,
                            userName
                    );

                    if (childCategory.created) {
                        createdCategories++;
                    } else {
                        reusedCategories++;
                    }

                    finalCategoryId = childCategory.categoryId;

                } else {

                    /*
                      If no parent category is given:
                      Category itself becomes the final category.
                    */
                    CategoryResult category = getOrCreateCategory(
                            categoryName,
                            null,
                            token,
                            userName
                    );

                    if (category.created) {
                        createdCategories++;
                    } else {
                        reusedCategories++;
                    }

                    finalCategoryId = category.categoryId;
                }

                /*
                  Product duplicate check:
                  Same product name + same final category ID = duplicate.
                */
                ProductResult product = getOrCreateProduct(
                        finalCategoryId,
                        productName,
                        brandName,
                        description,
                        mainImageKey,
                        token,
                        userName
                );

                if (product.created) {
                    createdProducts++;
                } else {
                    reusedProducts++;
                }

                /*
                  Variant duplicate check:
                  SKU should be unique globally.
                */
                boolean variantCreated = createVariantIfNotExists(
                        product.productId,
                        sku,
                        color,
                        size,
                        price,
                        token,
                        userName
                );

                if (variantCreated) {
                    createdVariants++;
                } else {
                    skippedVariants++;
                }
            }

            workbook.close();

            return "Excel upload completed. "
                    + "Categories created: " + createdCategories
                    + ", categories reused: " + reusedCategories
                    + ", products created: " + createdProducts
                    + ", products reused: " + reusedProducts
                    + ", variants created: " + createdVariants
                    + ", duplicate variants skipped: " + skippedVariants
                    + ", rows skipped: " + skippedRows + ".";

        } catch (Exception e) {
            return "Excel upload failed: " + e.getMessage();
        }
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

            String existingSlug = String.valueOf(category.get("slug"));

            if (existingSlug.equalsIgnoreCase(slug)) {

                Long id = Long.valueOf(String.valueOf(category.get("categoryId")));

                return new CategoryResult(id, false);
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("name", categoryName);
        body.put("slug", slug);
        body.put("parentId", parentCategoryId);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, getHeaders(token, userName));

        ResponseEntity<Map> response = restTemplate.exchange(
                productServiceUrl + "/admin/categories",
                HttpMethod.POST,
                request,
                Map.class
        );

        Object id = response.getBody().get("categoryId");

        return new CategoryResult(Long.valueOf(id.toString()), true);
    }

    private List<Map<String, Object>> getAllCategories(String token, String userName) {

        HttpEntity<Void> request =
                new HttpEntity<>(getHeaders(token, userName));

        ResponseEntity<List> response = restTemplate.exchange(
                productServiceUrl + "/admin/categories",
                HttpMethod.GET,
                request,
                List.class
        );

        return response.getBody();
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

            String existingName = String.valueOf(product.get("name"));
            Long existingCategoryId = Long.valueOf(String.valueOf(product.get("categoryId")));

            if (existingName.equalsIgnoreCase(productName)
                    && existingCategoryId.equals(categoryId)) {

                Long id = Long.valueOf(String.valueOf(product.get("productId")));

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

        ResponseEntity<Map> response = restTemplate.exchange(
                productServiceUrl + "/admin/products",
                HttpMethod.POST,
                request,
                Map.class
        );

        Object id = response.getBody().get("productId");

        return new ProductResult(Long.valueOf(id.toString()), true);
    }

    private List<Map<String, Object>> getAllProducts(String token, String userName) {

        HttpEntity<Void> request =
                new HttpEntity<>(getHeaders(token, userName));

        ResponseEntity<List> response = restTemplate.exchange(
                productServiceUrl + "/admin/products",
                HttpMethod.GET,
                request,
                List.class
        );

        return response.getBody();
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

            String existingSku = String.valueOf(variant.get("sku"));

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
                productServiceUrl + "/admin/variants",
                HttpMethod.POST,
                request,
                String.class
        );

        return true;
    }

    private List<Map<String, Object>> getAllVariants(String token, String userName) {

        HttpEntity<Void> request =
                new HttpEntity<>(getHeaders(token, userName));

        ResponseEntity<List> response = restTemplate.exchange(
                productServiceUrl + "/admin/variants",
                HttpMethod.GET,
                request,
                List.class
        );

        return response.getBody();
    }

    /* ================= RESULT CLASSES ================= */

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