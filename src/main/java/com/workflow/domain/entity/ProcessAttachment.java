package com.workflow.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "process_attachment")
public class ProcessAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private UserTask task;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    // getters/setters
}