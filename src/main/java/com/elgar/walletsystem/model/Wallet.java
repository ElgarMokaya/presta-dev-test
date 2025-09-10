package com.elgar.walletsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name="wallet")
public class Wallet extends BaseEntity {

    @ManyToOne(optional = false,fetch= FetchType.LAZY)
    @JoinColumn(name = "customer_id",nullable = false,columnDefinition = "uuid")
    private  Customer customer;

    @Column(nullable = false,precision=19,scale=2)
    private BigDecimal balance=BigDecimal.ZERO;


    @Version
    private Integer version;


    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

}
