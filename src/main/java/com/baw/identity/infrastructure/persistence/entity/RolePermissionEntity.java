package com.baw.identity.infrastructure.persistence.entity;

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
@Table(name = "role_permissions")
public class RolePermissionEntity {

	@EmbeddedId
	private RolePermissionId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("roleId")
	@JoinColumn(name = "role_id", nullable = false)
	private RoleEntity role;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("permissionId")
	@JoinColumn(name = "permission_id", nullable = false)
	private PermissionEntity permission;
}
