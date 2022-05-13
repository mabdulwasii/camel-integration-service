package com.stsl.wallency.camelintegrationservice.route;


import com.stsl.wallency.camelintegrationservice.configuration.AppConfiguration;
import com.stsl.wallency.camelintegrationservice.dto.BaseResponse;
import com.stsl.wallency.camelintegrationservice.dto.remita.BearerTokenConfiguration;
import com.stsl.wallency.camelintegrationservice.dto.remita.RemitaInitiateTransactionDto;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


@Component
public class IntegrationRoutes extends RouteBuilder {


    private final AppConfiguration appConfiguration;

    private final BearerTokenConfiguration bearerTokenConfiguration;


    public IntegrationRoutes(AppConfiguration appConfiguration, BearerTokenConfiguration bearerTokenConfiguration) {
        this.appConfiguration = appConfiguration;
        this.bearerTokenConfiguration = bearerTokenConfiguration;
    }

    @Override
    public void configure() throws Exception {
//        https://api.remita.net/#cac48dc9-14e6-4b5a-96d8-e4497e3ed02d
//        http://localhost:8080/integration-service/api-doc
        getContext().getGlobalOptions().put("CamelJacksonTypeConverterToPojo", "true");

        restConfiguration()
                .contextPath(appConfiguration.getContextPath())
                .apiContextPath("/api-doc")
                .host("localhost")
                .apiProperty("api.title", "Spring Boot Camel Postgres Rest API.")
                .apiProperty("api.version", "1.0")
                .enableCORS(true)
                .bindingMode(RestBindingMode.json);


        rest("/api/")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .get("/remita/billers")
                .responseMessage("200", "Billers found.")
                .responseMessage("404", "No remita billers found.")
                .description("Get All Remita Billers")
                .to("direct:remita-billers")

                .post("/remita/biller/product/{billerId}")
                .responseMessage("200", "Biller products found.")
                .responseMessage("404", "No biller products found.")
                .description("Get Remita Biller Products")
                .to("direct:remita-biller-products")

                .post("/remita/biller/transaction/initiate")
                .type(RemitaInitiateTransactionDto.class)
                .responseMessage("200", "Biller products found.")
                .responseMessage("404", "No biller products found.")
                .description("Get Remita Biller Products")
                .to("direct:remita-biller-transaction-initiate")

                .outType(BaseResponse.class);


        from("direct:remita-billers")
                .log("${body}")
                .to("direct:remita-authenticate")
                .log("${body}")
                .process(exchange -> {
                    String token = bearerTokenConfiguration.getToken();
                    exchange.getIn().setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                })
                .to("direct:remita-get-all-billers")
                .log("${body}");


        from("direct:remita-biller-products")
                .log("${body}")
                .to("direct:remita-authenticate")
                .log("${body}")
                .process(exchange -> {
                    String token = bearerTokenConfiguration.getToken();
                    exchange.getIn().setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                })
                .to("direct:remita-get-biller-products")
                .log("${body}");


        from("direct:remita-biller-transaction-initiate")
                .log("${body}")
                .to("direct:remita-authenticate")
                .log("${body}")
                .process(exchange -> {
                    String token = bearerTokenConfiguration.getToken();
                    exchange.getIn().setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                })
                .to("direct:remita-initiate-biller-transaction")
                .log("${body}");





//        rest("/api/")
//                .consumes(MediaType.APPLICATION_JSON_VALUE)
//                .produces(MediaType.APPLICATION_JSON_VALUE)
//                .get("/remita/billers/products/{billerId}")
//                .responseMessage("200", "Book with name was found.")
//                .responseMessage("404", "Book with name was not found.")
//                .description("Find book by name")
//                .to("direct:get-remita-billers");
//
//
//        from("direct:get-remita-billers")
//                .log("${body}")
//                .enrich("direct:remita-authenticate", new RemitaAuthIntegrationAggregationStrategy())
//                .log("${body}")
//                .process(exchange -> {
//                    String token = bearerTokenConfiguration.getToken();
//                    exchange.getIn().setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
//                })
//                .enrich("direct:remita-get-all-billers", new IntegrationAggregationStrategy())
//
//                .log("${body}")
//                .enrich("direct:remita-get-bill-categories", new IntegrationAggregationStrategy())
//
//                .log("${body}")
//                .enrich("direct:remita-get-bill-by-category", new IntegrationAggregationStrategy())
//
//                .log("${body}")
//                .enrich("direct:remita-get-biller-products", new IntegrationAggregationStrategy())
//
//                .to("log:testlogging");


    }
}
