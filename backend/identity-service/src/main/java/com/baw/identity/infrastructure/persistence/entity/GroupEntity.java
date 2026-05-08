package com.baw.identity.infrastructure.persistence.entity;

import java.time.OffsetDateTime;

import com.baw.identity.domain.model.enums.GroupType;
import com.baw.identity.domain.model.enums.RecordSource;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class GroupEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, unique = true, length = 100)
	private String code;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_group_id")
	private GroupEntity parentGroup;

	@Enumerated(EnumType.STRING)
	@Column(name = "group_type", nullable = false, length = 50)
	private GroupType groupType;

	@Column(name = "path")
	private String path;

	@Column(name = "description")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "source", nullable = false, length = 50)
	private RecordSource source = RecordSource.LOCAL;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = Boolean.TRUE;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;
}
