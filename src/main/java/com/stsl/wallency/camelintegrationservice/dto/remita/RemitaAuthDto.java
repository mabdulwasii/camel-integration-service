package com.stsl.wallency.camelintegrationservice.dto.remita;

import lombok.Data;

import java.util.List;

@Data
public class RemitaAuthDto {

    private String status;

    private String message;

    private List<RemitaAuthDataDto> data;

}
