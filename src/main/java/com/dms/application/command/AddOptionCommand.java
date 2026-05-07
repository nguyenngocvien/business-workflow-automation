package com.dms.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddOptionCommand {

    private final Long attributeId;
    private final String optionLabel;
    private final String optionValue;
    private final Integer sortOrder;
}