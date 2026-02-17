package com.sumit.excelvalidator.controller;

import com.sumit.excelvalidator.dto.ValidationResponse;
import com.sumit.excelvalidator.service.ExcelValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private ExcelValidationService service;

//    @PostMapping("/validate")
//    public ResponseEntity<Long> validateFile(
//            @RequestParam("file") MultipartFile file) throws Exception {
//
//        Long id = service.validateAndStore(file);
//
//        return ResponseEntity.ok(id);
//    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        logger.info("File upload request received: filename={}, size={} bytes",
                    file.getOriginalFilename(), file.getSize());

        ValidationResponse response = service.validateExcelFile(file);

        return ResponseEntity.ok(response);
    }


//    @GetMapping("/download/{id}")
//    public ResponseEntity<byte[]> downloadFile(
//            @PathVariable Long id) {
//
//        byte[] data = service.getValidatedFile(id);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=validated.xlsx")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(data);
//    }
}
