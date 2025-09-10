package com.elgar.walletsystem.repository;

import com.elgar.walletsystem.model.ReconciliationItem;
import com.elgar.walletsystem.enums.ReconciliationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface ReconciliationItemRepository extends JpaRepository<ReconciliationItem, UUID> {
    Page<ReconciliationItem> findByBusinessDate(LocalDate businessDate, Pageable pageable);

    long countByBusinessDateAndStatus(LocalDate businessDate, ReconciliationStatus status);
}