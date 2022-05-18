package com.stsl.wallency.camelintegrationservice.dto.remita.transaction;


import lombok.Data;

import java.util.List;

@Data
public class Metadata {
    private List<CustomFields> customFields;
}
