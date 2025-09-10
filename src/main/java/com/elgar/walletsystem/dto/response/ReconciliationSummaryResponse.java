package com.elgar.walletsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ReconciliationSummaryResponse {
    private LocalDate businessDate;
    private int matchedCount;
    private int missingInternal;
    private int missingExternal;
    private int amountMismatch;
    private BigDecimal netDifference;
}
