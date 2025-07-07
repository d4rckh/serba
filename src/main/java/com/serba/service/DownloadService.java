package com.serba.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.serba.domain.downloads.FileDownload;
import com.serba.domain.jobs.Job;
import com.serba.domain.jobs.JobType;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;
import com.serba.utils.PathUtils;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class DownloadService {
  private final DownloadTrackingService downloadTrackingService;
  private final SystemFilesService systemFilesService;
  private final JobService jobService;
  private final LibraryService libraryService;

  public FileDownload downloadLibraryFile(LibraryEntity library, String path, UserEntity user)
      throws IOException {
    Path safePath = PathUtils.resolveSafePath(library.getSystemLocation(), path);

    if (Files.isDirectory(safePath)) {
      throw new IllegalArgumentException("Cannot download directory directly. Submit a zip job first.");
    }

    long totalBytes = Files.size(safePath);
    String downloadUuid = this.downloadTrackingService.startTracking(
        user, library, path, safePath.toString(), totalBytes);

    FileCompletionHandler fileCompletionHandler = new FileCompletionHandler(downloadUuid, false, null);

    fileCompletionHandler.setDownloadTrackingService(downloadTrackingService);
    fileCompletionHandler.setJobService(jobService);

    FileDownload download = systemFilesService.downloadFileStream(
        safePath.toString(),
        (read, total) -> this.downloadTrackingService.updateProgress(downloadUuid, read),
        fileCompletionHandler);

    return FileDownload.builder()
        .stream(download.getStream())
        .filename(safePath.getFileName().toString())
        .build();
  }

  public FileDownload downloadZippedJob(UserEntity userEntity, String jobId) throws IOException {
    Job job = jobService.findById(jobId);

    if (!job.getType().equals(JobType.ZIP)) {
      throw new IllegalArgumentException("Unexpected job type: " + job.getType());
    }

    if (job.getProgress() < 100) {
      throw new IllegalStateException("Job not finished yet.");
    }

    Object pathObj = job.getAttrs().get("zipFilePath");
    if (!(pathObj instanceof String pathStr)) {
      throw new IllegalStateException("Missing or invalid zip file path in job attributes.");
    }

    // Extract metadata
    String originalPath = job.getAttrs().getOrDefault("libraryPath", "unknown").toString();
    String filename = job.getAttrs().getOrDefault("filename", Paths.get(pathStr).getFileName().toString()).toString();
    Object libraryIdObj = job.getAttrs().get("libraryId");

    if (!(libraryIdObj instanceof Long libraryId)) {
      throw new IllegalStateException("Missing or invalid libraryId in job attributes.");
    }

    LibraryEntity library = libraryService.findById(libraryId).orElseThrow(); // You must implement this

    long totalBytes;
    try {
      totalBytes = Files.size(Path.of(pathStr));
    } catch (IOException e) {
      throw new RuntimeException("Could not read zip file", e);
    }

    String downloadUuid = downloadTrackingService.startTracking(
        userEntity, library, originalPath, pathStr, totalBytes);

    FileCompletionHandler handler = new FileCompletionHandler(downloadUuid, true, job.getId());

    handler.setDownloadTrackingService(downloadTrackingService);
    handler.setJobService(jobService);

    FileDownload download = systemFilesService.downloadFileStream(
        pathStr,
        (read, total) -> downloadTrackingService.updateProgress(downloadUuid, read),
        handler);

    return FileDownload.builder()
        .stream(download.getStream())
        .filename(filename)
        .build();
  }

}
