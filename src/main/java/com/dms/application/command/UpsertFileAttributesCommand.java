package com.dms.application.command;

import java.util.List;

import com.dms.interfaces.rest.request.FileAttributeValueRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpsertFileAttributesCommand {

    private final Long fileId;
    private final List<FileAttributeValueRequest> attributes;
}