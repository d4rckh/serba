package com.serba.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Singleton;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Singleton
@Entity
@Table(name = "libraries")
@Serdeable
@Data
public class LibraryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String systemLocation;
}
