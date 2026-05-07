package com.dms.interfaces.rest.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileAttributeValuesRequest {

    private List<FileAttributeValueRequest> attributes;

}