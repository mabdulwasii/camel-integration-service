package com.stsl.wallency.camelintegrationservice.publisher;

import com.stsl.wallency.camelintegrationservice.CamelIntegrationServiceApplication;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;


@SpringBootTest(classes = CamelIntegrationServiceApplication.class)
class IntegrationPublisherTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    void testMocksAreValid() {
        System.out.println("Sending 1");
        Map userLogin = Map.of("username","UHSU6ZIMAVXNZHXW", "password", "K8JE73OFE508GMOW9VWLX5SLH5QG1PF2");
        producerTemplate.sendBody("direct:authenticate", userLogin);



    }

}