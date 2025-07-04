package com.serba.repository;

import java.util.Optional;

import com.serba.entity.UserEntity;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);
}