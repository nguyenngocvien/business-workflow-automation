package com.dms.domain.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributeFilter {
    private String key;
    private String operator; // eq, gt, like...
    private Object value;
}