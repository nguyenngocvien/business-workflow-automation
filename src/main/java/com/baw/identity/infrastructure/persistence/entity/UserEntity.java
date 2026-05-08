package com.baw.identity.infrastructure.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.baw.identity.domain.model.enums.RecordSource;
import com.baw.identity.domain.model.enums.UserStatus;

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
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "external_id", unique = true)
	private UUID externalId;

	@Column(name = "username", nullable = false, unique = true, length = 100)
	private String username;

	@Column(name = "email", unique = true, length = 255)
	private String email;

	@Column(name = "first_name", length = 100)
	private String firstName;

	@Column(name = "last_name", length = 100)
	private String lastName;

	@Column(name = "full_name", length = 255)
	private String fullName;

	@Column(name = "phone_number", length = 50)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private UserStatus status = UserStatus.ACTIVE;

	@Enumerated(EnumType.STRING)
	@Column(name = "source", nullable = false, length = 50)
	private RecordSource source = RecordSource.LOCAL;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = Boolean.FALSE;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;
}
