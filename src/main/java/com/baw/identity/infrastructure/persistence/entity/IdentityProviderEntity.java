package com.baw.identity.infrastructure.persistence.entity;

import java.time.OffsetDateTime;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "identity_providers")
public class IdentityProviderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "provider_code", nullable = false, unique = true, length = 50)
	private String providerCode;

	@Column(name = "provider_name", nullable = false, length = 255)
	private String providerName;

	@Column(name = "provider_type", nullable = false, length = 50)
	private String providerType;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "config", columnDefinition = "jsonb")
	private Map<String, Object> config;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = Boolean.TRUE;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;
}
