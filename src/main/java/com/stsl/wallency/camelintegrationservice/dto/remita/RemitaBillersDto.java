package com.stsl.wallency.camelintegrationservice.dto.remita;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemitaBillersDto {

    private String billerId;

    private String billerName;

    private String billerShortName;

    private String billerLogoUrl;

    private Integer categoryId;

    private String categoryName;

    private String categoryDescription;


}
