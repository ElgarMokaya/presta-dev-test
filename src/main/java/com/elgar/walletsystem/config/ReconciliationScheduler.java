package com.elgar.walletsystem.config;

import com.elgar.walletsystem.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class ReconciliationScheduler {
    private final ReconciliationService reconciliationService;


    @Scheduled(cron = "0 5 0 * * *", zone = "UTC")
    public void runDailyReconciliation() {
        LocalDate businessDate = LocalDate.now(ZoneOffset.UTC).minusDays(1);

        System.out.println("▶ Running reconciliation for date: " + businessDate);

        reconciliationService.generateReport(businessDate);
    }
}
