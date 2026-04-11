package com.workflow.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "process_history")
public class ProcessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_instance_id")
    private ProcessInstance processInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private UserTask task;

    @Column(name = "action", length = 100)
    private String action;

    @Column(name = "action_by", length = 100)
    private String actionBy;

    @Column(name = "action_at")
    private LocalDateTime actionAt;

    @Column(name = "note", length = 500)
    private String note;
}
