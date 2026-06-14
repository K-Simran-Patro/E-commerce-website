package com.ecommerce_project.admin_service.dto.bulk;

public class BulkUploadResponseDTO {

    private String message;

    private int categoriesCreated;
    private int categoriesReused;

    private int productsCreated;
    private int productsReused;

    private int variantsCreated;
    private int duplicateVariantsSkipped;

    private int rowsSkipped;

    public BulkUploadResponseDTO() {
    }

    public BulkUploadResponseDTO(String message) {
        this.message = message;
    }

    public void increaseCategoriesCreated() {
        this.categoriesCreated++;
    }

    public void increaseCategoriesReused() {
        this.categoriesReused++;
    }

    public void increaseProductsCreated() {
        this.productsCreated++;
    }

    public void increaseProductsReused() {
        this.productsReused++;
    }

    public void increaseVariantsCreated() {
        this.variantsCreated++;
    }

    public void increaseDuplicateVariantsSkipped() {
        this.duplicateVariantsSkipped++;
    }

    public void increaseRowsSkipped() {
        this.rowsSkipped++;
    }

    public void buildSuccessMessage() {
        this.message = "Bulk upload completed. "
                + "Categories created: " + categoriesCreated
                + ", categories reused: " + categoriesReused
                + ", products created: " + productsCreated
                + ", products reused: " + productsReused
                + ", variants created: " + variantsCreated
                + ", duplicate variants skipped: " + duplicateVariantsSkipped
                + ", rows skipped: " + rowsSkipped + ".";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCategoriesCreated() {
        return categoriesCreated;
    }

    public void setCategoriesCreated(int categoriesCreated) {
        this.categoriesCreated = categoriesCreated;
    }

    public int getCategoriesReused() {
        return categoriesReused;
    }

    public void setCategoriesReused(int categoriesReused) {
        this.categoriesReused = categoriesReused;
    }

    public int getProductsCreated() {
        return productsCreated;
    }

    public void setProductsCreated(int productsCreated) {
        this.productsCreated = productsCreated;
    }

    public int getProductsReused() {
        return productsReused;
    }

    public void setProductsReused(int productsReused) {
        this.productsReused = productsReused;
    }

    public int getVariantsCreated() {
        return variantsCreated;
    }

    public void setVariantsCreated(int variantsCreated) {
        this.variantsCreated = variantsCreated;
    }

    public int getDuplicateVariantsSkipped() {
        return duplicateVariantsSkipped;
    }

    public void setDuplicateVariantsSkipped(int duplicateVariantsSkipped) {
        this.duplicateVariantsSkipped = duplicateVariantsSkipped;
    }

    public int getRowsSkipped() {
        return rowsSkipped;
    }

    public void setRowsSkipped(int rowsSkipped) {
        this.rowsSkipped = rowsSkipped;
    }
}