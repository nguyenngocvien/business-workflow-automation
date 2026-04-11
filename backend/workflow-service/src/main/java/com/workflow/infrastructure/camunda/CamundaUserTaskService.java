package com.workflow.infrastructure.camunda;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.dto.TaskState;
import io.camunda.tasklist.exception.TaskListException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CamundaUserTaskService {

    private CamundaTaskListClient tasklistClient;

    public CamundaUserTaskService(@Autowired(required = false) CamundaTaskListClient tasklistClient) {
        this.tasklistClient = tasklistClient;
    }

    public List<Task> getPendingTasks() throws TaskListException {
        return tasklistClient.getTasks(true, TaskState.CREATED, 50).getItems();
    }

    public void completeUserTask(String taskId, Map<String, Object> variables) throws TaskListException {
        tasklistClient.completeTask(taskId, variables);
        
        System.out.println("User Task " + taskId + " completed successfully.");
    }

    public void claimTask(String taskId, String assignee) throws TaskListException {
        tasklistClient.claim(taskId, assignee);
    }

    public void unclaimTask(String taskId) throws TaskListException {
        tasklistClient.unclaim(taskId);
    }
}
