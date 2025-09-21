package com.suraj.moneymanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suraj.moneymanager.service.EmailService;
import com.suraj.moneymanager.service.ExcelExportService;
import com.suraj.moneymanager.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final ExcelExportService excelExportService;
    private final ProfileService profileService;

    @GetMapping("/email/income-excel")
    public ResponseEntity<String> emailIncomeExcel() {
        try {
            String toEmail = profileService.getCurrentProfile().getEmail();

            byte[] excelBytes = excelExportService.generateIncomeExcel();

            emailService.sendEmailWithAttachment(
                    toEmail,
                    "Income Report",
                    "Hi,<br><br>Please find attached your income report.<br><br>Regards,<br>Money Manager",
                    "income_details.xlsx",
                    excelBytes
            );

            return ResponseEntity.ok("Email sent successfully to " + toEmail);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send email: " + e.getMessage());
        }
    }
}
