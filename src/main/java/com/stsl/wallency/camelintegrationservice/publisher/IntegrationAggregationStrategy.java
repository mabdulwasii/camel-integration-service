package com.stsl.wallency.camelintegrationservice.publisher;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Map;


public class IntegrationAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
//        // this is just an example, for real-world use-cases the
//        // aggregation strategy would be specific to the use-case

        if (newExchange == null) {
            return oldExchange;
        }
        Object oldBody = oldExchange.getIn().getBody(Object.class);
        Map<String, Object> oldHeader = oldExchange.getIn().getHeaders();

        Object newBody = newExchange.getIn().getBody();
        Map<String, Object> newHeader = newExchange.getIn().getHeaders();

        ArrayList<Object> listBody = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(oldBody)) {
            listBody.add(oldBody);
        }
        listBody.add(newBody);
        oldExchange.getIn().setBody(listBody);

        oldHeader.putAll(newHeader);
        oldExchange.getIn().setHeaders(oldHeader);

        return oldExchange;
    }

}