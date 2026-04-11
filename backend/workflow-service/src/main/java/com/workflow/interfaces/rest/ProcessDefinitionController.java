package com.workflow.interfaces.rest;

import com.workflow.application.usecase.ProcessDefinitionUseCase;
import com.workflow.application.usecase.command.DeployWorkflowBpmnCommand;
import com.workflow.application.usecase.command.DeployWorkflowDefinitionCommand;
import com.workflow.application.usecase.command.StepDefinitionCommand;
import com.workflow.application.usecase.command.UploadWorkflowBpmnCommand;
import com.workflow.application.usecase.result.WorkflowBpmnResult;
import com.workflow.application.usecase.result.WorkflowDefinitionResult;
import com.workflow.application.usecase.result.WorkflowDefinitionDeploymentResult;
import com.workflow.interfaces.rest.request.DeployWorkflowBpmnRequest;
import com.workflow.interfaces.rest.request.DeployWorkflowDefinitionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/workflow-definitions")
@Tag(name = "Workflow Definition", description = "APIs for workflow definition management")
public class ProcessDefinitionController {

    private final ProcessDefinitionUseCase workflowDefinitionUseCase;

    public ProcessDefinitionController(ProcessDefinitionUseCase workflowDefinitionUseCase) {
        this.workflowDefinitionUseCase = workflowDefinitionUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Deploy workflow definition", description = "Create a new workflow definition version with its step definitions")
    public WorkflowDefinitionResult deployDefinition(@Valid @RequestBody DeployWorkflowDefinitionRequest request) {
        DeployWorkflowDefinitionCommand command = DeployWorkflowDefinitionCommand.builder()
            .applicationName(request.applicationName())
            .workflowName(request.workflowName())
            .workflowKey(request.workflowKey())
            .description(request.description())
            .createdBy(request.createdBy())
            .steps(request.steps().stream()
                .map(step -> StepDefinitionCommand.builder()
                    .stepName(step.stepName())
                    .stepCode(step.stepCode())
                    .stepType(step.stepType())
                    .stepOrder(step.stepOrder())
                    .nextStepCode(step.nextStepCode())
                    .conditionExpression(step.conditionExpression())
                    .slaMinutes(step.slaMinutes())
                    .build())
                .toList())
            .build();
        return workflowDefinitionUseCase.deployDefinition(command);
    }

    @PostMapping(path = "/{definitionId}/bpmn", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload BPMN source", description = "Upload BPMN XML for an existing workflow definition version")
    public WorkflowBpmnResult uploadBpmn(
        @PathVariable Long definitionId,
        @RequestPart("file") MultipartFile file,
        @RequestParam(required = false, name = "resource_name") String resourceName,
        @RequestParam(name = "uploaded_by") String uploadedBy
    ) throws IOException {
        UploadWorkflowBpmnCommand command = UploadWorkflowBpmnCommand.builder()
            .definitionId(definitionId)
            .resourceName(resourceName == null || resourceName.isBlank() ? file.getOriginalFilename() : resourceName)
            .bpmnXml(new String(file.getBytes(), StandardCharsets.UTF_8))
            .uploadedBy(uploadedBy)
            .build();
        return workflowDefinitionUseCase.uploadBpmn(command);
    }

    @PostMapping("/{definitionId}/bpmn/deploy")
    @Operation(summary = "Deploy BPMN to Camunda", description = "Deploy uploaded BPMN of a workflow definition version to Camunda")
    public WorkflowDefinitionDeploymentResult deployBpmn(
        @PathVariable Long definitionId,
        @Valid @RequestBody DeployWorkflowBpmnRequest request
    ) {
        DeployWorkflowBpmnCommand command = DeployWorkflowBpmnCommand.builder()
            .definitionId(definitionId)
            .deployedBy(request.deployedBy())
            .build();
        return workflowDefinitionUseCase.deployBpmn(command);
    }

    @GetMapping("/{definitionId}/bpmn/deployments")
    @Operation(summary = "Get BPMN deployment history", description = "Return deployment history of uploaded BPMN by workflow definition id")
    public List<WorkflowDefinitionDeploymentResult> getDeploymentHistory(@PathVariable Long definitionId) {
        return workflowDefinitionUseCase.getDeploymentHistory(definitionId);
    }

    @GetMapping("/{definitionId}")
    @Operation(summary = "Get workflow definition by id", description = "Return workflow definition details by definition id")
    public WorkflowDefinitionResult getDefinition(@PathVariable Long definitionId) {
        return workflowDefinitionUseCase.getDefinition(definitionId);
    }

    @GetMapping
    @Operation(summary = "Search definitions by workflow key", description = "Return all versions of a workflow definition by workflow key")
    public List<WorkflowDefinitionResult> getDefinitionsByWorkflowKey(@RequestParam String workflowKey) {
        return workflowDefinitionUseCase.getDefinitionsByWorkflowKey(workflowKey);
    }
}
