package com.stsl.wallency.camelintegrationservice.dto.remita;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
//@Embeddable
public class CustomFields {
        private String variable_name;

        private String value;
    }