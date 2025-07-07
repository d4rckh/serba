package com.serba.service;

import com.serba.domain.jobs.Job;
import com.serba.domain.jobs.JobType;
import com.serba.entity.LibraryEntity;
import com.serba.utils.PathUtils;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Singleton
@RequiredArgsConstructor
public class ZipService {
  private final JobService jobService;

  public Job createZipJob(LibraryEntity libraryEntity, String folderPath, Long userId) {
    Path systemPath = PathUtils.resolveSafePath(libraryEntity.getSystemLocation(), folderPath);

    Job job = new Job();
    job.setId(UUID.randomUUID().toString());
    job.setType(JobType.ZIP);
    job.setOwnedByUserIds(List.of(userId));
    job.setProgress(0);
    job.setAttrs(new HashMap<>(Map.of(
        "libraryId", libraryEntity.getId(),
        "libraryPath", folderPath,
        "systemPath", systemPath.toString()
    )));

    jobService.save(job);

    new Thread(() -> {
      try {
        Path zipFile = createZipFromDirectory(job);
        job.getAttrs().put("zipFilePath", zipFile.toString());
        job.setProgress(100);
        jobService.save(job);
      } catch (Exception e) {
        job.getAttrs().put("error", e.getMessage());
        job.setProgress(-1);
        jobService.save(job);
      }
    }).start();

    return job;
  }

  private Path createZipFromDirectory(Job job) throws IOException {
    Path directory = Paths.get((String) job.getAttrs().get("systemPath"));
    Path tempZip = Files.createTempFile("zipped-", ".zip");

    List<Path> files = Files.walk(directory)
        .filter(Files::isRegularFile)
        .toList();

    int totalFiles = files.size();
    AtomicInteger filesZipped = new AtomicInteger(0);
    
    try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(tempZip))) {
      for (Path file : files) {
        String entryName = directory.getFileName() + "/" + directory.relativize(file).toString().replace("\\", "/");
        zipOut.putNextEntry(new ZipEntry(entryName));
        Files.copy(file, zipOut);
        zipOut.closeEntry();

        int progress = (int) ((filesZipped.incrementAndGet() * 100.0) / totalFiles);
        job.setProgress(progress);
        jobService.save(job);
      }
    }

    return tempZip;
  }
}
