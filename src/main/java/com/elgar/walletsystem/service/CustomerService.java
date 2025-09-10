package com.elgar.walletsystem.service;

import com.elgar.walletsystem.dto.request.CustomerRequest;
import com.elgar.walletsystem.dto.response.CustomerResponse;
import com.elgar.walletsystem.dto.response.WalletResponse;

import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    WalletResponse createWallet(UUID customerId);
}
