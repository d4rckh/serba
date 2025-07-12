package com.serba.service;

import com.serba.domain.downloads.UserDownload;

import jakarta.annotation.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FileCompletionHandler implements Runnable {

  private final String downloadUuid;
  private final boolean tempFile;
  @Nullable private final String jobId;

  @Setter
  private boolean successful = true;

  @Setter
  private DownloadTrackingService downloadTrackingService;
  
  @Setter
  private JobService jobService;


  @Override
  public void run() {
    try {
      UserDownload userDownload = this.downloadTrackingService.findById(downloadUuid);

      if (this.successful) {
        this.downloadTrackingService.completeDownload(downloadUuid);
      } else {
        this.downloadTrackingService.failDownload(downloadUuid);
      }

      if (this.tempFile) {
        try {
          Files.deleteIfExists(Paths.get(userDownload.getRealSystemPath()));
          
          if (!Objects.isNull(jobId))
            jobService.deletebyId(jobId);
          
          log.info("Deleted temp file: {}", userDownload.getRealSystemPath());
        } catch (IOException e) {
          log.warn("Failed to delete temp file: {}", userDownload.getRealSystemPath(), e);
        }
      }
    } catch (Exception e) {
      log.info(downloadUuid, e);
    }
  }
}
