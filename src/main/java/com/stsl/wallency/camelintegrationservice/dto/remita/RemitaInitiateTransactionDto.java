package com.stsl.wallency.camelintegrationservice.dto.remita;


import com.stsl.wallency.camelintegrationservice.dto.remita.transaction.Metadata;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RemitaInitiateTransactionDto {

    private String billPaymentProductId;

    private BigDecimal amount;

    private String transactionRef;

    private String name;

    private String email;

    private String phoneNumber;

    private String customerId;

    private Metadata metadata;

}

