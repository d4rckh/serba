package com.serba.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.serba.domain.files.SystemFileFolder;
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
    return this.libraryRepository.update(libraryEntity);
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
    return this.systemFilesService.listFolderContents(
        Paths.get(library.getSystemLocation(), path).toString());
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
}
