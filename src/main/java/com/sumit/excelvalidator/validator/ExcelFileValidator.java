package com.sumit.excelvalidator.validator;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

// Utility class to validate and parse Excel files
@Component
public class ExcelFileValidator {

    public Workbook getValidatedWorkbook(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null ||
                !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
            throw new IllegalArgumentException("Invalid file type. Only .xls or .xlsx allowed");
        }

        try {
            return WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
            throw new IllegalArgumentException("Uploaded file is not a valid Excel file");
        }
    }
}

