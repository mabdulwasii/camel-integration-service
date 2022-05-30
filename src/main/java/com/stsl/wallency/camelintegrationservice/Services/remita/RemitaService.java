package com.stsl.wallency.camelintegrationservice.Services.remita;

import com.stsl.wallency.camelintegrationservice.dto.BaseResponse;
import com.stsl.wallency.camelintegrationservice.dto.remita.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.support.DefaultMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class RemitaService {
    public void saveAuthToken(Exchange exchange) {
        RemitaAuthDto remitaAuthDto = exchange.getIn().getBody(RemitaAuthDto.class);
        if (remitaAuthDto.getStatus().equals("00")) {
            RemitaAuthDataDto remitaAuthDataDto = remitaAuthDto.getData().get(0);
            String accessToken = remitaAuthDataDto.getAccessToken();
            Message message = new DefaultMessage(exchange);
            Map<String, Object> headers = exchange.getMessage().getHeaders();
            Map<String, Object> authHeader = Map.of(
                    HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            headers.putAll(authHeader);
            message.setHeaders(exchange.getIn().getHeaders());
            message.setBody(exchange.getIn().getBody());
            exchange.setMessage(message);
        } else {
            throw new RuntimeException(remitaAuthDto.getMessage());
        }

    }


    public BaseResponse billerResponse(Exchange exchange) {
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


    public BaseResponse transactionResponse(Exchange exchange) {
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


}
