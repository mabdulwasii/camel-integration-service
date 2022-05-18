package com.stsl.wallency.camelintegrationservice.publisher;

import com.stsl.wallency.camelintegrationservice.dto.BaseResponse;
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

        Object newBody = newExchange.getIn().getBody();
        Map<String, Object> newHeader = newExchange.getIn().getHeaders();
        ArrayList<Object> list;
        if (oldExchange == null) {
            list = new ArrayList<>();
            list.add(newBody);
            newExchange.getIn().setBody(list);
            return newExchange;
        } else {
            if (newBody instanceof BaseResponse) {
                oldExchange.getIn().setBody(newBody);
            } else {
                list = oldExchange.getIn().getBody(ArrayList.class);
                list.add(newBody);
                Map<String, Object> oldHeader = oldExchange.getIn().getHeaders();
                oldHeader.putAll(newHeader);
                oldExchange.getIn().setBody(list);
                oldExchange.getIn().setHeaders(oldHeader);
            }
            return oldExchange;
        }
    }

}