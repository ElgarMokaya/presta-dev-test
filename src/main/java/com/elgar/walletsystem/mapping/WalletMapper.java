package com.elgar.walletsystem.mapping;

import com.elgar.walletsystem.dto.response.TransactionResponse;
import com.elgar.walletsystem.model.WalletTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface WalletMapper {
    @Mapping(target ="walletId",source="wallet.id")
    TransactionResponse toTransactionResponse(WalletTransaction walletTransaction);
}
