package com.stsl.wallency.camelintegrationservice.dto.remita;

import lombok.Data;
import org.checkerframework.checker.index.qual.LowerBoundBottom;

@Data
public class RemitaAuthDataDto {

    private String accessToken;

    private Integer expiresIn;



}
