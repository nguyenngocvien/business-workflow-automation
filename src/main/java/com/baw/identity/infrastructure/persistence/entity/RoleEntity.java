package com.baw.identity.infrastructure.persistence.entity;

import java.time.OffsetDateTime;

import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.RoleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "roles")
public class RoleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, unique = true, length = 100)
	private String code;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "role_type", nullable = false, length = 50)
	private RoleType roleType;

	@Column(name = "description")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "source", nullable = false, length = 50)
	private RecordSource source = RecordSource.LOCAL;

	@Column(name = "is_system", nullable = false)
	private Boolean isSystem = Boolean.FALSE;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;
}
