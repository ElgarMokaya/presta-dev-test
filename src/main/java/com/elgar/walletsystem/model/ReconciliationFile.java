package com.elgar.walletsystem.model;

import com.elgar.walletsystem.enums.FileSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="reconciliation_file")
@Data
public class ReconciliationFile extends BaseEntity {
    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;
    @Column(name = "filename", nullable = false, length = 200)
    private  String fileName;
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private FileSource fileSource;
    @Column(name = "ingested_at", nullable = false)
    private Instant ingestedAt;
}
