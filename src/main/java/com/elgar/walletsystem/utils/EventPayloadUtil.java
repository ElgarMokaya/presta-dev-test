package com.elgar.walletsystem.utils;

import com.elgar.walletsystem.dto.response.TransactionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;

public class EventPayloadUtil {
    private EventPayloadUtil() {
    }

    public static Object toJsonPayload(TransactionResponse response, ObjectMapper mapper) {
        try {
            // Convert DTO → Map so Hibernate can persist as jsonb
            return mapper.convertValue(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (IllegalArgumentException e) {
            // fallback minimal payload
            return Map.of("transactionId", response.getId());
        }
    }
}
