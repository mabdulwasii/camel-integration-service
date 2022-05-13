package com.stsl.wallency.camelintegrationservice.dto.remita;

import lombok.Data;

import java.util.List;

@Data
public class RemitaBillersResponseDto {

    private String status;

    private String message;

    private List<RemitaBillersDto> data;



}
