package com.sumit.excelvalidator.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RowData {

    @NotNull
    private Integer rowIndex;

    @NotBlank
    @Pattern(regexp= "^[a-zA-Z0-9 ]+$", message = " Name should be alphanumeric; must not contain any special characters")
    private String seekerName;

    @NotBlank
    @Pattern(regexp = "^\\+(91|44|1|33|49)\\s?[0-9]{10}$", message = "Phone must include valid country code (+91, +44, +1, +33, +49) followed by 10 digits")
    private String seekerPhone;

    @NotBlank
    @Pattern(
            regexp = "^[A-Za-z0-9][A-Za-z0-9._+-]*[A-Za-z0-9]@[A-Za-z0-9][A-Za-z0-9.-]*[A-Za-z0-9]\\.[A-Za-z]{2,6}$",
            message = "Invalid email"
    )
    private String seekerEmail;

    @NotBlank
    @Pattern(regexp= "^[a-zA-Z0-9 ]+$", message = " Name should be alphanumeric; must not contain any special characters")
    private String providerName;

    @NotBlank
    @Pattern(
            regexp = "^[A-Za-z0-9][A-Za-z0-9._+-]*[A-Za-z0-9]@[A-Za-z0-9][A-Za-z0-9.-]*[A-Za-z0-9]\\.[A-Za-z]{2,6}$",
            message = "Invalid email"
    )
    private String providerEmail;

    @NotBlank
    @Pattern(regexp = "^(?i)(Peer|Manager|Direct report|Customer)$", message = "Choose between Peer, Manager, Direct Report, Customer")
    private String relationshipWithSeeker;

    @NotNull (message = "Must choose between TRUE or FALSE")
    private Boolean isFamilyRelated;

    public RowData(int i, String seekerName, String string, String seekerEmail, String providerName, String providerEmail, String relationshipWithSeeker, Boolean isFamilyRelated) {
        this.rowIndex = i;
        this.seekerName = seekerName;
        this.seekerPhone = string;
        this.seekerEmail = seekerEmail;
        this.providerName = providerName;
        this.providerEmail = providerEmail;
        this.relationshipWithSeeker = relationshipWithSeeker;
        this.isFamilyRelated = isFamilyRelated;
    }
}
