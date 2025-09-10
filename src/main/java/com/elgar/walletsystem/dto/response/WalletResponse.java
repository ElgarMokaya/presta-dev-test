package com.elgar.walletsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class WalletResponse {
    private UUID id;
    private UUID customerId;
    private BigDecimal balance;
}
