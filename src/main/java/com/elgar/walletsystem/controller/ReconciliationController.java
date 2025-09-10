package com.elgar.walletsystem.controller;

import com.elgar.walletsystem.dto.response.PagedResponse;
import com.elgar.walletsystem.dto.response.ReconciliationItemResponse;
import com.elgar.walletsystem.dto.response.ReconciliationSummaryResponse;
import com.elgar.walletsystem.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/reconciliation")
@RequiredArgsConstructor
public class ReconciliationController {
    private final ReconciliationService service;

    // Upload file
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("businessDate") String businessDate,
                                             @RequestParam("source") String source,
                                             @RequestParam("file") MultipartFile file) {
        service.ingestFile(LocalDate.parse(businessDate), source, file);
        return ResponseEntity.ok("File uploaded successfully");
    }

    // Generate reconciliation report
    @PostMapping("/report")
    public ResponseEntity<ReconciliationSummaryResponse> generateReport(@RequestParam("date") String date) {
        ReconciliationSummaryResponse summary = service.generateReport(LocalDate.parse(date));
        return ResponseEntity.ok(summary);
    }

    // List reconciliation items with pagination
    @GetMapping("/items")
    public ResponseEntity<PagedResponse<ReconciliationItemResponse>> listItems(
            @RequestParam("date") String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(service.listItems(LocalDate.parse(date), PageRequest.of(page, size)));
    }

    // Export reconciliation report as CSV
    @GetMapping("/report/export")
    public ResponseEntity<byte[]> exportCsv(@RequestParam("date") String date) {
        byte[] csv = service.exportCsv(LocalDate.parse(date));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reconciliation.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}