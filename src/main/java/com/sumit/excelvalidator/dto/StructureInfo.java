package com.sumit.excelvalidator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;
@RequiredArgsConstructor
@Getter
@Setter
public class StructureInfo {
    private final int headerRowIndex;
    private final Map<String, Integer>  columnIndexMap;
}
