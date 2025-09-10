package com.elgar.walletsystem.service;

import com.elgar.walletsystem.dto.request.ConsumeRequest;
import com.elgar.walletsystem.dto.request.TopUpRequest;
import com.elgar.walletsystem.dto.response.BalanceResponse;
import com.elgar.walletsystem.dto.response.PagedResponse;
import com.elgar.walletsystem.dto.response.TransactionResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WalletService {
    TransactionResponse topUp(UUID walletId, TopUpRequest topUpRequest, String idempotencyKey);
    TransactionResponse consume(UUID walletId, ConsumeRequest consumeRequest, String idempotencyKey);
    BalanceResponse getBalance(UUID walletId);
    PagedResponse<TransactionResponse> listTransactions(UUID walletId, Pageable pageable);
}
