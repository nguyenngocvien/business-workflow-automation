package com.dms.interfaces.rest.request;

import java.util.List;

import com.dms.domain.entity.AttributeFilter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileSearchRequest {

    private Short status;
    private String categoryCode;

    private List<AttributeFilter> filters;

    private int page = 0;
    private int size = 20;
}