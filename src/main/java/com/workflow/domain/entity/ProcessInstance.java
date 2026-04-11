package com.workflow.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.workflow.domain.enums.ProcessStatus;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "process_instance", indexes = {
        @Index(name = "idx_process_instance_status", columnList = "status"),
        @Index(name = "idx_process_instance_business_key", columnList = "business_key")
})
public class ProcessInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instance_id", unique = true, length = 64)
    private String instanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_definition_id", nullable = false)
    private ProcessDefinition processDefinition;

    @Column(name = "business_key", length = 150)
    private String businessKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ProcessStatus status;

    @Column(name = "current_step_code", length = 100)
    private String currentStepCode;

    @Column(name = "started_by", length = 100)
    private String startedBy;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}
