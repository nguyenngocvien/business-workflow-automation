package com.dms.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddContentTypeCommand {

    private final Long categoryId;
    private final String contentType;
}