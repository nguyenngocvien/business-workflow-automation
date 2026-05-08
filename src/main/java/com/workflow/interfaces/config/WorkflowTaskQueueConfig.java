package com.workflow.interfaces.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WorkflowTaskQueueProperties.class)
public class WorkflowTaskQueueConfig {

    @Bean
    public DirectExchange workflowTaskExchange(WorkflowTaskQueueProperties properties) {
        return new DirectExchange(properties.getExchange(), true, false);
    }

    @Bean
    public Queue workflowTaskQueue(WorkflowTaskQueueProperties properties) {
        return QueueBuilder.durable(properties.getName())
                .deadLetterExchange(properties.getDeadLetterExchange())
                .deadLetterRoutingKey(properties.getDeadLetterRoutingKey())
                .build();
    }

    @Bean
    public DirectExchange workflowTaskDeadLetterExchange(WorkflowTaskQueueProperties properties) {
        return new DirectExchange(properties.getDeadLetterExchange(), true, false);
    }

    @Bean
    public Queue workflowTaskDeadLetterQueue(WorkflowTaskQueueProperties properties) {
        return QueueBuilder.durable(properties.getDeadLetterQueue()).build();
    }

    @Bean
    public Binding workflowTaskCreateBinding(
            Queue workflowTaskQueue,
            DirectExchange workflowTaskExchange,
            WorkflowTaskQueueProperties properties) {
        return BindingBuilder.bind(workflowTaskQueue).to(workflowTaskExchange).with(properties.getCreateRoutingKey());
    }

    @Bean
    public Binding workflowTaskClaimBinding(
            Queue workflowTaskQueue,
            DirectExchange workflowTaskExchange,
            WorkflowTaskQueueProperties properties) {
        return BindingBuilder.bind(workflowTaskQueue).to(workflowTaskExchange).with(properties.getClaimRoutingKey());
    }

    @Bean
    public Binding workflowTaskCompleteBinding(
            Queue workflowTaskQueue,
            DirectExchange workflowTaskExchange,
            WorkflowTaskQueueProperties properties) {
        return BindingBuilder.bind(workflowTaskQueue).to(workflowTaskExchange).with(properties.getCompleteRoutingKey());
    }

    @Bean
    public Binding workflowTaskDeadLetterBinding(
            Queue workflowTaskDeadLetterQueue,
            DirectExchange workflowTaskDeadLetterExchange,
            WorkflowTaskQueueProperties properties) {
        return BindingBuilder.bind(workflowTaskDeadLetterQueue)
                .to(workflowTaskDeadLetterExchange)
                .with(properties.getDeadLetterRoutingKey());
    }
}
