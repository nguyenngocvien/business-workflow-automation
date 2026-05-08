package com.baw.identity.infrastructure.persistence.entity;

import java.time.OffsetDateTime;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sync_jobs")
public class SyncJobEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_id", nullable = false)
	private IdentityProviderEntity provider;

	@Column(name = "job_type", nullable = false, length = 50)
	private String jobType;

	@Column(name = "started_at", nullable = false)
	private OffsetDateTime startedAt;

	@Column(name = "completed_at")
	private OffsetDateTime completedAt;

	@Column(name = "status", nullable = false, length = 20)
	private String status;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "summary", columnDefinition = "jsonb")
	private Map<String, Object> summary;
}
