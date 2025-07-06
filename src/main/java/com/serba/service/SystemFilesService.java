package com.serba.service;

import com.serba.domain.files.SystemFileFolder;
import com.serba.domain.files.SystemFileFolderType;
import com.serba.streams.ProgressInputStream;
import jakarta.inject.Singleton;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Singleton
public class SystemFilesService {

  public List<SystemFileFolder> listFolderContents(String directoryPath) throws IOException {
    Path dir = Paths.get(directoryPath);

    if (!Files.exists(dir) || !Files.isDirectory(dir)) {
      throw new IllegalArgumentException("Invalid directory: " + directoryPath);
    }

    List<SystemFileFolder> result = new ArrayList<>();

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
      for (Path path : stream) {
        SystemFileFolder entry = new SystemFileFolder();
        entry.setName(path.getFileName().toString());
        entry.setPath(path.toAbsolutePath().toString());
        entry.setSize(Files.isDirectory(path) ? 0 : Files.size(path));
        entry.setType(
            Files.isDirectory(path) ? SystemFileFolderType.FOLDER : SystemFileFolderType.FILE);

        result.add(entry);
      }
    }

    return result;
  }

  public InputStream downloadFileStream(
      String path,
      BiConsumer<Long, Long> progressCallback,
      Runnable onComplete,
      Runnable onCloseEarly)
      throws IOException {

    Path filePath = Paths.get(path);

    if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
      throw new FileNotFoundException("File not found or is a directory: " + path);
    }

    long totalBytes = Files.size(filePath);
    InputStream raw = Files.newInputStream(filePath);

    return new ProgressInputStream(raw, totalBytes, progressCallback, onComplete, onCloseEarly);
  }
}
