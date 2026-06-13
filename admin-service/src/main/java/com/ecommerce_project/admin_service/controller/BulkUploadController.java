package com.ecommerce_project.admin_service.controller;

import com.ecommerce_project.admin_service.dto.bulk.BulkUploadResponseDTO;
import com.ecommerce_project.admin_service.service.BulkUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/bulk-upload")
public class BulkUploadController {

    @Autowired
    private BulkUploadService bulkUploadService;

    @PostMapping("/products")
    public BulkUploadResponseDTO uploadProductsExcel(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Name") String userName
    ) {
        return bulkUploadService.uploadProducts(file, token, userName);
    }
}