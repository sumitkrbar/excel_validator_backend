package com.sumit.excelvalidator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class CellError {
    private final Integer row;
    private final String field;
    private final String message;
}
