package com.elgar.walletsystem.controller;

import com.elgar.walletsystem.dto.request.ConsumeRequest;
import com.elgar.walletsystem.dto.request.TopUpRequest;
import com.elgar.walletsystem.dto.response.BalanceResponse;
import com.elgar.walletsystem.dto.response.PagedResponse;
import com.elgar.walletsystem.dto.response.TransactionResponse;
import com.elgar.walletsystem.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("wallets")
public class WalletController {
    private final WalletService walletService;
    @PostMapping("/{walletId}/topup")
    public ResponseEntity<TransactionResponse>topUp(@PathVariable("walletId") UUID walletId, @RequestHeader("Idempotency-Key") String idempotencyKey, @Valid @RequestBody TopUpRequest topUpRequest) {
 var response=walletService.topUp(walletId,topUpRequest,idempotencyKey);
 return ResponseEntity.ok(response);
    }

    @PostMapping("/{walletId}/consume")
    public ResponseEntity<TransactionResponse>consume(@PathVariable("walletId") UUID walletId,
                                                      @RequestHeader("Idempotency-Key") String idempotencyKey,
                                                      @Valid @RequestBody ConsumeRequest request) {

        var response = walletService.consume(walletId, request, idempotencyKey);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable("walletId") UUID walletId) {
        BalanceResponse resp = walletService.getBalance(walletId);
        return ResponseEntity.ok(resp);
    }

    // GET /api/v1/wallets/{walletId}/transactions?page=0&size=20
    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<PagedResponse<TransactionResponse>> listTransactions(
            @PathVariable("walletId") UUID walletId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<TransactionResponse> paged = walletService.listTransactions(walletId, pageable);
        return ResponseEntity.ok(paged);
    }

    }