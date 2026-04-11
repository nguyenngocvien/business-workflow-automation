package com.workflow.infrastructure.camunda;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.DeploymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class CamundaDeployService {

    private final CamundaClient camundaClient;

    public CamundaDeployService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public DeploymentResult deployBpmn(String processKey, Integer version, String bpmnXml) {
        String resourceName = processKey + "_v" + version + ".bpmn";

        log.info("Deploying process: {} version: {} to Camunda 8", processKey, version);

        DeploymentEvent deploymentEvent = camundaClient.newDeployResourceCommand()
                .addResourceBytes(
                    bpmnXml.getBytes(StandardCharsets.UTF_8),
                    resourceName
                )
                .send()
                .join();

        // Lấy thông tin process đã deploy
        io.camunda.client.api.response.Process deployedProcess = deploymentEvent.getProcesses().stream()
                .filter(p -> p.getBpmnProcessId().equals(processKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                    "Process not found in deployment: " + processKey));

        log.info("Successfully deployed. DefinitionKey={}, CamundaVersion={}",
                deployedProcess.getProcessDefinitionKey(),
                deployedProcess.getVersion());

        return DeploymentResult.builder()
                .deploymentKey(String.valueOf(deploymentEvent.getKey()))
                .processDefinitionKey(deployedProcess.getProcessDefinitionKey())
                .camundaVersion(String.valueOf(deployedProcess.getVersion()))
                .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class DeploymentResult {
        private String deploymentKey;
        private long processDefinitionKey;
        private String camundaVersion;
    }
}