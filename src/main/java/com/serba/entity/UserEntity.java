package com.serba.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "users")
@Data
@Serdeable
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String hashedPassword;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
}
