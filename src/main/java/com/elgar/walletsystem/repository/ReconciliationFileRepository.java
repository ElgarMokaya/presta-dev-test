package com.elgar.walletsystem.repository;

import com.elgar.walletsystem.model.ReconciliationFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ReconciliationFileRepository extends JpaRepository<ReconciliationFile, UUID> {
    Optional<ReconciliationFile> findByBusinessDate(LocalDate businessDate);

}
