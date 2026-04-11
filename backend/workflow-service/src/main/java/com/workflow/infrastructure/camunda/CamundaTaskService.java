package com.workflow.infrastructure.camunda;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.dto.TaskState;
import io.camunda.tasklist.exception.TaskListException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CamundaTaskService {

    private final CamundaTaskListClient tasklistClient;

    public List<Task> getPendingTasks() throws TaskListException {
        return tasklistClient.getTasks(true, TaskState.CREATED, 50).getItems();
    }

    public Task getTask(String taskId) throws TaskListException {
        return tasklistClient.getTask(taskId);
    }

    public Task claimTask(String taskId, String assignee) throws TaskListException {
        return tasklistClient.claim(taskId, assignee);
    }

    public Task unclaimTask(String taskId) throws TaskListException {
        return tasklistClient.unclaim(taskId);
    }

    public void completeTask(String taskId, Map<String, Object> variables) throws TaskListException {
        tasklistClient.completeTask(taskId, variables);
    }

    public void saveTaskData(String taskId, Map<String, Object> variables) throws TaskListException {
        tasklistClient.saveDraftVariables(taskId, variables);
    }
}
