package com.serba.service;

import java.util.List;
import java.util.Optional;

import com.serba.entity.LibraryEntity;
import com.serba.repository.LibraryRepository;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class LibraryService {
  
  private final LibraryRepository libraryRepository;
  
  public LibraryEntity createLibrary(LibraryEntity libraryEntity) {
    return libraryRepository.save(libraryEntity);
  }

  public LibraryEntity updateLibrary(LibraryEntity libraryEntity) {
    return libraryRepository.update(libraryEntity);
  }

  public Optional<LibraryEntity> findById(Long id) {
    return this.libraryRepository.findById(id);
  }

  public Optional<LibraryEntity> findByName(String name) {
    return this.libraryRepository.findByName(name);
  }

  public Optional<LibraryEntity> findBySystemLocation(String location) {
    return this.libraryRepository.findBySystemLocation(location);
  }

  public List<LibraryEntity> findAll() {
    return this.libraryRepository.findAll();
  }
}
