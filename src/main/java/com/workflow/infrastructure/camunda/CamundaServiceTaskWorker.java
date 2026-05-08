package com.workflow.infrastructure.camunda;

import java.time.LocalDateTime;
import java.util.Map;

import com.workflow.application.usecase.UserTaskUseCase;
import com.workflow.application.usecase.WorkflowDefinitionUseCase;
import com.workflow.application.usecase.command.CompleteTaskCommand;
import com.workflow.application.usecase.command.CreateTaskCommand;
import com.workflow.application.usecase.result.WorkflowTaskResult;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;

public class CamundaServiceTaskWorker {

    private final WorkflowDefinitionUseCase workflowDefinitionUseCase;
    private final UserTaskUseCase userTaskUseCase;

    public CamundaServiceTaskWorker(WorkflowDefinitionUseCase workflowDefinitionUseCase, UserTaskUseCase userTaskUseCase) {
        this.workflowDefinitionUseCase = workflowDefinitionUseCase;
        this.userTaskUseCase = userTaskUseCase;
    }

    @JobWorker(type = "get-first-step")
    public Map<String, Object> getFirstStep(ActivatedJob job) {

        String processInstanceId = String.valueOf(job.getProcessInstanceKey());

        boolean hasNextTask = workflowDefinitionUseCase.hasFirstStep(processInstanceId);

        return Map.of(
                "hasNextTask", hasNextTask
        );
    }

    @JobWorker(type = "create-task")
    public Map<String, Object> createTask(ActivatedJob job) {

        Long processInstanceId = job.getProcessInstanceKey();

        Map<String, Object> vars = job.getVariablesAsMap();

        // 3. Build command
        CreateTaskCommand command = CreateTaskCommand.builder()
                .processInstanceId(processInstanceId)
                .taskName((String) vars.get("taskName"))
                .taskCode((String) vars.get("taskCode"))
                .candidateGroup((String) vars.get("candidateGroup"))
                .assignee(null) // chưa assign
                .owner(null)
                .status("OPEN")
                .priority((Integer) vars.get("priority"))
                .dueDate(null) // optional
                .createdAt(LocalDateTime.now())
                .data(vars)
                .build();

        // 4. Call use case
        WorkflowTaskResult result = userTaskUseCase.createTask(command);

        // 5. Return cho Zeebe (IMPORTANT)
        return Map.of(
                "taskId", String.valueOf(result.id())
        );
    }

    @JobWorker(type = "complete-task")
    public Map<String, Object> completeTask(ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();

        String taskId = (String) vars.get("taskId");
        String processInstanceId = String.valueOf(job.getProcessInstanceKey());

        CompleteTaskCommand cmd = CompleteTaskCommand.builder()
                .taskId(Long.valueOf(taskId))
                .actionBy("system") // system action
                .comment("Auto-completed by service task worker")
                .data(Map.of(
                        "processInstanceId", processInstanceId
                ))
                .build();

        WorkflowTaskResult nextTask = userTaskUseCase.completeTask(cmd);

        return Map.of("hasNextTask", nextTask.taskCode() != null);
    }
}
