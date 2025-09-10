package com.elgar.walletsystem.controller;

import com.elgar.walletsystem.dto.request.CustomerRequest;
import com.elgar.walletsystem.dto.response.CustomerResponse;
import com.elgar.walletsystem.dto.response.WalletResponse;
import com.elgar.walletsystem.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PostMapping("/{customerId}/wallets")
    public ResponseEntity<WalletResponse> createWallet(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerService.createWallet(customerId));
    }
}
