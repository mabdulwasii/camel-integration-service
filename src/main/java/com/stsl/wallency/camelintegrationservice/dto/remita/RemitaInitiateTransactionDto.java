package com.stsl.wallency.camelintegrationservice.dto.remita;

import java.math.BigDecimal;
import java.util.List;

public class RemitaInitiateTransactionDto {

    private String billPaymentProductId;

    private BigDecimal amount;

    private String transactionRef;

    private String name;

    private String email;

    private String phoneNumber;

    private String customerId;

    private List<CustomFields> metadata;

    private class CustomFields {
        private String variable_name;

        private String value;
    }
}

