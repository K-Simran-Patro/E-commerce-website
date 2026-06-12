package com.ecommerce_project.admin_service.controller;

import com.ecommerce_project.admin_service.service.ExcelUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
public class AdminUploadController {

    @Autowired
    private ExcelUploadService excelUploadService;

    @PostMapping("/upload-product-excel")
    public String uploadProductExcel(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Name") String userName
    ) {
        return excelUploadService.readExcelAndCreateProducts(file, token, userName);
    }
}