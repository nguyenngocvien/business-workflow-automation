package com.dms.domain.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.dms.domain.enums.AttributeDataType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "file_attributes", uniqueConstraints = @UniqueConstraint(columnNames = { "key_code" }))
@Getter
@Setter
public class FileAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_code", nullable = false)
    private String keyCode;

    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    private AttributeDataType dataType;

    private Boolean isRequired;

    private Instant createdAt;
    private String createdBy;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileAttributeOption> options = new ArrayList<>();
}