package com.dms.application.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlResult {
    private int partNumber;
    private String url;
}