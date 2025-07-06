package com.serba.service;

import com.serba.domain.files.SystemFileFolder;
import com.serba.domain.files.SystemFileFolderType;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;
import com.serba.repository.LibraryRepository;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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
  private final DownloadTrackingService downloadTrackingService;

  private Path resolveSafePath(String baseDir, String relativePath) {
    Path base = Paths.get(baseDir).toAbsolutePath().normalize();

    // Handle empty or "/" path as the base directory itself
    if (relativePath == null || relativePath.isEmpty() || relativePath.equals("/")) {
      return base;
    }

    // Remove leading slashes to avoid resolving to root
    while (relativePath.startsWith("/") || relativePath.startsWith("\\")) {
      relativePath = relativePath.substring(1);
    }

    Path resolved = base.resolve(relativePath).normalize();

    if (!resolved.startsWith(base)) {
      throw new SecurityException("Path traversal attempt detected: " + relativePath);
    }

    return resolved;
  }

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
    Path safePath = resolveSafePath(library.getSystemLocation(), path);

    List<SystemFileFolder> contents =
        this.systemFilesService.listFolderContents(safePath.toString());

    contents.sort(
        Comparator.comparing((SystemFileFolder f) -> f.getType() != SystemFileFolderType.FOLDER)
            .thenComparing(SystemFileFolder::getName, String.CASE_INSENSITIVE_ORDER));

    return contents;
  }

  public InputStream downloadLibraryFile(LibraryEntity library, String path, UserEntity user)
      throws IOException {
    Path safePath = resolveSafePath(library.getSystemLocation(), path);
    long totalBytes = java.nio.file.Files.size(safePath);

    String downloadUuid =
        this.downloadTrackingService.startTracking(user, library, path, totalBytes);

    return systemFilesService.downloadFileStream(
        safePath.toString(),
        (read, total) -> this.downloadTrackingService.updateProgress(downloadUuid, read),
        () -> this.downloadTrackingService.completeDownload(downloadUuid),
        () -> this.downloadTrackingService.failDownload(downloadUuid));
  }

  public void deleteLibrary(LibraryEntity libraryEntity) {
    this.libraryRepository.delete(libraryEntity);
  }
}
