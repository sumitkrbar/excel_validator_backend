package com.sumit.excelvalidator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "uploaded_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
