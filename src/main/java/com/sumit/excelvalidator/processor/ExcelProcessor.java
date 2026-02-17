package com.sumit.excelvalidator.processor;

import com.sumit.excelvalidator.dto.CellError;
import com.sumit.excelvalidator.dto.ExcelProcessorResult;
import com.sumit.excelvalidator.dto.RowData;
import com.sumit.excelvalidator.dto.StructureInfo;
import com.sumit.excelvalidator.reader.ExcelReader;
import com.sumit.excelvalidator.validator.ColumnValidator;
import com.sumit.excelvalidator.validator.DataValidator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExcelProcessor {

    public ExcelProcessorResult processWorkbook(Workbook workbook) {
        try {
            Sheet sheet = workbook.getSheetAt(0);

            // ✅ ColumnValidator.validateColumns() already throws IllegalArgumentException
            // if headerRowIndex == -1, so no need to check again
            StructureInfo structureInfo = ColumnValidator.validateColumns(sheet);

            ExcelReader reader = new ExcelReader();
            List<RowData> records = reader.read(sheet, structureInfo);

            DataValidator dataValidator = new DataValidator();
            List<CellError> errors = dataValidator.validate(records);

            return new ExcelProcessorResult(records, errors, structureInfo);

        } catch (IllegalArgumentException e) {
            // ✅ Re-throw IllegalArgumentException as-is
            throw e;
        } catch (Exception e) {
            // ✅ Provide better error context
            throw new RuntimeException("Error processing workbook: " + e.getMessage(), e);
        }
    }
}
