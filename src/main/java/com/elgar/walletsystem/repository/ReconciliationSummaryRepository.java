package com.elgar.walletsystem.repository;

import com.elgar.walletsystem.model.ReconciliationSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ReconciliationSummaryRepository extends JpaRepository<ReconciliationSummary, LocalDate> {
}
