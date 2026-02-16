package com.sumit.excelvalidator.repository;

import com.sumit.excelvalidator.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
}
