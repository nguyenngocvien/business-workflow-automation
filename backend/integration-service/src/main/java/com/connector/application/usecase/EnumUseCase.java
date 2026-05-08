package com.connector.application.usecase;

import java.util.List;
import java.util.Map;

import com.connector.application.dto.response.EnumOptionResponse;

public interface EnumUseCase {

    Map<String, List<EnumOptionResponse>> findAll();

    List<EnumOptionResponse> findByName(String enumName);
}
