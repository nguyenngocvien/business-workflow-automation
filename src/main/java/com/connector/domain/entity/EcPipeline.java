package com.connector.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "ec_pipeline")
public class EcPipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pipeline_code", unique = true, length = 100)
    private String pipelineCode;

    @Column(name = "pipeline_name", length = 255)
    private String pipelineName;

    @Column(columnDefinition = "text")
    private String description;

    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "pipeline")
    private List<EcPipelineStep> steps = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "pipeline")
    private List<EcScheduleJob> scheduleJobs = new ArrayList<>();
}
