package com.suraj.moneymanager.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suraj.moneymanager.service.ExcelExportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/excel/download")
@RequiredArgsConstructor
public class ExcelDownloadController {

     private final ExcelExportService excelExportService;

    @GetMapping("/income")
    public ResponseEntity<byte[]> downloadIncomeExcel() {
        byte[] excelBytes = excelExportService.generateIncomeExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=income_details.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
    
    @GetMapping("/expense")
    public ResponseEntity<byte[]> downloadExpenseExcel() {
        byte[] excelBytes = excelExportService.generateExpenseExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expense_details.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}
