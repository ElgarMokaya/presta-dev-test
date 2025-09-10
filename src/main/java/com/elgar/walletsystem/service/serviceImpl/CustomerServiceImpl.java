package com.elgar.walletsystem.service.serviceImpl;

import com.elgar.walletsystem.exception.CustomExceptionHandler;
import com.elgar.walletsystem.dto.request.CustomerRequest;
import com.elgar.walletsystem.dto.response.CustomerResponse;
import com.elgar.walletsystem.dto.response.WalletResponse;
import com.elgar.walletsystem.model.Customer;
import com.elgar.walletsystem.model.Wallet;
import com.elgar.walletsystem.repository.CustomerRepository;
import com.elgar.walletsystem.repository.WalletRepository;
import com.elgar.walletsystem.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
     private final CustomerRepository customerRepository;
    private final WalletRepository walletRepository;

     @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setCreatedAt(Instant.now());

        Customer saved = customerRepository.save(customer);

        return CustomerResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .build();
    }
    @Override
    public WalletResponse createWallet(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomExceptionHandler.WalletNotFoundException("Customer not found: " + customerId));

        Wallet wallet = new Wallet();
        wallet.setCustomer(customer);
        wallet.setBalance(BigDecimal.ZERO);

        Wallet saved = walletRepository.save(wallet);

        return WalletResponse.builder()
                .id(saved.getId())
                .customerId(customerId)
                .balance(saved.getBalance())
                .build();
    }

}
