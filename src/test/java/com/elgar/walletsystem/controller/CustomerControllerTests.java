package com.elgar.walletsystem.controller;

import com.elgar.walletsystem.dto.request.CustomerRequest;
import com.elgar.walletsystem.dto.response.CustomerResponse;
import com.elgar.walletsystem.dto.response.WalletResponse;
import com.elgar.walletsystem.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@WebMvcTest(CustomerController.class)
public class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerRequest request = new CustomerRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");

        CustomerResponse response = CustomerResponse.builder()
                .id(customerId)
                .name(request.getName())
                .email(request.getEmail())
                .build();

        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }



    @Test
    void createWallet_ShouldReturnCreatedWallet() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();

        WalletResponse response = WalletResponse.builder()
                .id(walletId)
                .customerId(customerId)
                .balance(BigDecimal.ZERO)
                .build();

        when(customerService.createWallet(customerId)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/customers/{customerId}/wallets", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(walletId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.balance").value(0));
    }


}
