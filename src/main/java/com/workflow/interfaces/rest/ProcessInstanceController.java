package com.workflow.interfaces.rest;

import com.workflow.application.usecase.ProcessInstanceUseCase;
import com.workflow.application.usecase.command.StartProcessCommand;
import com.workflow.application.usecase.result.ProcessInstanceResult;
import com.workflow.application.usecase.result.WorkflowProgressResult;
import com.workflow.application.usecase.result.WorkflowSearchOptionsResult;
import com.workflow.application.usecase.result.WorkflowSearchResult;
import com.workflow.interfaces.rest.request.StartProcessRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/processes")
@Tag(name = "Workflow Runtime", description = "APIs for workflow runtime, task processing, and search")
public class ProcessInstanceController {

    private final ProcessInstanceUseCase processInstanceUseCase;

    public ProcessInstanceController(ProcessInstanceUseCase processInstanceUseCase) {
        this.processInstanceUseCase = processInstanceUseCase;
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start process", description = "Start a new process instance using the latest active definition for the workflow key")
    public ProcessInstanceResult startProcess(@Valid @RequestBody StartProcessRequest request) {
        StartProcessCommand command = StartProcessCommand.builder()
            .workflowKey(request.workflowKey())
            .businessKey(request.businessKey())
            .startedBy(request.startedBy())
            .formData(request.formData())
            .build();
        return processInstanceUseCase.startProcess(command);
    }

    @GetMapping("/{instanceId}")
    @Operation(summary = "Get process instance", description = "Return workflow instance details including step instances and tasks")
    public ProcessInstanceResult getProcessInstance(@PathVariable Long instanceId) {
        return processInstanceUseCase.getProcessInstance(instanceId);
    }

    @GetMapping("/{instanceId}/progress")
    @Operation(summary = "Get workflow progress", description = "Return the transaction progress timeline including total steps, step names, processor, current step, and step statuses")
    public WorkflowProgressResult getProgress(@PathVariable Long instanceId) {
        return processInstanceUseCase.getProgress(instanceId);
    }

    @GetMapping("/search")
    @Operation(summary = "Search workflow tasks", description = "Search runtime workflow data by application name, workflow key, current step, workflow status, business key, and assignee")
    public List<WorkflowSearchResult> searchProcesses(
        @RequestParam(required = false, name = "application_name") String applicationName,
        @RequestParam(required = false, name = "workflow_key") String workflowKey,
        @RequestParam(required = false, name = "current_step_code") String currentStepCode,
        @RequestParam(required = false) String status,
        @RequestParam(required = false, name = "business_key") String businessKey,
        @RequestParam(required = false) String assignee
    ) {
        return processInstanceUseCase.searchProcesses(
            applicationName,
            workflowKey,
            currentStepCode,
            status,
            businessKey,
            assignee
        );
    }

    @GetMapping("/search-options")
    @Operation(summary = "Get workflow search options", description = "Return lookup data for workflow, step, and status dropdowns used by the search UI")
    public WorkflowSearchOptionsResult getProcessSearchOptions(
        @RequestParam(required = false, name = "workflow_key") String workflowKey
    ) {
        return processInstanceUseCase.getProcessSearchOptions(workflowKey);
    }

}
