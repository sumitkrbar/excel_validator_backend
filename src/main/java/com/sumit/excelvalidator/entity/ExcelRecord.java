package com.sumit.excelvalidator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "excel_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRecord {

    @Id
    @SequenceGenerator(
            name = "record_seq",
            sequenceName = "record_seq",
            allocationSize = 50
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "record_seq")
    private Long id;

    private String seekerName;
    private String seekerPhone;
    private String seekerEmail;
    private String providerName;
    private String providerEmail;
    private String relationshipWithSeeker;
    private Boolean isFamilyRelated;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private UploadedFile file;
}
