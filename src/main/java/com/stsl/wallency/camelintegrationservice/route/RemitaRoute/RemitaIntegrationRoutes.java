package com.stsl.wallency.camelintegrationservice.route.RemitaRoute;

import com.stsl.wallency.camelintegrationservice.Services.remita.RemitaService;
import com.stsl.wallency.camelintegrationservice.configuration.AppConfiguration;
import com.stsl.wallency.camelintegrationservice.dto.BaseResponse;
import com.stsl.wallency.camelintegrationservice.dto.remita.*;
import com.stsl.wallency.camelintegrationservice.publisher.IntegrationAggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Component
public class RemitaIntegrationRoutes extends RouteBuilder {

    private final AppConfiguration appConfiguration;

    public RemitaIntegrationRoutes(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Override
    public void configure() throws Exception {
        getContext().getGlobalOptions().put("CamelJacksonTypeConverterToPojo", "true");

//        onException(RuntimeException.class)
//                .process(this::reworkException);

        String password = appConfiguration.getRemita().getPassword();

        restConfiguration()
                .host("https://remitademo.net")
                .bindingMode(RestBindingMode.json);

        from("direct:remita-authenticate")
                .process(exchange -> {
                    Map<?, ?> userLogin;
                    if (appConfiguration.getRemita().isDemoEnv()) {
                        userLogin = Map.of(
                                "username", appConfiguration.getRemita().getUsername(),
                                "password", appConfiguration.getRemita().getPassword());
                    } else {
                        userLogin = Map.of(
                                "username", appConfiguration.getRemita().getLiveUsername(),
                                "password", appConfiguration.getRemita().getLivePassword());
                    }
                    exchange.getIn().setBody(userLogin);
                })
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:post:/remita/exapp/api/v1/send/api/uaasvc/uaa/token?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaAuthDto.class)
                .bean(RemitaService.class, "saveAuthToken")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler")
                .end();

        from("direct:remita-billers")
                .log("${body}")
                .enrich("direct:remita-authenticate", new IntegrationAggregationStrategy())
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:get:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/billers?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaBillersResponseDto.class)
                .bean(RemitaService.class, "billerResponse")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler")
                .end();


        from("direct:remita-biller-products")
                .log("${body}")
                .enrich("direct:remita-authenticate", new IntegrationAggregationStrategy())
                .process(this::reworkHeader)
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:get:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/biller/{billerId}/products?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaResponseDto.class)
                .bean(RemitaService.class, "transactionResponse")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler")
                .end();


        from("direct:remita-biller-transaction-validate-customer")
                .log("${body}")
                .enrich("direct:remita-authenticate", new IntegrationAggregationStrategy())
                .process(exchange -> {
                    Map<String, Object> headers = exchange.getMessage().getHeaders();
                    Map<String, Object> productHeaders = Map.of(
                            "publicKey", appConfiguration.getRemita().getPublicKey());
                    headers.putAll(productHeaders);
                    exchange.getIn().setHeaders(headers);
                    ArrayList<?> body = (ArrayList<?>) exchange.getMessage().getBody(Object.class);
                    RemitaValidateCustomerDto remitaValidateCustomerDto = (RemitaValidateCustomerDto) body.get(0);
                    exchange.getIn().setBody(remitaValidateCustomerDto);
                })
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:post:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/biller/customer/validation?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaResponseDto.class)
                .bean(RemitaService.class, "transactionResponse")
                .doCatch(RuntimeException.class)
                .to("direct:exceptionHandler")
                .end();


        from("direct:remita-biller-transaction-validate-initiate")
                .log("${body}")
                .enrich("direct:remita-authenticate", new IntegrationAggregationStrategy())
                .process(this::initiateTransactionProcessor)
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:post:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/biller/transaction/initiate?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaResponseDto.class)
                .bean(RemitaService.class, "transactionResponse")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler");


        from("direct:remita-bill-payment-notification")
                .log("${body}")
                .enrich("direct:remita-authenticate", new IntegrationAggregationStrategy())
                .process(this::process)
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:post:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/biller/transaction/paymentnotification?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaResponseDto.class)
                .bean(RemitaService.class, "transactionResponse")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler")
                .end();


        from("direct:remita-bill-payment-status")
                .log("${body}")
                .enrich("direct:remita-authenticate", new IntegrationAggregationStrategy())
                .process(this::reworkHeader)
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:get:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/biller/transaction/query/{transactionRef}?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaResponseDto.class)
                .bean(RemitaService.class, "transactionResponse")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler")
                .end();


        from("direct:remita-biller-categories")
                .log("${body}")
                .enrich("direct:remita-authenticate", new IntegrationAggregationStrategy())
                .process(this::reworkHeader)
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:get:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/categories?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaBillersResponseDto.class)
                .bean(RemitaService.class, "billerResponse")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler")
                .end();


        from("direct:remita-biller-by-category")
                .log("${body}")
                .enrich("direct:remita-authenticate", new IntegrationAggregationStrategy())
                .log("${body}")
                .process(this::reworkHeader)
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:get:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/category/4?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaBillersResponseDto.class)
                .bean(RemitaService.class, "billerResponse")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler")
                .end();


        from("direct:exceptionHandler")
                .process(this::reworkException)
                .log(LoggingLevel.WARN, "${body}")
                .to("log:reply");

    }


    private void initiateTransactionProcessor(Exchange exchange) {
        setExchangeHeader(exchange);
        ArrayList<?> body = (ArrayList<?>) exchange.getMessage().getBody(Object.class);
        RemitaInitiateTransactionDto remitaInitiateTransactionDto = (RemitaInitiateTransactionDto) body.get(0);
        exchange.getIn().setBody(remitaInitiateTransactionDto);
    }

    private void setExchangeHeader(Exchange exchange) {
        Map<String, Object> headers = exchange.getMessage().getHeaders();
        Map<String, Object> productHeaders = Map.of(
                "publicKey", appConfiguration.getRemita().getPublicKey());
        headers.putAll(productHeaders);
        exchange.getIn().setHeaders(headers);
    }

    private void reworkException(Exchange exchange) {
        Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setDescription(exception.getMessage());
        baseResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        exchange.getIn().setBody(baseResponse);
    }

    private void reworkHeader(Exchange exchange) {
        setExchangeHeader(exchange);
        exchange.getIn().setBody(null);
    }

    private void process(Exchange exchange) {
        setExchangeHeader(exchange);
        ArrayList<?> body = (ArrayList<?>) exchange.getMessage().getBody(Object.class);
        RemitaBillPaymentNotificationDto remitaBillPaymentNotificationDto = (RemitaBillPaymentNotificationDto) body.get(0);
        exchange.getIn().setBody(remitaBillPaymentNotificationDto);
    }
}
