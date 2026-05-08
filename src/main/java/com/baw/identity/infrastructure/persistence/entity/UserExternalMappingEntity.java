package com.baw.identity.infrastructure.persistence.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
	name = "user_external_mappings",
	uniqueConstraints = @UniqueConstraint(name = "uk_provider_external_user", columnNames = {"provider_id", "external_user_id"})
)
public class UserExternalMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_id", nullable = false)
	private IdentityProviderEntity provider;

	@Column(name = "external_user_id", nullable = false, length = 255)
	private String externalUserId;

	@Column(name = "external_username", length = 255)
	private String externalUsername;

	@Column(name = "synced_at")
	private OffsetDateTime syncedAt;
}
