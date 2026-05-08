package com.workflow.interfaces.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "workflow.task.queue")
public class WorkflowTaskQueueProperties {

    private boolean enabled = true;
    private String exchange = "workflow.task.exchange";
    private String name = "workflow.task.queue";
    private String createRoutingKey = "workflow.task.create";
    private String claimRoutingKey = "workflow.task.claim";
    private String completeRoutingKey = "workflow.task.complete";
    private String deadLetterExchange = "workflow.task.dlx";
    private String deadLetterQueue = "workflow.task.dlq";
    private String deadLetterRoutingKey = "workflow.task.dead";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateRoutingKey() {
        return createRoutingKey;
    }

    public void setCreateRoutingKey(String createRoutingKey) {
        this.createRoutingKey = createRoutingKey;
    }

    public String getClaimRoutingKey() {
        return claimRoutingKey;
    }

    public void setClaimRoutingKey(String claimRoutingKey) {
        this.claimRoutingKey = claimRoutingKey;
    }

    public String getCompleteRoutingKey() {
        return completeRoutingKey;
    }

    public void setCompleteRoutingKey(String completeRoutingKey) {
        this.completeRoutingKey = completeRoutingKey;
    }

    public String getDeadLetterExchange() {
        return deadLetterExchange;
    }

    public void setDeadLetterExchange(String deadLetterExchange) {
        this.deadLetterExchange = deadLetterExchange;
    }

    public String getDeadLetterQueue() {
        return deadLetterQueue;
    }

    public void setDeadLetterQueue(String deadLetterQueue) {
        this.deadLetterQueue = deadLetterQueue;
    }

    public String getDeadLetterRoutingKey() {
        return deadLetterRoutingKey;
    }

    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) {
        this.deadLetterRoutingKey = deadLetterRoutingKey;
    }
}
