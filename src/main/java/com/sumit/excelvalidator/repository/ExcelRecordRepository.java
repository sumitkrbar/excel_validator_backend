package com.sumit.excelvalidator.repository;

import com.sumit.excelvalidator.entity.ExcelRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcelRecordRepository extends JpaRepository<ExcelRecord, Long> {
}
