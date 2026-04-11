package com.connector.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ec_pipeline_step")
public class EcPipelineStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    private EcPipeline pipeline;

    @Column(name = "step_order")
    private Integer stepOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private EcService service;

    @Column(name = "step_name", length = 255)
    private String stepName;

    @Column(name = "request_transform", columnDefinition = "text")
    private String requestTransform;

    @Column(name = "response_transform", columnDefinition = "text")
    private String responseTransform;

    @Column(name = "continue_on_error")
    private Boolean continueOnError;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
