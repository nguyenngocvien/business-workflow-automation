package com.dms.interfaces.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileAttributeValueRequest {

    private String key; // attribute keyCode
    private Object value; // dynamic
}