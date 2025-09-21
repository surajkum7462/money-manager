package com.suraj.moneymanager.controller;

import com.suraj.moneymanager.dto.IncomeDTO;
import com.suraj.moneymanager.service.IncomeService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class ExcelDownloadController {

    @Autowired
    private IncomeService incomeService;

    @GetMapping("/excel/download/income")
    public ResponseEntity<byte[]> downloadIncomeExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Income Details");

            // ✅ Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // ✅ Header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Name", "Category", "Icon", "Amount", "Date", "Created At", "Updated At"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // ✅ Fetch incomes (only for current logged-in user)
            List<IncomeDTO> incomes = incomeService.getAllIncomesForCurrentUser();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            int rowNum = 1;
            for (IncomeDTO income : incomes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(income.getId() != null ? income.getId() : 0);
                row.createCell(1).setCellValue(income.getName() != null ? income.getName() : "");
                row.createCell(2).setCellValue(income.getCategoryName() != null ? income.getCategoryName() : "N/A");
                row.createCell(3).setCellValue(income.getIcon() != null ? income.getIcon() : "");
                row.createCell(4).setCellValue(
                        income.getAmount() != null ? income.getAmount().toPlainString() : "0.00"
                );
                row.createCell(5).setCellValue(
                        income.getDate() != null ? income.getDate().format(dateFormatter) : "N/A"
                );
                row.createCell(6).setCellValue(
                        income.getCreatedAt() != null ? income.getCreatedAt().format(dateFormatter) : "N/A"
                );
                row.createCell(7).setCellValue(
                        income.getUpdatedAt() != null ? income.getUpdatedAt().format(dateFormatter) : "N/A"
                );
            }

            // ✅ Auto-size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ✅ Convert workbook to bytes
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=income_details.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(bos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(("Error generating Excel: " + e.getMessage()).getBytes());
        }
    }
}
