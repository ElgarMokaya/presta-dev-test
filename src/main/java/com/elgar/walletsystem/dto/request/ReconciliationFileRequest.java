package com.elgar.walletsystem.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class ReconciliationFileRequest {
    private LocalDate businessDate;
    private String source; // CSV or JSON
    private MultipartFile file;
}
