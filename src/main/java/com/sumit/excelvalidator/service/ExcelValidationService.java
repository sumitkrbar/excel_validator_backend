package com.sumit.excelvalidator.service;

import com.sumit.excelvalidator.dto.CellError;
import com.sumit.excelvalidator.dto.ExcelProcessorResult;
import com.sumit.excelvalidator.dto.RowData;
import com.sumit.excelvalidator.dto.ValidationResponse;
import com.sumit.excelvalidator.processor.ExcelProcessor;
import com.sumit.excelvalidator.validator.ExcelFileValidator;
import com.sumit.excelvalidator.writer.ExcelHighlighter;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

import java.util.List;

@Service
public class ExcelValidationService {

    public static ValidationResponse validateExcelFile(MultipartFile file) {

        try (Workbook workbook = ExcelFileValidator.getValidatedWorkbook(file)) {

            ExcelProcessorResult excelProcessorResult = ExcelProcessor.processWorkbook(workbook);

            //If errors exist → highlight + return file
            if (!excelProcessorResult.getErrors().isEmpty()) {

                ExcelHighlighter.highlightErrors(
                        workbook,
                        excelProcessorResult.getErrors(),
                        excelProcessorResult.getStructureInfo()
                );

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                workbook.write(out);

                return ValidationResponse.builder()
                        .success(false)
                        .message("Errors found in the file. Please check the highlighted cells.")
                        .fileBytes(out.toByteArray())
                        .build();
            }
            System.out.println("hey there no errors");
            // If no errors → save to DB
            //saveToDatabase(validRows);

            return ValidationResponse.builder()
                    .success(true)
                    .message("File validated and data saved successfully")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }

    private void saveToDatabase(List<RowData> rows) {
        // call repository.saveAll(rows);
    }
}

