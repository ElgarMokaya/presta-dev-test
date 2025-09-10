package com.elgar.walletsystem.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="customer")
@Data
public class Customer extends BaseEntity {
    private String name;
    @Column(length = 150,unique = true)
    private String email;
}
