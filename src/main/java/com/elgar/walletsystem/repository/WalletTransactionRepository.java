package com.elgar.walletsystem.repository;


import com.elgar.walletsystem.model.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {
    boolean existsByWalletIdAndClientTxnId(UUID walletId, String clientTxnId);
    Optional<WalletTransaction> findByWalletIdAndClientTxnId(UUID walletId, String clientTxnId);
    Page<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(UUID walletId, Pageable pageable);

}
