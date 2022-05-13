package com.stsl.wallency.camelintegrationservice.route.RemitaRoute;

import com.stsl.wallency.camelintegrationservice.Services.remita.RemitaService;
import com.stsl.wallency.camelintegrationservice.dto.BaseResponse;
import com.stsl.wallency.camelintegrationservice.dto.remita.RemitaAuthDto;
import com.stsl.wallency.camelintegrationservice.dto.remita.RemitaBillerProductsResponseDto;
import com.stsl.wallency.camelintegrationservice.dto.remita.RemitaBillersResponseDto;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RemitaIntegrationRoutes extends RouteBuilder {


    @Value("${app.remita.password}")
    String remitaPassword;

    @Value("${app.remita.username}")
    String remitaUserName;

    @Value("${app.remita.publickey}")
    String remitapublicKey;


    @Override
    public void configure() throws Exception {
        getContext().getGlobalOptions().put("CamelJacksonTypeConverterToPojo", "true");

        restConfiguration()
                .host("https://remitademo.net")
                .bindingMode(RestBindingMode.json);

        from("direct:remita-authenticate")
                .process(exchange -> {
                    Map<?, ?> userLogin = Map.of(
                            "username", remitaUserName,
                            "password", remitaPassword);
                    exchange.getIn().setBody(userLogin);
                })
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .to("rest:post:/remita/exapp/api/v1/send/api/uaasvc/uaa/token?bridgeEndpoint=true")
                .log("${body}")
                .unmarshal().json(JsonLibrary.Jackson, RemitaAuthDto.class)
                .bean(RemitaService.class, "saveAuthToken");


        from("direct:remita-get-all-billers")
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:get:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/billers?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaBillersResponseDto.class)
                .bean(RemitaService.class, "getAllBillers")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler")
                .end();


        from("direct:remita-get-biller-products")
                .process(exchange -> {
                    Map<String, Object> headers = exchange.getMessage().getHeaders();
                    Map<String, Object> productHeaders = Map.of(
                            "publicKey", remitapublicKey);
                    headers.putAll(productHeaders);
                    exchange.getIn().setHeaders(headers);
                })
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:get:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/biller/{billerId}/products?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaBillerProductsResponseDto.class)
                .bean(RemitaService.class, "getBillerProducts")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler")
                .end();


        from("direct:remita-validate-initiate-biller-transaction")
                .process(exchange -> {
                    Map<String, Object> headers = exchange.getMessage().getHeaders();
                    Map<String, Object> productHeaders = Map.of(
                            "publicKey", remitapublicKey);
                    headers.putAll(productHeaders);
                    exchange.getIn().setHeaders(headers);
                })
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                    .to("rest:post:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/biller/customer/validation?bridgeEndpoint=true")
                    .unmarshal().json(JsonLibrary.Jackson, RemitaBillerProductsResponseDto.class)
                    .bean(RemitaService.class, "validateCustomer")
//                    .to("direct:remita-initiate-biller-transaction")
                .doCatch(Exception.class)
                     .to("direct:exceptionHandler")
                .end();




        from("direct:remita-initiate-biller-transaction")
                .process(exchange -> {
                    Map<String, Object> headers = exchange.getMessage().getHeaders();
                    Object body = exchange.getMessage().getBody();
                    log.info("body {}", body);
//                    Map<String, Object> categoriesHeaders = Map.of(
//                            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
//                            "publicKey", remitapublicKey);
//                    headers.putAll(categoriesHeaders);
                    exchange.getIn().setHeaders(headers);
                })
                .log("${body}")
                .removeHeader(Exchange.HTTP_PATH)
                .doTry()
                .to("rest:post:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/biller/transaction/initiate?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, RemitaBillerProductsResponseDto.class)
                .bean(RemitaService.class, "initiateTransaction")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler");


//
//        from("direct:remita-get-bill-categories")
//                .process(exchange -> {
//                    Map<String, Object> headers = exchange.getMessage().getHeaders();
//                    Map<String, Object> categoriesHeaders = Map.of(
//                            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
//                            "publicKey", remitapublicKey);
//                    headers.putAll(categoriesHeaders);
//                    exchange.getIn().setHeaders(headers);
//                })
//                .log("${body}")
//                .removeHeader(Exchange.HTTP_PATH)
//                .to("rest:get:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/categories?bridgeEndpoint=true")
//                .unmarshal().json(JsonLibrary.Jackson, RemitaBillersResponseDto.class)
//                .bean(RemitaService.class, "getBillCategories");
//
//
//        from("direct:remita-get-bill-by-category")
//                .log("${body}")
//                .removeHeader(Exchange.HTTP_PATH)
//                .to("rest:get:/remita/exapp/api/v1/send/api/bgatesvc/v3/billpayment/category/4?bridgeEndpoint=true")
//                .unmarshal().json(JsonLibrary.Jackson, RemitaBillersResponseDto.class)
//                .bean(RemitaService.class, "getBillsByCategory");


        from("direct:exceptionHandler")
                .process(this::reworkException)
                .log(LoggingLevel.WARN, "${body}")
                .to("log:reply");

    }

    private void reworkException(Exchange exchange) {
        Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setDescription(exception.getMessage());
        baseResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        exchange.getIn().setBody(baseResponse);
    }

}
