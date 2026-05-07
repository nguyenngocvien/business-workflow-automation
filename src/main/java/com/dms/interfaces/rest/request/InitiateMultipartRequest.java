package com.dms.interfaces.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitiateMultipartRequest {
    private String categoryCode;
    private String fileName;
}