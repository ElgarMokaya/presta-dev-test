package com.elgar.walletsystem.repository;

import com.elgar.walletsystem.enums.TransactionType;
import com.elgar.walletsystem.model.Customer;
import com.elgar.walletsystem.model.Wallet;
import com.elgar.walletsystem.model.WalletTransaction;
import com.elgar.walletsystem.repository.CustomerRepository;
import com.elgar.walletsystem.repository.WalletRepository;
import com.elgar.walletsystem.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
//@Rollback(true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class WalletTransactionRepositoryTests {
 @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Wallet testWallet;
    private UUID walletId;

    @BeforeEach
    void setUp() {

        walletTransactionRepository.deleteAll();
        walletRepository.deleteAll();
        customerRepository.deleteAll();


        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer = customerRepository.save(customer);


        testWallet = new Wallet();
        testWallet.setCustomer(customer);
        testWallet.setBalance(BigDecimal.ZERO);
        testWallet = walletRepository.save(testWallet);
        walletId = testWallet.getId();
    }

    @Test
    void findByWalletIdAndClientTxnId_ShouldReturnTransaction() {
        // Given
        String clientTxnId = "txn-123";
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(testWallet)
                .transactionType(TransactionType.TOPUP)
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("0.00"))
                .balanceAfter(new BigDecimal("100.00"))
                .clientTxnId(clientTxnId)
                .build();
        walletTransactionRepository.save(transaction);

        // When
        Optional<WalletTransaction> foundTransaction = walletTransactionRepository.findByWalletIdAndClientTxnId(walletId, clientTxnId);

        // Then
        assertTrue(foundTransaction.isPresent());
        assertEquals(clientTxnId, foundTransaction.get().getClientTxnId());
        assertEquals(walletId, foundTransaction.get().getWallet().getId());
    }

    @Test
    void findByWalletIdAndClientTxnId_ShouldReturnEmptyWhenNotFound() {
        // Given
        String clientTxnId = "non-existent-txn";

        // When
        Optional<WalletTransaction> foundTransaction = walletTransactionRepository.findByWalletIdAndClientTxnId(walletId, clientTxnId);

        // Then
        assertFalse(foundTransaction.isPresent());
    }

    @Test
    void findByWalletIdOrderByCreatedAtDesc_ShouldReturnPaginatedAndSortedTransactions() {
        WalletTransaction oldTransaction = WalletTransaction.builder()
                .wallet(testWallet)
                .transactionType(TransactionType.TOPUP)
                .amount(new BigDecimal("10.00"))
                .balanceBefore(new BigDecimal("0.00"))
                .balanceAfter(new BigDecimal("10.00"))
                .clientTxnId("old-txn")
                .build();
        oldTransaction.setCreatedAt(Instant.parse("2024-01-01T12:00:00Z"));
        walletTransactionRepository.save(oldTransaction);

        WalletTransaction newTransaction = WalletTransaction.builder()
                .wallet(testWallet)
                .transactionType(TransactionType.TOPUP)
                .amount(new BigDecimal("20.00"))
                .balanceBefore(new BigDecimal("10.00"))
                .balanceAfter(new BigDecimal("30.00"))
                .clientTxnId("new-txn")
                .build();
        newTransaction.setCreatedAt(Instant.parse("2024-01-01T12:05:00Z"));
        walletTransactionRepository.save(newTransaction);

        // When
        Pageable pageable = PageRequest.of(0, 10); // Requesting the first page with 10 items
        Page<WalletTransaction> transactionPage = walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId, pageable);

        // Then
        assertNotNull(transactionPage);
        assertEquals(2, transactionPage.getTotalElements());
        assertEquals(1, transactionPage.getTotalPages());

        // Assert that the transactions are sorted by createdAt in descending order
        List<WalletTransaction> transactions = transactionPage.getContent();
        assertEquals(2, transactions.size());
        assertEquals("new-txn", transactions.get(0).getClientTxnId());
        assertEquals("old-txn", transactions.get(1).getClientTxnId());
    }
}
