package com.workflow.infrastructure.camunda;

import java.util.Map;

import com.workflow.application.port.out.UserTaskService;

import io.camunda.client.CamundaClient;

public class CamundaUserTaskService implements UserTaskService {

    private final CamundaClient zeebeClient;
    
    public CamundaUserTaskService(CamundaClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public void completeTask(String taskId) {

        zeebeClient.newPublishMessageCommand()
                .messageName("USER_TASK_COMPLETED")
                .correlationKey(taskId)
                .variables(Map.of("taskId", taskId))
                .send();
    }
}
