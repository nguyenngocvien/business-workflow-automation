package com.workflow.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "process_task_data_history")
public class ProcessTaskDataHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private UserTask task;

    @Column(name = "data_json", columnDefinition = "jsonb")
    private String dataJson;

    @Column(name = "changed_by", length = 100)
    private String changedBy;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "version")
    private Integer version;
}
