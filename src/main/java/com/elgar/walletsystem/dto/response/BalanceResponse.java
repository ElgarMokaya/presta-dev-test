package com.elgar.walletsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class BalanceResponse {
    private UUID walletId;
    private BigDecimal balance;
}
