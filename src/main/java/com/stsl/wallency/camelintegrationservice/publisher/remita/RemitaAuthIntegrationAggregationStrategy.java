package com.stsl.wallency.camelintegrationservice.publisher.remita;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.Map;


public class RemitaAuthIntegrationAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // this is just an example, for real-world use-cases the
        // aggregation strategy would be specific to the use-case

        if (newExchange == null) {
            return oldExchange;
        }
        Map<String, Object> oldHeader = oldExchange.getIn().getHeaders();
        String billerId = (String) oldHeader.get("billerId");
//        Map<String, Object> newHeader = newExchange.getIn().getHeaders();
//        String accessToken = (String) newHeader.get("accessToken");
        Map<String, Object> newHeaders = Map.of("billerId", billerId);
        oldExchange.getIn().setHeaders(newHeaders);
        return oldExchange;
    }


//
//    Object newBody = newExchange.getIn().getBody();
//    ArrayList<Object> list = null;
//        if (oldExchange == null) {
//        list = new ArrayList<Object>();
//        list.add(newBody);
//        newExchange.getIn().setBody(list);
//        return newExchange;
//    } else {
//        list = oldExchange.getIn().getBody(ArrayList.class);
//        list.add(newBody);
//        return oldExchange;
//    }
}