package com.dms.interfaces.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddOptionRequest {

    private String optionLabel;
    private String optionValue;
    private Integer sortOrder;
}