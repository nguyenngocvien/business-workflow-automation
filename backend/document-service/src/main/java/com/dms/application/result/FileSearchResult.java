package com.dms.application.result;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileSearchResult {

    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private Instant createdAt;
}