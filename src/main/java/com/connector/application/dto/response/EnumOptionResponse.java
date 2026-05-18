package com.connector.application.dto.response;

public record EnumOptionResponse(
    String value,
    String label
) {
    public static EnumOptionResponse of(Enum<?> value) {
        return new EnumOptionResponse(value.name(), value.name());
    }
}
