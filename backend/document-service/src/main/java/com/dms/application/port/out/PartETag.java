package com.dms.application.port.out;

import lombok.Data;

@Data
public class PartETag {
    private int partNumber;
    private String eTag;
}