package com.elgar.walletsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reconciliation_summary")
@Data
public class ReconciliationSummary {
    @Id
    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "matched_count", nullable = false)
    private Integer matchedCount;

    @Column(name = "missing_internal", nullable = false)
    private Integer missingInternal;

    @Column(name = "missing_external", nullable = false)
    private Integer missingExternal;

    @Column(name = "amount_mismatch", nullable = false)
    private Integer amountMismatch;

    @Column(name = "net_difference", precision = 19, scale = 2, nullable = false)
    private BigDecimal netDifference;
}
