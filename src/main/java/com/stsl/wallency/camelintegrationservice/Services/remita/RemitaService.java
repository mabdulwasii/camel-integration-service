package com.stsl.wallency.camelintegrationservice.Services.remita;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stsl.wallency.camelintegrationservice.dto.BaseResponse;
import com.stsl.wallency.camelintegrationservice.dto.remita.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.support.DefaultMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


@Service
@Slf4j
public class RemitaService {

    private final BearerTokenConfiguration bearerTokenConfiguration;

    public RemitaService(BearerTokenConfiguration bearerTokenConfiguration) {
        this.bearerTokenConfiguration = bearerTokenConfiguration;
    }

    public void saveAuthToken(@Body RemitaAuthDto remitaAuthDto) {
        RemitaAuthDataDto remitaAuthDataDto = remitaAuthDto.getData().get(0);
        String accessToken = remitaAuthDataDto.getAccessToken();
        bearerTokenConfiguration.setToken(accessToken);
    }

//
//    public void getAllBillers(Exchange exchange) {
//        log.info("{}", exchange.getIn().getBody(RemitaBillersResponseDto.class));
//        RemitaBillersResponseDto remitaBillersResponseDto = exchange.getIn().getBody(RemitaBillersResponseDto.class);
//
//        List<RemitaBillersDto> remitaBillersDtos = remitaBillersResponseDto.getData();
//        Message message = new DefaultMessage(exchange);
//        HashMap<String, Object> allBillers = new HashMap<>();
//        allBillers.put("allBillers", remitaBillersDtos);
//        message.setBody(allBillers);
//        message.setHeaders(exchange.getIn().getHeaders());
//        exchange.setMessage(message);
//    }

    public void getBillCategories(Exchange exchange) {
        RemitaBillersResponseDto remitaBillersResponseDto = exchange.getIn().getBody(RemitaBillersResponseDto.class);
        List<RemitaBillersDto> remitaBillersDtoList = remitaBillersResponseDto.getData();
        Message message = new DefaultMessage(exchange);
        HashMap<String, Object> billCategories = new HashMap<>();
        billCategories.put("billCategories", remitaBillersDtoList);
        message.setBody(billCategories);
        message.setHeaders(exchange.getIn().getHeaders());
        exchange.setMessage(message);

    }

    public void getBillsByCategory(Exchange exchange) {
        RemitaBillersResponseDto remitaBillersResponseDto = exchange.getIn().getBody(RemitaBillersResponseDto.class);
        List<RemitaBillersDto> remitaBillersResponseDtoData = remitaBillersResponseDto.getData();
        Message message = new DefaultMessage(exchange);
        HashMap<String, Object> billsByCategory = new HashMap<>();
        billsByCategory.put("billsByCategory", remitaBillersResponseDtoData);
        message.setBody(billsByCategory);
        message.setHeaders(exchange.getIn().getHeaders());
        exchange.setMessage(message);

    }


//    public void getBillerProducts(Exchange exchange) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.findAndRegisterModules();
//        RemitaBillerProductsResponseDto remitaBillerProductsResponseDto = exchange.getIn().getBody(RemitaBillerProductsResponseDto.class);
//        Message message = new DefaultMessage(exchange);
//        HashMap<String, Object> billerProducts = new HashMap<>();
//        billerProducts.put("billerProducts", remitaBillerProductsResponseDto.getData());
//        message.setBody(billerProducts);
//        message.setHeaders(exchange.getIn().getHeaders());
//        exchange.setMessage(message);
//
//    }


    public BaseResponse getAllBillers(Exchange exchange) {
        BaseResponse baseResponse = new BaseResponse();
        RemitaBillersResponseDto remitaBillersResponseDto = exchange.getIn().getBody(RemitaBillersResponseDto.class);
        if (remitaBillersResponseDto.getStatus().equals("00")) {
            List<RemitaBillersDto> remitaBillersDtoList = remitaBillersResponseDto.getData();
            baseResponse.setStatusCode(HttpStatus.OK.value());
            baseResponse.setDescription("Remita billers found.");
            baseResponse.setData(remitaBillersDtoList);
        } else {
            throw new RuntimeException("No remita billers found.");
        }
        return baseResponse;
    }


    public BaseResponse getBillerProducts(Exchange exchange) {
        BaseResponse baseResponse = new BaseResponse();
        RemitaBillerProductsResponseDto remitaBillerProductsResponseDto = exchange.getIn().getBody(RemitaBillerProductsResponseDto.class);
        if (remitaBillerProductsResponseDto.getStatus().equals("00")) {
            Object remitaBillerProductsResponseDtoData = remitaBillerProductsResponseDto.getData();
            baseResponse.setStatusCode(HttpStatus.OK.value());
            baseResponse.setDescription("Biller products found.");
            baseResponse.setData(remitaBillerProductsResponseDtoData);
        } else {
            throw new RuntimeException("No biller products found.");
        }
        return baseResponse;

    }

}
