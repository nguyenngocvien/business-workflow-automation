package com.connector.api.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.dto.response.EnumOptionResponse;
import com.connector.application.usecase.EnumUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/enums")
@Tag(name = "Enums", description = "Reference data for frontend select inputs")
public class EnumController {

    private final EnumUseCase useCase;

    public EnumController(EnumUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    @Operation(summary = "List all enum options")
    public java.util.Map<String, java.util.List<EnumOptionResponse>> findAll() {
        return useCase.findAll();
    }

    @GetMapping("/{enumName}")
    @Operation(summary = "List options for a single enum")
    public java.util.List<EnumOptionResponse> findByName(
        @Parameter(description = "Enum name, for example ServiceType") @PathVariable String enumName
    ) {
        return useCase.findByName(enumName);
    }
}
