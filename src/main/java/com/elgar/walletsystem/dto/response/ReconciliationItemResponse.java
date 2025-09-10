package com.elgar.walletsystem.dto.response;

import com.elgar.walletsystem.enums.ReconciliationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ReconciliationItemResponse {
        private UUID id;
    private LocalDate businessDate;
    private String transactionId;
    private BigDecimal internalAmount;
    private BigDecimal externalAmount;
    private ReconciliationStatus status;
}
