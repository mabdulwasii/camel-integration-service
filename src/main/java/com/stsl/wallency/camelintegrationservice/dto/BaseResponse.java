package com.stsl.wallency.camelintegrationservice.dto;

import lombok.Data;

@Data
public class BaseResponse {
    private int statusCode;

    private String description;

    private Object data;

    private Object errors;
}
