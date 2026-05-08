package com.workflow.application.port.out.serializer;

public interface JsonSerializer {
    String toJson(Object obj);
    <T> T fromJson(String json, Class<T> clazz);
}