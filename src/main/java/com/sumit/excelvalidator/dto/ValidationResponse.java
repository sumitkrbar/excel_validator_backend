package com.sumit.excelvalidator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValidationResponse {

    private boolean success;
    private byte[] fileBytes;   // Modified Excel (if invalid)
    private String message;

}

