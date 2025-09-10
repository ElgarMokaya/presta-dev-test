package com.elgar.walletsystem.service;

import com.elgar.walletsystem.dto.response.PagedResponse;
import com.elgar.walletsystem.dto.response.ReconciliationItemResponse;
import com.elgar.walletsystem.dto.response.ReconciliationSummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface ReconciliationService {
    void ingestFile(LocalDate businessDate, String source, MultipartFile file);

    ReconciliationSummaryResponse generateReport(LocalDate businessDate);

    PagedResponse<ReconciliationItemResponse> listItems(LocalDate businessDate, Pageable pageable);

    byte[] exportCsv(LocalDate businessDate);
}
