package com.dms.application.command;

import java.util.List;

import com.dms.domain.entity.AttributeFilter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileSearchCommand {

    private final Short status;
    private final String categoryCode;
    private final List<AttributeFilter> filters;
    private final int page;
    private final int size;
}