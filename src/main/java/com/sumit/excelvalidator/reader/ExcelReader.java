package com.sumit.excelvalidator.reader;

import com.sumit.excelvalidator.dto.RowData;
import com.sumit.excelvalidator.dto.StructureInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    public List<RowData> read(Sheet sheet, StructureInfo info) throws Exception {

        List<RowData> records = new ArrayList<>();
        int startRow = info.getHeaderRowIndex() + 1;
        Map<String, Integer> col = info.getColumnIndexMap();

        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            if (row == null || isRowEmpty(row)) continue;
            //if (isJunkRow(row, col)) continue;

            RowData data = new RowData(
                    i,
                    getString(row, col.get("Seeker Name")),
                    getString(row, col.get("Seeker Phone no.")),
                    getString(row, col.get("Seeker Email")),
                    getString(row, col.get("Provider Name")),
                    getString(row, col.get("Provider Email")),
                    getString(row, col.get("Relationship with Seeker")),
                    parseBoolean(getString(row, col.get("is Family Related")))
            );
            //System.out.println(data.getRowIndex());
            records.add(data);
        }
        return records;
    }


    // helper func

    private String getString(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell.toString().trim();
    }

    private Boolean parseBoolean(String value) {
        if (value == null) return null;

        value = value.trim().toLowerCase();

        if (value.equals("true")) return true;
        if (value.equals("false")) return false;

        return null;
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }

    // skips note/merged/middle junk rows
//    private boolean isJunkRow(Row row, Map<String, Integer> col) {
//
//        String name = getString(row, col.get("Seeker Name"));
//        String email = getString(row, col.get("Seeker Email"));
//
//        // required columns missing -> not real data
//        return name.isBlank() || email.isBlank();
//    }
}