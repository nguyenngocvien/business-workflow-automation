package com.dms.interfaces.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dms.application.command.AddOptionCommand;
import com.dms.application.command.CreateFileAttributeCommand;
import com.dms.application.result.FileAttributeResult;
import com.dms.application.usecase.FileAttributeUseCase;
import com.dms.interfaces.rest.request.AddOptionRequest;
import com.dms.interfaces.rest.request.CreateFileAttributeRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/file-attributes")
@Tag(name = "File Attributes", description = "Define file attributes and their selectable options.")
@RequiredArgsConstructor
public class FileAttributeController {

    private final FileAttributeUseCase useCase;

    @PostMapping
    @Operation(
            operationId = "createFileAttribute",
            summary = "Create a file attribute",
            description = "Creates a new file attribute definition.")
    public FileAttributeResult create(@RequestBody CreateFileAttributeRequest req) {

        return useCase.create(new CreateFileAttributeCommand(
                req.getKeyCode(),
                req.getDisplayName(),
                req.getDataType(),
                req.getIsRequired()
        ));
    }

    @GetMapping
    @Operation(
            operationId = "listFileAttributes",
            summary = "List file attributes",
            description = "Returns all configured file attributes.")
    public List<FileAttributeResult> getAll() {
        return useCase.getAll();
    }

    @PostMapping("/{id}/options")
    @Operation(
            operationId = "addFileAttributeOption",
            summary = "Add an option",
            description = "Adds an option to a file attribute.")
    public void addOption(
            @PathVariable Long id,
            @RequestBody AddOptionRequest req) {

        useCase.addOption(new AddOptionCommand(
                id,
                req.getOptionLabel(),
                req.getOptionValue(),
                req.getSortOrder()
        ));
    }
}
