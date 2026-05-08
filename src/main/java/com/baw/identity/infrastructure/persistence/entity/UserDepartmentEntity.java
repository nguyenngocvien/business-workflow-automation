package com.baw.identity.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_departments")
public class UserDepartmentEntity {

	@EmbeddedId
	private UserDepartmentId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("departmentId")
	@JoinColumn(name = "department_id", nullable = false)
	private DepartmentEntity department;

	@Column(name = "is_primary", nullable = false)
	private Boolean primary = Boolean.FALSE;
}
