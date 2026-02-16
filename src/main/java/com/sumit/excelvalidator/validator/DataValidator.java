package com.sumit.excelvalidator.validator;

import com.sumit.excelvalidator.dto.CellError;
import com.sumit.excelvalidator.dto.RowData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataValidator {

    private final Validator validator;

    public DataValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }


    public List<CellError> validate(List<RowData> records){
        List<CellError> errors = new ArrayList<>();
        for(RowData data: records){
            Set<ConstraintViolation<RowData>> violations = validator.validate(data);

            for(ConstraintViolation<RowData> v : violations){
                errors.add(new CellError(
                        data.getRowIndex(),
                        v.getPropertyPath().toString(),
                        v.getMessage()
                ));
                //System.out.println(v.getPropertyPath().toString());
            }
        }
        return errors;
    }
}

