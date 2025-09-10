package com.elgar.walletsystem.service.serviceImpl;

import com.elgar.walletsystem.enums.AggregateType;
import com.elgar.walletsystem.enums.TransactionType;
import com.elgar.walletsystem.exception.CustomExceptionHandler;
import com.elgar.walletsystem.dto.request.ConsumeRequest;
import com.elgar.walletsystem.dto.request.TopUpRequest;
import com.elgar.walletsystem.dto.response.BalanceResponse;
import com.elgar.walletsystem.dto.response.PagedResponse;
import com.elgar.walletsystem.dto.response.TransactionResponse;
import com.elgar.walletsystem.mapping.WalletMapper;
import com.elgar.walletsystem.model.OutboxEvent;
import com.elgar.walletsystem.model.Wallet;
import com.elgar.walletsystem.model.WalletTransaction;
import com.elgar.walletsystem.repository.OutboxEventRepository;
import com.elgar.walletsystem.repository.WalletRepository;
import com.elgar.walletsystem.repository.WalletTransactionRepository;
import com.elgar.walletsystem.service.WalletService;
import com.elgar.walletsystem.utils.EventPayloadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletMapper walletMapper;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Override
    @Transactional
    public TransactionResponse topUp(UUID walletId, TopUpRequest topUpRequest, String idempotencyKey) {
        if(idempotencyKey ==null || idempotencyKey.isBlank() ){
            throw new CustomExceptionHandler.BusinessRuleException("Missing idempotency key");
        }
        if(topUpRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new CustomExceptionHandler.BusinessRuleException("Amount must be greater than zero");
        }
        Wallet wallet = walletRepository.findByIdForUpdate(walletId).orElseThrow(() -> new CustomExceptionHandler.BusinessRuleException("Wallet not found: " + walletId));

        var existingOpt=walletTransactionRepository.findByWalletIdAndClientTxnId(walletId, idempotencyKey);
        if(existingOpt.isPresent()){
            return  walletMapper.toTransactionResponse(existingOpt.get());

        }
        //update wallet current balance
         var balanceBefore = wallet.getBalance();
        var balanceAfter=balanceBefore.add(topUpRequest.getAmount());
        wallet.setBalance(balanceAfter);

        //create a ledger entry
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .wallet(wallet)
                .transactionType(TransactionType.TOPUP)
                .amount(topUpRequest.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .clientTxnId(idempotencyKey)
                .externalRef(null)
                .build();
        WalletTransaction savedWalletTransaction = walletTransactionRepository.save(walletTransaction);

        TransactionResponse response = walletMapper.toTransactionResponse(savedWalletTransaction);

        //create outbox event
        OutboxEvent outboxEvent=OutboxEvent.builder()
                .aggregateId(savedWalletTransaction.getId())
                .aggregateType(AggregateType.WALLET_TRANSACTION)
                .eventType(TransactionType.TOPUP)
                .payload(EventPayloadUtil.toJsonPayload(response,objectMapper))
                .publishedAt(null)
                .build();
        outboxEventRepository.save(outboxEvent);
        return response;
    }

  @Override
@Transactional
public TransactionResponse consume(UUID walletId, ConsumeRequest consumeRequest, String idempotencyKey) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
        throw new CustomExceptionHandler.BusinessRuleException("Missing Idempotency-Key header");
    }

    if (consumeRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new CustomExceptionHandler.BusinessRuleException("Amount must be positive");
    }

    Wallet wallet = walletRepository.findByIdForUpdate(walletId)
            .orElseThrow(() -> new CustomExceptionHandler.WalletNotFoundException("Wallet not found: " + walletId));

    // Idempotency check
    var existingOpt = walletTransactionRepository.findByWalletIdAndClientTxnId(walletId, idempotencyKey);
    if (existingOpt.isPresent()) {
        return walletMapper.toTransactionResponse(existingOpt.get());
    }

    var balanceBefore = wallet.getBalance();
    if (balanceBefore.compareTo(consumeRequest.getAmount()) < 0) {
        throw new CustomExceptionHandler.InsufficientBalanceException(
            "Balance " + balanceBefore + " is less than " + consumeRequest.getAmount()
        );
    }

    var balanceAfter = balanceBefore.subtract(consumeRequest.getAmount());

    // Update wallet
    wallet.setBalance(balanceAfter);
    walletRepository.save(wallet);

    // Ledger
    WalletTransaction walletTransaction = WalletTransaction.builder()
            .wallet(wallet)
            .transactionType(TransactionType.CONSUME)
            .amount(consumeRequest.getAmount())
            .balanceBefore(balanceBefore)
            .balanceAfter(balanceAfter)
            .clientTxnId(idempotencyKey)
            .externalRef(null)
            .build();

    WalletTransaction savedTxn = walletTransactionRepository.save(walletTransaction);

    TransactionResponse response = walletMapper.toTransactionResponse(savedTxn);

    // Outbox event
    OutboxEvent outboxEvent = OutboxEvent.builder()
            .aggregateId(savedTxn.getId())
            .aggregateType(AggregateType.WALLET_TRANSACTION)
            .eventType(TransactionType.CONSUME)
            .payload(EventPayloadUtil.toJsonPayload(response, objectMapper))
            .publishedAt(null)
            .build();

    outboxEventRepository.save(outboxEvent);

    return response;
}


    @Override
    @Transactional
    public BalanceResponse getBalance(UUID walletId) {
        var wallet=walletRepository.findByIdForUpdate(walletId).orElseThrow(() -> new CustomExceptionHandler.WalletNotFoundException("Wallet not found: " + walletId));
        return BalanceResponse.builder()
                .balance(wallet.getBalance())
                .walletId(wallet.getId())
                .build();
    }

    @Override
    public PagedResponse<TransactionResponse> listTransactions(UUID walletId, Pageable pageable) {
        Page<WalletTransaction> page = walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId, pageable);
        return PagedResponse.<TransactionResponse>builder()
                .content(page.stream().map(walletMapper::toTransactionResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }


}
