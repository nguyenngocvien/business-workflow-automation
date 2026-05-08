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
@Table(name = "audit_logs")
public class AuditLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "entity_type", nullable = false, length = 100)
	private String entityType;

	@Column(name = "entity_id", nullable = false, length = 255)
	private String entityId;

	@Column(name = "action", nullable = false, length = 100)
	private String action;

	@Column(name = "performed_by", length = 255)
	private String performedBy;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "details", columnDefinition = "jsonb")
	private Map<String, Object> details;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;
}
