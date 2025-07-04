package com.serba.repository;

import java.util.Optional;

import com.serba.entity.LibraryEntity;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface LibraryRepository extends CrudRepository<LibraryEntity, Long> {
  Optional<LibraryEntity> findByName(String name);
  Optional<LibraryEntity> findBySystemLocation(String systemLocation);
}
