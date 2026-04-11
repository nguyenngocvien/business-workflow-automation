package com.workflow.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "process_task_identity_link", indexes = {
        @Index(name = "idx_process_task_identity_task", columnList = "task_id"),
        @Index(name = "idx_process_task_identity_group", columnList = "group_id"),
        @Index(name = "idx_process_task_identity_user", columnList = "user_id")
})
public class ProcessTaskIdentityLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private UserTask task;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
