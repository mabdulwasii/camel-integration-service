package com.stsl.wallency.camelintegrationservice.route;


import com.stsl.wallency.camelintegrationservice.configuration.AppConfiguration;
import com.stsl.wallency.camelintegrationservice.dto.BaseResponse;
import com.stsl.wallency.camelintegrationservice.dto.remita.RemitaBillPaymentNotificationDto;
import com.stsl.wallency.camelintegrationservice.dto.remita.RemitaInitiateTransactionDto;
import com.stsl.wallency.camelintegrationservice.dto.remita.RemitaValidateCustomerDto;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


@Component
public class IntegrationRoutes extends RouteBuilder {

    private final AppConfiguration appConfiguration;


    public IntegrationRoutes(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
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

                .post("/remita/biller/transaction/validate/customer")
                .type(RemitaValidateCustomerDto.class)
                .responseMessage("200", "Biller products found.")
                .responseMessage("404", "No biller products found.")
                .description("Validate Customer")
                .to("direct:remita-biller-transaction-validate-customer")

                .post("/remita/biller/transaction/initiate")
                .type(RemitaInitiateTransactionDto.class)
                .responseMessage("200", "Biller products found.")
                .responseMessage("404", "No biller products found.")
                .description("Generate RRR and initiate transaction")
                .to("direct:remita-biller-transaction-validate-initiate")
                .outType(BaseResponse.class)

                .post("/remita/bill/payment/notification")
                .type(RemitaBillPaymentNotificationDto.class)
                .responseMessage("200", "Biller products found.")
                .responseMessage("404", "No biller products found.")
                .description("Generate bill payment notification")
                .to("direct:remita-bill-payment-notification")
                .outType(BaseResponse.class)

                .get("/remita/bill/payment/status/{transactionRef}")
                .responseMessage("200", "Biller products found.")
                .responseMessage("404", "No biller products found.")
                .description("Get bill payment status")
                .to("direct:remita-bill-payment-status")
                .outType(BaseResponse.class)

                .get("/remita/biller/categories")
                .responseMessage("200", "Book with name was found.")
                .responseMessage("404", "Book with name was not found.")
                .description("Get biller categories")
                .to("direct:remita-biller-categories")


                .get("/remita/biller/category/{categoryId}")
                .responseMessage("200", "Book with name was found.")
                .responseMessage("404", "Book with name was not found.")
                .description("Get biller categories")
                .to("direct:remita-biller-by-category");


    }
}
