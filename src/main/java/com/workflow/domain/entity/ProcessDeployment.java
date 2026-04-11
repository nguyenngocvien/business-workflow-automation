package com.workflow.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "process_deployments")
public class ProcessDeployment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_definition_id", nullable = false)
    private ProcessDefinition processDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_version_id", nullable = false)
    private ProcessVersion processVersion;

    @Column(name = "deployment_id", nullable = false, length = 255)
    private String deploymentId;

    @Column(name = "deployed_by", length = 100)
    private String deployedBy;

    @Column(name = "deployed_at", nullable = false)
    private LocalDateTime deployedAt;

    @Column(name = "environment", length = 50)
    private String environment;

    // getters/setters
}