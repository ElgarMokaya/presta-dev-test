package com.elgar.walletsystem.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

@Data
@Builder
public class TopUpRequest {
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be positive")
    private BigDecimal amount;
    private String note;
}
