package com.elgar.walletsystem.service.ServiceImpl;

import com.elgar.walletsystem.dto.request.CustomerRequest;
import com.elgar.walletsystem.dto.response.CustomerResponse;
import com.elgar.walletsystem.dto.response.WalletResponse;
import com.elgar.walletsystem.exception.CustomExceptionHandler;
import com.elgar.walletsystem.model.Customer;
import com.elgar.walletsystem.model.Wallet;
import com.elgar.walletsystem.repository.CustomerRepository;
import com.elgar.walletsystem.repository.WalletRepository;
import com.elgar.walletsystem.service.serviceImpl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequest customerRequest;

    private Customer customer;

    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        customerRequest = new CustomerRequest();
        customerRequest.setName("John Doe");
        customerRequest.setEmail("john.doe@example.com");

        customer = new Customer();
        customer.setId(customerId);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setCreatedAt(Instant.now());
    }

    @Test
    void createCustomer_Success() {

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);


        CustomerResponse response = customerService.createCustomer(customerRequest);

        assertNotNull(response);
        assertEquals(customerId, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("john.doe@example.com", response.getEmail());


        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void createWallet_Success() {

        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCustomer(customer);


        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));


        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);


        WalletResponse response = customerService.createWallet(customerId);

        // Assertions to verify the response
        assertNotNull(response);
        assertEquals(walletId, response.getId());
        assertEquals(customerId, response.getCustomerId());
        assertEquals(BigDecimal.ZERO, response.getBalance());

        // Verify repository interactions
        verify(customerRepository, times(1)).findById(customerId);
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void createWallet_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());


        CustomExceptionHandler.WalletNotFoundException thrown = assertThrows(
            CustomExceptionHandler.WalletNotFoundException.class,
            () -> customerService.createWallet(customerId)
        );


        assertEquals("Customer not found: " + customerId, thrown.getMessage());

        // Verify repository interactions
        verify(customerRepository, times(1)).findById(customerId);
        // Ensure walletRepository.save() was never called because the customer was not found
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}

