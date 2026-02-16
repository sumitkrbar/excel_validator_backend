package com.sumit.excelvalidator.writer;

import com.sumit.excelvalidator.dto.CellError;
import com.sumit.excelvalidator.dto.StructureInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelHighlighter {

    private static final Map<String, String> FIELD_TO_HEADER = Map.of(
            "seekerName", "Seeker Name",
            "seekerPhone", "Seeker Phone no.",
            "seekerEmail", "Seeker Email",
            "providerName", "Provider Name",
            "providerEmail", "Provider Email",
            "relationshipWithSeeker", "Relationship with Seeker",
            "isFamilyRelated", "is Family Related"
    );

    public static void highlightErrors(
            Workbook workbook,
            List<CellError> errors,
            StructureInfo info
    ) throws Exception {

        Sheet sheet = workbook.getSheetAt(0);
        Map<String, Integer> colMap = info.getColumnIndexMap();

        CellStyle redStyle = workbook.createCellStyle();
        redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Map<String, Integer> columnErrorCount = new HashMap<>();

        Drawing<?> drawing = sheet.createDrawingPatriarch();
        CreationHelper helper = workbook.getCreationHelper();

        //  mark cells
        for (CellError error : errors) {

            int rowIndex = error.getRow();
            String header = FIELD_TO_HEADER.get(error.getField());
            int colIndex = colMap.get(header);

            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            cell.setCellStyle(redStyle);

            if (cell.getCellComment() == null) {
                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(colIndex);
                anchor.setCol2(colIndex + 2);
                anchor.setRow1(rowIndex);
                anchor.setRow2(rowIndex + 2);

                Comment comment = drawing.createCellComment(anchor);
                comment.setString(helper.createRichTextString(error.getMessage()));
                cell.setCellComment(comment);
            }

            columnErrorCount.merge(error.getField(), 1, Integer::sum);
        }


        int startCol = Collections.min(colMap.values());
        int lastDataRow = Math.max(
                errors.stream().mapToInt(CellError::getRow).max().orElse(0),
                info.getHeaderRowIndex()
        );
        int rowNum = lastDataRow + 3;
//        System.out.println(startCol);
//        System.out.println(rowNum);
        Row title = sheet.createRow(rowNum++);
        title.createCell(startCol).setCellValue("Validation Summary");

        Row total = sheet.createRow(rowNum++);
        total.createCell(startCol).setCellValue("Total errors");
        total.createCell(startCol + 1).setCellValue(errors.size());

        rowNum++;

        Row head = sheet.createRow(rowNum++);
        head.createCell(startCol).setCellValue("Column");
        head.createCell(startCol + 1).setCellValue("Error count");

        for (Map.Entry<String, Integer> e : columnErrorCount.entrySet()) {
            Row r = sheet.createRow(rowNum++);
            r.createCell(startCol).setCellValue(e.getKey());
            r.createCell(startCol + 1).setCellValue(e.getValue());
        }
    }
}