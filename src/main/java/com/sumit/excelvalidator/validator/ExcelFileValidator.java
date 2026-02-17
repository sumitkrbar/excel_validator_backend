package com.sumit.excelvalidator.validator;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

// Utility class to validate and parse Excel files
@Component
public class ExcelFileValidator {
    private static final String SHEET_ONE = "Participant upload data";
    private static final String SHEET_TWO = "Instructions";

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
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            if (workbook.getNumberOfSheets() != 2) {
                throw new IllegalArgumentException(
                        "File must contain exactly 2 sheets. Found: " + workbook.getNumberOfSheets()
                );
            }
            boolean hasSheetOne = workbook.getSheet(SHEET_ONE) != null;
            boolean hasSheetTwo = workbook.getSheet(SHEET_TWO) != null;

            if (!hasSheetOne || !hasSheetTwo) {
                throw new IllegalArgumentException(
                        "File must contain sheets named '" + SHEET_ONE + "' and '" + SHEET_TWO + "'."
                );
            }

            return workbook;
        }catch (IllegalArgumentException e){
            throw e;
        }catch (Exception e) {
            System.out.println( "hey " + e.getMessage());
            throw new IllegalArgumentException("Uploaded file is not a valid Excel file");
        }
    }
}

