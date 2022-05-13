package com.stsl.wallency.camelintegrationservice.dto.remita;

import lombok.Data;
import org.springframework.stereotype.Component;

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

    private Metadata metadata;


}
@Data
@Component
class Metadata {
    private List<CustomFields> customFields;
}

@Data
@Component
class CustomFields {
    private String variable_name;

    private String value;
}
