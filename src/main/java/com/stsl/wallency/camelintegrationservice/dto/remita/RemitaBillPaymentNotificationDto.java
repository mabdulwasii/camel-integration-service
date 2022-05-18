package com.stsl.wallency.camelintegrationservice.dto.remita;

import com.stsl.wallency.camelintegrationservice.dto.remita.bill.MetaData;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class RemitaBillPaymentNotificationDto {

    private String rrr;

    private String transactionRef;

    private BigDecimal amount;

    private String channel;

    private MetaData metadata;



}
