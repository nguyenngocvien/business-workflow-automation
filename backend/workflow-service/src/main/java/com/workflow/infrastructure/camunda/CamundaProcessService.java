package com.workflow.infrastructure.camunda;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;

import org.springframework.stereotype.Service;

import com.workflow.application.port.out.ProcessService;

import java.util.Map;

@Service
public class CamundaProcessService implements ProcessService {

    private final CamundaClient camundaClient;

    public CamundaProcessService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public long startProcess(String bpmnProcessId, Map<String, Object> variables) {
        ProcessInstanceEvent instance = camundaClient.newCreateInstanceCommand()
                .bpmnProcessId(bpmnProcessId)
                .latestVersion()
                .variables(variables)
                .send()
                .join();


        return instance.getProcessInstanceKey();
    }
}