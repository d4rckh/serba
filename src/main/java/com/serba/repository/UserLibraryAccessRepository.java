package com.serba.repository;

import java.util.List;
import java.util.Optional;

import com.serba.entity.UserLibraryAccessEntity;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface UserLibraryAccessRepository extends CrudRepository<UserLibraryAccessEntity, Long> {
  Optional<UserLibraryAccessEntity> findByUserIdAndLibraryId(Long userId, Long libraryId);

  List<UserLibraryAccessEntity> findByUserId(Long userId);

  List<UserLibraryAccessEntity> findByLibraryId(Long libraryId);
}
