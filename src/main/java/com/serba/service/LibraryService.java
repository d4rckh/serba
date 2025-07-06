package com.serba.service;

import com.serba.domain.downloads.FileDownload;
import com.serba.domain.files.SystemFileFolder;
import com.serba.domain.files.SystemFileFolderType;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;
import com.serba.repository.LibraryRepository;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipOutputStream;
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

    if (relativePath == null
        || relativePath.isEmpty()
        || relativePath.equals("/")
        || relativePath.equals("\\")) {
      return base;
    }

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

  private Path createZipFromDirectory(Path directory) throws IOException {
    Path tempZip = Files.createTempFile("zipped-", ".zip");
    try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(tempZip))) {
      systemFilesService.zipDirectory(directory, directory.getFileName().toString(), zipOut);
      zipOut.finish();
    }
    return tempZip;
  }

  public FileDownload downloadLibraryFile(LibraryEntity library, String path, UserEntity user)
      throws IOException {
    Path safePath = resolveSafePath(library.getSystemLocation(), path);
    Path fileToDownload = safePath;
    boolean isTempZip = false;

    if (Files.isDirectory(safePath)) {
      fileToDownload = createZipFromDirectory(safePath);
      isTempZip = true;
    }

    long totalBytes = Files.size(fileToDownload);

    String downloadUuid =
        this.downloadTrackingService.startTracking(
            user, library, path, fileToDownload.toString(), totalBytes);

    FileCompletionHandler fileCompletionHandler =
        new FileCompletionHandler(this.downloadTrackingService, downloadUuid, isTempZip);

    FileDownload download =
        systemFilesService.downloadFileStream(
            fileToDownload.toString(),
            (read, total) -> this.downloadTrackingService.updateProgress(downloadUuid, read),
            fileCompletionHandler);

    String filename =
        isTempZip ? safePath.getFileName().toString() + ".zip" : safePath.getFileName().toString();

    return FileDownload.builder().stream(download.getStream()).filename(filename).build();
  }

  public void deleteLibrary(LibraryEntity libraryEntity) {
    this.libraryRepository.delete(libraryEntity);
  }
}
