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
@Table(name = "delegations")
public class DelegationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assignor_user_id", nullable = false)
	private UserEntity assignorUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delegate_user_id", nullable = false)
	private UserEntity delegateUser;

	@Column(name = "start_time", nullable = false)
	private OffsetDateTime startTime;

	@Column(name = "end_time", nullable = false)
	private OffsetDateTime endTime;

	@Column(name = "reason")
	private String reason;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;
}
