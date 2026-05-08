package com.baw.identity.infrastructure.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class GroupRoleId implements Serializable {

	@Column(name = "group_id")
	private Long groupId;

	@Column(name = "role_id")
	private Long roleId;
}
