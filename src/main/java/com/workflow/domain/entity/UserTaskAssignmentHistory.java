package com.workflow.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "process_task_assignment_history", indexes = @Index(name = "idx_process_task_assign_hist_task", columnList = "task_id"))
public class UserTaskAssignmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private UserTask task;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "from_user", length = 100)
    private String fromUser;

    @Column(name = "to_user", length = 100)
    private String toUser;

    @Column(name = "from_group")
    private Long fromGroup;

    @Column(name = "to_group")
    private Long toGroup;

    @Column(name = "action_by", length = 100)
    private String actionBy;

    @Column(name = "action_at")
    private LocalDateTime actionAt;

    @Column(name = "comment", length = 500)
    private String comment;
}
