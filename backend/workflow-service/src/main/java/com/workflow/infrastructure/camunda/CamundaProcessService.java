package com.workflow.infrastructure.camunda;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import io.camunda.client.api.response.ProcessInstanceResult;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CamundaProcessService {

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

    /**
     * Start process and wait for result (optional use case)
     */
    public ProcessInstanceResult startProcessAndWait(String bpmnProcessId, Map<String, Object> variables) {
        return camundaClient
                .newCreateInstanceCommand()
                .bpmnProcessId(bpmnProcessId)
                .latestVersion()
                .variables(variables)
                .withResult()
                .send()
                .join();
    }

    /**
     * Get process instance info
     * NOTE: Zeebe không query trực tiếp runtime instance như Camunda 7
     * => cần dùng Operate API hoặc lưu tracking riêng
     */
    public String getProcessInstance(long processInstanceKey) {
        // Zeebe không hỗ trợ query trực tiếp instance state qua client
        // Thông thường bạn sẽ:
        // 1. Query qua Operate API
        // 2. Hoặc lưu state vào DB riêng

        return "ProcessInstanceKey: " + processInstanceKey +
                " (Use Operate API to fetch details)";
    }
}