package com.workflow.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "process_task", indexes = {
        @Index(name = "idx_process_task_assignee", columnList = "assignee"),
        @Index(name = "idx_process_task_status", columnList = "status"),
        @Index(name = "idx_process_task_task_id", columnList = "task_id")
})
public class UserTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_instance_id", nullable = false)
    private ProcessInstance processInstance;

    @Column(name = "task_id", unique = true, length = 64)
    private String taskId;

    @Column(name = "task_name", length = 150)
    private String taskName;

    @Column(name = "task_code", length = 100)
    private String taskCode;

    @Column(name = "assignee", length = 100)
    private String assignee;

    @Column(name = "candidate_group", length = 100)
    private String candidateGroup;

    @Column(name = "owner", length = 100)
    private String owner;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
