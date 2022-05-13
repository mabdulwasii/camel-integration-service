package com.stsl.wallency.camelintegrationservice.dto.remita;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Embedded;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RemitaInitiateTransactionDto {

    private String billPaymentProductId;

    private BigDecimal amount;

    private String transactionRef;

    private String name;

    private String email;

    private String phoneNumber;

    private String customerId;

    private String metadata ;

}

