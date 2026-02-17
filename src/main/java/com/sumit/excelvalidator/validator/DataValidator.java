package com.sumit.excelvalidator.validator;

import com.sumit.excelvalidator.dto.CellError;
import com.sumit.excelvalidator.dto.RowData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.*;

public class DataValidator {

    private final Validator validator;

    public DataValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }


    public List<CellError> validate(List<RowData> records){
        List<CellError> errors = new ArrayList<>();

        Map<String, Integer> seekerEmailMap = new HashMap<>();
        Map<String, Integer> providerEmailMap = new HashMap<>();
        Map<String, Integer> seekerPhoneMap = new HashMap<>();

        for(RowData data: records){

            int rowIndex = data.getRowIndex();

            boolean seekerEmailValid = true;
            boolean providerEmailValid = true;
            boolean seekerPhoneValid = true;

            Set<ConstraintViolation<RowData>> violations = validator.validate(data);

            for(ConstraintViolation<RowData> v : violations){
                String fieldName = v.getPropertyPath().toString();

                if(fieldName.equals("seekerEmail")) seekerEmailValid = false;
                if(fieldName.equals("providerEmail")) providerEmailValid = false;
                if(fieldName.equals("phone")) seekerPhoneValid = false;

                errors.add(new CellError(
                        data.getRowIndex(),
                        v.getPropertyPath().toString(),
                        v.getMessage()
                ));
                //System.out.println(v.getPropertyPath().toString());
            }
            if(seekerEmailValid){
                String email = normalize(data.getSeekerEmail());

                if(email != null){
                    if(seekerEmailMap.containsKey(email)){
                        int firstRow = seekerEmailMap.get(email);
                        errors.add(new CellError(rowIndex, "seekerEmail", "Duplicate seeker email"));
                    } else {
                        seekerEmailMap.put(email, rowIndex);
                    }
                }
            }

            // 3️⃣ Provider Email uniqueness
            if(providerEmailValid){
                String email = normalize(data.getProviderEmail());

                if(email != null){
                    if(providerEmailMap.containsKey(email)){
                        int firstRow = providerEmailMap.get(email);
                        errors.add(new CellError(rowIndex, "providerEmail", "Duplicate provider email"));
                    } else {
                        providerEmailMap.put(email, rowIndex);
                    }
                }
            }

            // 4️⃣ Phone uniqueness
            if(seekerPhoneValid){
                String phone = normalize(data.getSeekerPhone());

                if(phone != null){
                    if(seekerPhoneMap.containsKey(phone)){
                        int firstRow = seekerPhoneMap.get(phone);
                        errors.add(new CellError(rowIndex, "seekerPhone", "Duplicate phone"));
                    } else {
                        seekerPhoneMap.put(phone, rowIndex);
                    }
                }
            }
        }
        return errors;
    }
    private String normalize(String value){
        if(value == null) return null;

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase();
    }

}

