package com.serba.service;

import com.serba.domain.downloads.UserDownload;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FileCompletionHandler implements Runnable {

  private final DownloadTrackingService downloadTrackingService;
  private final String downloadUuid;
  private final boolean tempFile;
  private boolean successful = true;

  public void setSuccessful(boolean value) {
    this.successful = value;
  }

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
          log.info("Deleted temp file: {}", userDownload.getRealSystemPath());
        } catch (IOException e) {
          log.warn("Failed to delete temp file: {}", userDownload.getRealSystemPath(), e);
        }
      }
    } catch (Exception e) {
    }
  }
}
