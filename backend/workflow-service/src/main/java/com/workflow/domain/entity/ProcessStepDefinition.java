package com.workflow.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "process_step_definition", indexes = @Index(name = "idx_step_def_process", columnList = "process_definition_id"))
public class ProcessStepDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_definition_id", nullable = false)
    private ProcessDefinition processDefinition;

    @Column(name = "step_name", nullable = false, length = 150)
    private String stepName;

    @Column(name = "step_code", nullable = false, length = 100)
    private String stepCode;

    @Column(name = "step_type", nullable = false, length = 50)
    private String stepType;

    @Column(name = "step_order")
    private Integer stepOrder;

    @Column(name = "next_step_code", length = 100)
    private String nextStepCode;

    @Column(name = "condition_expression", length = 500)
    private String conditionExpression;

    @Column(name = "sla_minutes")
    private Integer slaMinutes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
