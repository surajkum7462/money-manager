package com.suraj.moneymanager.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.suraj.moneymanager.dto.ExpenseDTO;
import com.suraj.moneymanager.dto.IncomeDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    public byte[] generateIncomeExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Income Details");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Name", "Category", "Icon", "Amount", "Date", "Created At", "Updated At"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fetch incomes
            List<IncomeDTO> incomes = incomeService.getAllIncomesForCurrentUser();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            int rowNum = 1;
            for (IncomeDTO income : incomes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(income.getId() != null ? income.getId() : 0);
                row.createCell(1).setCellValue(income.getName() != null ? income.getName() : "");
                row.createCell(2).setCellValue(income.getCategoryName() != null ? income.getCategoryName() : "N/A");
                row.createCell(3).setCellValue(income.getIcon() != null ? income.getIcon() : "");
                row.createCell(4).setCellValue(income.getAmount() != null ? income.getAmount().toPlainString() : "0.00");
                row.createCell(5).setCellValue(income.getDate() != null ? income.getDate().format(dateFormatter) : "N/A");
                row.createCell(6).setCellValue(income.getCreatedAt() != null ? income.getCreatedAt().format(dateFormatter) : "N/A");
                row.createCell(7).setCellValue(income.getUpdatedAt() != null ? income.getUpdatedAt().format(dateFormatter) : "N/A");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating income Excel: " + e.getMessage());
        }
    }
     public byte[] generateExpenseExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expense Details");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Name", "Category", "Icon", "Amount", "Date", "Created At", "Updated At"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fetch expenses
            List<ExpenseDTO> expenses = expenseService.getAllExpenseForCurrentUser();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            int rowNum = 1;
            for (ExpenseDTO expense : expenses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(expense.getId() != null ? expense.getId() : 0);
                row.createCell(1).setCellValue(expense.getName() != null ? expense.getName() : "");
                row.createCell(2).setCellValue(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A");
                row.createCell(3).setCellValue(expense.getIcon() != null ? expense.getIcon() : "");
                row.createCell(4).setCellValue(expense.getAmount() != null ? expense.getAmount().toPlainString() : "0.00");
                row.createCell(5).setCellValue(expense.getDate() != null ? expense.getDate().format(dateFormatter) : "N/A");
                row.createCell(6).setCellValue(expense.getCreatedAt() != null ? expense.getCreatedAt().format(dateFormatter) : "N/A");
                row.createCell(7).setCellValue(expense.getUpdatedAt() != null ? expense.getUpdatedAt().format(dateFormatter) : "N/A");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating expense Excel: " + e.getMessage());
        }
    }
}
