package com.sumit.excelvalidator.validator;

import com.sumit.excelvalidator.dto.StructureInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnValidator {
    private static final List<String> EXPECTED_HEADERS = List.of(
            "Seeker Name",
            "Seeker Phone no.",
            "Seeker Email",
            "Provider Name",
            "Provider Email",
            "Relationship with Seeker",
            "is Family Related"
    );

    public static StructureInfo validateColumns(Sheet sheet){
        int headerRowIndex = -1;
        Map<String, Integer> columnMap = new HashMap<>();
        for(int i=0; i<=sheet.getLastRowNum(); i++){
            Row row = sheet.getRow(i);
            if(row== null) continue;
            Map<String, Integer> tempMap = extractHeaderMap(row);
            if(tempMap.keySet().containsAll(EXPECTED_HEADERS)){
                headerRowIndex = i;
                columnMap = tempMap;
                break;
            }
        }

        if(headerRowIndex == -1){
            throw new IllegalArgumentException(
                "Required columns not found in the Excel file. " +
                "Expected headers: " + EXPECTED_HEADERS
            );
        }
        return new StructureInfo(headerRowIndex, columnMap);
    }
    private static Map<String, Integer> extractHeaderMap(Row row) {

        Map<String, Integer> map = new HashMap<>();

        for (Cell cell : row) {
            String value = cell.toString().trim();

            if (EXPECTED_HEADERS.contains(value)) {
                map.put(value, cell.getColumnIndex());
            }
        }

        return map;
    }
}
