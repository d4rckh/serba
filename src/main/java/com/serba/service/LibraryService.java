package com.serba.service;

import com.serba.domain.files.SystemFileFolder;
import com.serba.domain.files.SystemFileFolderType;
import com.serba.entity.LibraryEntity;
import com.serba.repository.LibraryRepository;
import com.serba.utils.PathUtils;

import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class LibraryService {
  private final LibraryRepository libraryRepository;
  private final SystemFilesService systemFilesService;

  public LibraryEntity createLibrary(LibraryEntity libraryEntity) {
    return this.libraryRepository.save(libraryEntity);
  }

  public LibraryEntity updateLibrary(LibraryEntity libraryEntity) {
    LibraryEntity existingLibrary =
        this.libraryRepository
            .findById(libraryEntity.getId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Library not found with id: " + libraryEntity.getId()));
    existingLibrary.setName(libraryEntity.getName());
    existingLibrary.setSystemLocation(libraryEntity.getSystemLocation());
    return this.libraryRepository.update(existingLibrary);
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

  public List<SystemFileFolder> getLibraryFiles(LibraryEntity library, String path)
      throws IOException {
    Path safePath = PathUtils.resolveSafePath(library.getSystemLocation(), path);

    List<SystemFileFolder> contents =
        this.systemFilesService.listFolderContents(safePath.toString());

    contents.sort(
        Comparator.comparing((SystemFileFolder f) -> f.getType() != SystemFileFolderType.FOLDER)
            .thenComparing(SystemFileFolder::getName, String.CASE_INSENSITIVE_ORDER));

    return contents;
  }

  public void deleteLibrary(LibraryEntity libraryEntity) {
    this.libraryRepository.delete(libraryEntity);
  }
}
