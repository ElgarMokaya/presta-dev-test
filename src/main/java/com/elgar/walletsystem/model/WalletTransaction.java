package com.elgar.walletsystem.model;

import com.elgar.walletsystem.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

    @EqualsAndHashCode(callSuper = true)
    @Entity
    @Table(
            name = "wallet_transaction",
            uniqueConstraints = {
                    @UniqueConstraint(columnNames = {"wallet_id", "client_txn_id"})
            }
    )
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public class WalletTransaction extends BaseEntity {

         @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "wallet_id", nullable = false, columnDefinition = "uuid")
        private Wallet wallet;

         @Enumerated(EnumType.STRING)
         private TransactionType transactionType;
        @Column(nullable = false, precision = 19, scale = 2)
         private BigDecimal amount;
        @Column(nullable = false, precision = 19, scale = 2)
         private BigDecimal  balanceBefore;
        @Column(nullable = false, precision = 19, scale = 2)
         private BigDecimal  balanceAfter;
        @Column(name = "client_txn_id", nullable = false, length = 50)
        private String clientTxnId;
        @Column(name = "external_ref", length = 50)
        private String externalRef;


    }
