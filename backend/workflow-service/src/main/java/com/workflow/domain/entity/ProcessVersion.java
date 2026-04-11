package com.workflow.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

import com.workflow.domain.enums.DeployStatus;

@Entity
@Getter
@Setter
@Table(name = "process_versions", uniqueConstraints = @UniqueConstraint(name = "uq_process_version", columnNames = {
        "process_definition_id", "version" }), indexes = {
                @Index(name = "idx_versions_definition_status", columnList = "process_definition_id,status"),
                @Index(name = "idx_versions_deployment_id", columnList = "deployment_id")
        })
public class ProcessVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_definition_id", nullable = false)
    private ProcessDefinition processDefinition;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Lob
    @Column(name = "bpmn_xml", nullable = false)
    private String bpmnXml;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DeployStatus status;

    @Column(name = "camunda_definition_key", length = 255)
    private String camundaDefinitionKey;

    @Column(name = "camunda_version")
    private Integer camundaVersion;

    @Column(name = "deployment_id", length = 255)
    private String deploymentId;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deployed_at")
    private LocalDateTime deployedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null)
            this.status = DeployStatus.DRAFT;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
