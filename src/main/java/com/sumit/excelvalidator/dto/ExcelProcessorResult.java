package com.sumit.excelvalidator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ExcelProcessorResult {

    private List<RowData> records;
    private List<CellError> errors;
    private StructureInfo structureInfo;

}

