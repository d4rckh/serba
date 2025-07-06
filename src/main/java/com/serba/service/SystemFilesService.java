package com.serba.service;

import com.serba.domain.downloads.FileDownload;
import com.serba.domain.files.SystemFileFolder;
import com.serba.domain.files.SystemFileFolderType;
import com.serba.streams.ProgressInputStream;
import jakarta.inject.Singleton;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

  public FileDownload downloadFileStream(
      String path,
      BiConsumer<Long, Long> progressCallback,
      FileCompletionHandler fileCompletionHandler)
      throws IOException {

    Path filePath = Paths.get(path);

    if (!Files.exists(filePath)) {
      fileCompletionHandler.setSuccessful(false);
      fileCompletionHandler.run();
      throw new FileNotFoundException("File not found: " + path);
    }

    if (!Files.isRegularFile(filePath)) {
      fileCompletionHandler.setSuccessful(false);
      fileCompletionHandler.run();
      throw new IOException("Path is not a file: " + path);
    }

    long totalBytes = Files.size(filePath);
    InputStream raw = Files.newInputStream(filePath);
    InputStream stream =
        new ProgressInputStream(raw, totalBytes, progressCallback, fileCompletionHandler);
    String filename = filePath.getFileName().toString();
    return FileDownload.builder().stream(stream).filename(filename).build();
  }

  public void zipDirectory(Path sourceDir, String baseName, ZipOutputStream zipOut)
      throws IOException {
    Files.walk(sourceDir)
        .forEach(
            path -> {
              try {
                String entryName =
                    baseName + "/" + sourceDir.relativize(path).toString().replace("\\", "/");
                if (Files.isDirectory(path)) {
                  if (Files.list(path).findAny().isEmpty()) {
                    zipOut.putNextEntry(new ZipEntry(entryName + "/"));
                    zipOut.closeEntry();
                  }
                  return;
                }
                zipOut.putNextEntry(new ZipEntry(entryName));
                Files.copy(path, zipOut);
                zipOut.closeEntry();
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            });
  }
}
