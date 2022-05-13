package com.stsl.wallency.camelintegrationservice.publisher;

import org.apache.camel.ProducerTemplate;

public class IntegrationPublisher {

    private final ProducerTemplate producerTemplate;

    public IntegrationPublisher(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }



}
