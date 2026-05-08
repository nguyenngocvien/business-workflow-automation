package com.connector.application.port.out;

import java.util.List;
import java.util.Map;

import com.connector.application.port.out.model.DbRequest;

public interface DatabaseClient {

    int executeUpdate(DbRequest request);

    List<Map<String, Object>> executeQuery(DbRequest request);
}