package com.stsl.wallency.camelintegrationservice.Services.remita;

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


    public BaseResponse getAllBillers(Exchange exchange) {
        BaseResponse baseResponse = new BaseResponse();
        RemitaBillersResponseDto remitaBillersResponseDto = exchange.getIn().getBody(RemitaBillersResponseDto.class);
        if (remitaBillersResponseDto.getStatus().equals("00")) {
            List<RemitaBillersDto> remitaBillersDtoList = remitaBillersResponseDto.getData();
            baseResponse.setStatusCode(HttpStatus.OK.value());
            baseResponse.setDescription(remitaBillersResponseDto.getMessage());
            baseResponse.setData(remitaBillersDtoList);
        } else {
            throw new RuntimeException(remitaBillersResponseDto.getMessage());
        }
        return baseResponse;
    }


//    public BaseResponse getBillerProducts(Exchange exchange) {
//        BaseResponse baseResponse = new BaseResponse();
//        RemitaResponseDto remitaResponseDto = exchange.getIn().getBody(RemitaResponseDto.class);
//        if (remitaResponseDto.getStatus().equals("00")) {
//            Object remitaBillerProductsResponseDtoData = remitaResponseDto.getData();
//            baseResponse.setStatusCode(HttpStatus.OK.value());
//            baseResponse.setDescription(remitaResponseDto.getMessage());
//            baseResponse.setData(remitaBillerProductsResponseDtoData);
//        } else {
//            throw new RuntimeException(remitaResponseDto.getMessage());
//        }
//        return baseResponse;
//
//    }


//    public BaseResponse validateCustomer(Exchange exchange) {
//        BaseResponse baseResponse = new BaseResponse();
//        RemitaResponseDto remitaResponseDto = exchange.getIn().getBody(RemitaResponseDto.class);
//        if (remitaResponseDto.getStatus().equals("00")) {
//            baseResponse.setStatusCode(HttpStatus.OK.value());
//            baseResponse.setDescription(remitaResponseDto.getMessage());
//            baseResponse.setData(remitaResponseDto.getData());
//        } else {
//            throw new RuntimeException(remitaResponseDto.getMessage());
//        }
//        return baseResponse;
//
//    }

//    public BaseResponse initiateTransaction(Exchange exchange) {
//        BaseResponse baseResponse = new BaseResponse();
//        RemitaResponseDto remitaResponseDto = exchange.getIn().getBody(RemitaResponseDto.class);
//        if (remitaResponseDto.getStatus().equals("00")) {
//            baseResponse.setStatusCode(HttpStatus.OK.value());
//            baseResponse.setDescription(remitaResponseDto.getMessage());
//            baseResponse.setData(remitaResponseDto.getData());
//        } else {
//            throw new RuntimeException(remitaResponseDto.getMessage());
//        }
//        return baseResponse;
//
//    }

//    public BaseResponse billPaymentNotification(Exchange exchange) {
//        BaseResponse baseResponse = new BaseResponse();
//        RemitaResponseDto remitaResponseDto = exchange.getIn().getBody(RemitaResponseDto.class);
//        if (remitaResponseDto.getStatus().equals("00")) {
//            baseResponse.setStatusCode(HttpStatus.OK.value());
//            baseResponse.setDescription(remitaResponseDto.getMessage());
//            baseResponse.setData(remitaResponseDto.getData());
//        } else {
//            throw new RuntimeException(remitaResponseDto.getMessage());
//        }
//        return baseResponse;
//    }


    public BaseResponse remitaResponse(Exchange exchange) {
        BaseResponse baseResponse = new BaseResponse();
        RemitaResponseDto remitaResponseDto = exchange.getIn().getBody(RemitaResponseDto.class);
        if (remitaResponseDto.getStatus().equals("00")) {
            baseResponse.setStatusCode(HttpStatus.OK.value());
            baseResponse.setDescription(remitaResponseDto.getMessage());
            baseResponse.setData(remitaResponseDto.getData());
        } else {
            throw new RuntimeException(remitaResponseDto.getMessage());
        }
        return baseResponse;
    }




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


}
