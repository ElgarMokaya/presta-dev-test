package com.elgar.walletsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class CustomerResponse {
    private UUID id;
    private String name;
    private String email;
}
