package com.sumit.excelvalidator.service;

import com.sumit.excelvalidator.dto.CellError;
import com.sumit.excelvalidator.dto.ExcelProcessorResult;
import com.sumit.excelvalidator.dto.RowData;
import com.sumit.excelvalidator.dto.ValidationResponse;
import com.sumit.excelvalidator.entity.ExcelRecord;
import com.sumit.excelvalidator.entity.UploadedFile;
import com.sumit.excelvalidator.entity.User;
import com.sumit.excelvalidator.processor.ExcelProcessor;
import com.sumit.excelvalidator.repository.ExcelRecordRepository;
import com.sumit.excelvalidator.repository.UploadedFileRepository;
import com.sumit.excelvalidator.repository.UserRepository;
import com.sumit.excelvalidator.validator.ExcelFileValidator;
import com.sumit.excelvalidator.writer.ExcelHighlighter;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ExcelValidationService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelValidationService.class);

    private final UserRepository userRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final ExcelRecordRepository excelRecordRepository;
    private final ExcelFileValidator excelFileValidator;
    private final ExcelProcessor excelProcessor;
    private final ExcelHighlighter excelHighlighter;

    public ExcelValidationService(
            UserRepository userRepository,
            UploadedFileRepository uploadedFileRepository,
            ExcelRecordRepository excelRecordRepository,
            ExcelFileValidator excelFileValidator,
            ExcelProcessor excelProcessor,
            ExcelHighlighter excelHighlighter
    ) {
        this.userRepository = userRepository;
        this.uploadedFileRepository = uploadedFileRepository;
        this.excelRecordRepository = excelRecordRepository;
        this.excelFileValidator = excelFileValidator;
        this.excelProcessor = excelProcessor;
        this.excelHighlighter = excelHighlighter;
    }

    public ValidationResponse validateExcelFile(MultipartFile file) {
        logger.debug("Starting validation for file: {}", file.getOriginalFilename());

        try (Workbook workbook = excelFileValidator.getValidatedWorkbook(file)) {
            logger.debug("File loaded and validated successfully");

            ExcelProcessorResult excelProcessorResult = excelProcessor.processWorkbook(workbook);
            logger.debug("Workbook processed. Found {} errors", excelProcessorResult.getErrors().size());

            //If errors exist >>> highlight + return file
            if (!excelProcessorResult.getErrors().isEmpty()) {
                logger.warn("Validation errors found for file: {}. Error count: {}",
                           file.getOriginalFilename(), excelProcessorResult.getErrors().size());

                excelHighlighter.highlightErrors(
                        workbook,
                        excelProcessorResult.getErrors(),
                        excelProcessorResult.getStructureInfo()
                );

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                workbook.write(out);

                logger.debug("Error file generated with highlighted cells");
                return ValidationResponse.builder()
                        .success(false)
                        .message("Errors found in the file. Please check the highlighted cells.")
                        .fileBytes(out.toByteArray())
                        .build();
            }

            // If no errors >> save to DB
            logger.info("No validation errors found. Proceeding to save data to database");
            List<RowData> validRows = excelProcessorResult.getRecords();
            saveToDatabase(validRows, file);

            logger.info("File validation and data storage completed successfully for: {}", file.getOriginalFilename());
            return ValidationResponse.builder()
                    .success(true)
                    .message("File validated and data saved successfully")
                    .build();

        } catch (IllegalArgumentException e) {
            logger.error("Validation error for file {}: {}", file.getOriginalFilename(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during file validation: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Validation failed: " + e.getMessage(), e);
        }
    }

    private void saveToDatabase(List<RowData> rows, MultipartFile file) {
        logger.debug("Saving {} records to database", rows.size());

        // Get logged-in user
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Save UploadedFile first
        UploadedFile uploadedFile = UploadedFile.builder()
                .fileName(file.getOriginalFilename())
                .uploadedAt(LocalDateTime.now())
                .user(user)
                .build();

        uploadedFile = uploadedFileRepository.save(uploadedFile);

        //Convert RowData -> ExcelRecord
        UploadedFile finalUploadedFile = uploadedFile;
        List<ExcelRecord> records = rows.stream()
                .map(row -> ExcelRecord.builder()
                        .seekerName(row.getSeekerName())
                        .seekerPhone(row.getSeekerPhone())
                        .seekerEmail(row.getSeekerEmail())
                        .providerName(row.getProviderName())
                        .providerEmail(row.getProviderEmail())
                        .relationshipWithSeeker(row.getRelationshipWithSeeker())
                        .isFamilyRelated(row.getIsFamilyRelated())
                        .file(finalUploadedFile)
                        .build())
                .toList();

        //Batch Insert
        excelRecordRepository.saveAll(records);
        logger.info("Successfully saved {} records to database for file: {}", records.size(), file.getOriginalFilename());
    }

}
