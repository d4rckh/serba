package com.serba.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.serba.domain.files.SystemFileFolder;
import com.serba.domain.files.SystemFileFolderType;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;
import com.serba.repository.LibraryRepository;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class LibraryService {
  private final LibraryRepository libraryRepository;
  private final SystemFilesService systemFilesService;
  private final DownloadTrackingService downloadTrackingService;

  public LibraryEntity createLibrary(LibraryEntity libraryEntity) {
    return this.libraryRepository.save(libraryEntity);
  }

  public LibraryEntity updateLibrary(LibraryEntity libraryEntity) {
    LibraryEntity existingLibrary = this.libraryRepository.findById(libraryEntity.getId())
        .orElseThrow(() -> new IllegalArgumentException("Library not found with id: " + libraryEntity.getId()));
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

public List<SystemFileFolder> getLibraryFiles(LibraryEntity library, String path) throws IOException {
    List<SystemFileFolder> contents = this.systemFilesService.listFolderContents(
        Paths.get(library.getSystemLocation(), path).toString());

    contents.sort(Comparator
        .comparing((SystemFileFolder f) -> f.getType() != SystemFileFolderType.FOLDER)
        .thenComparing(SystemFileFolder::getName, String.CASE_INSENSITIVE_ORDER));

    return contents;
}


  public InputStream downloadLibraryFile(LibraryEntity library, String path, UserEntity user) throws IOException {
    String fullPath = Paths.get(library.getSystemLocation(), path).toString();
    long totalBytes = java.nio.file.Files.size(Paths.get(fullPath));
    String downloadUuid = this.downloadTrackingService.startTracking(user, library, path, totalBytes);

    return systemFilesService.downloadFileStream(
        fullPath,
        (bytesRead, total) -> this.downloadTrackingService.updateProgress(downloadUuid, bytesRead),
        () -> this.downloadTrackingService.completeDownload(downloadUuid),
        () -> this.downloadTrackingService.failDownload(downloadUuid));
  }

  public void deleteLibrary(LibraryEntity libraryEntity) {
    this.libraryRepository.delete(libraryEntity);
  }
}
