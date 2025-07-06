package com.serba.repository;

import com.serba.entity.LibraryEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@Repository
public interface LibraryRepository extends CrudRepository<LibraryEntity, Long> {
  Optional<LibraryEntity> findByName(String name);

  Optional<LibraryEntity> findBySystemLocation(String systemLocation);
}
