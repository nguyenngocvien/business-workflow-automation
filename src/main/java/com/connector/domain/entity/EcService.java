package com.connector.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.connector.domain.enums.ServiceType;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "ec_service")
public class EcService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_code", nullable = false, unique = true, length = 100)
    private String serviceCode;

    @Column(name = "service_name", length = 255)
    private String serviceName;

    @Column(name = "service_type", length = 50)
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "service_version", length = 50)
    private String serviceVersion;

    @Column(name = "app_id", length = 100)
    private String appId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_id")
    private EcConnection connection;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_json", nullable = false, columnDefinition = "jsonb")
    private String configJson;

    @Column(name = "timeout_ms")
    private Integer timeoutMs;

    @Column(name = "retry_count")
    private Integer retryCount;

    private Boolean active;

    @Column(name = "log_enable")
    private Boolean logEnable;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Builder.Default
    @OneToMany(mappedBy = "service")
    private List<EcPipelineStep> pipelineSteps = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "service")
    private List<EcScheduleJob> scheduleJobs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "service")
    private List<EcLog> logs = new ArrayList<>();
}
