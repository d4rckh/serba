package com.serba.service;

import com.serba.domain.downloads.UserDownload;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;
import com.serba.repository.DownloadTrackingRepository;
import jakarta.inject.Singleton;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class DownloadTrackingService {
  private final DownloadTrackingRepository downloadTrackingRepository;

  public String startTracking(
      UserEntity user, LibraryEntity library, String path, String realSystemPath, long totalBytes) {
    log.info(
        "User {} is starting download of {} from library {} with total bytes: {}",
        user.getUsername(),
        path,
        library.getName(),
        totalBytes);
    return downloadTrackingRepository.trackDownload(
        user, library, path, realSystemPath, totalBytes);
  }

  public void completeDownload(String downloadUuid) {
    downloadTrackingRepository.markCompleted(downloadUuid);
    log.info("Download {} marked as completed.", downloadUuid);
  }

  public void failDownload(String downloadUuid) {
    downloadTrackingRepository.markFailed(downloadUuid);
    log.warn("Download {} marked as failed/interrupted.", downloadUuid);
  }

  public void updateProgress(String downloadUuid, long bytesRead) {
    UserDownload download =
        downloadTrackingRepository.updateDownloadProgress(downloadUuid, bytesRead);

    if (download.getBytesRead() >= download.getTotalBytes()) {
      log.info(
          "Download {} completed for user {} from library {}, average speed: {} bytes/s",
          downloadUuid,
          download.getUser().getUsername(),
          download.getLibrary().getName(),
          download.getTotalBytes()
              / (System.currentTimeMillis() - download.getStartedAt().toEpochMilli())
              * 1000);
    }
  }

  public List<UserDownload> findAll() {
    return downloadTrackingRepository.findAll();
  }

  public UserDownload findById(String id) {
    return downloadTrackingRepository.findById(id);
  }

  public List<UserDownload> findByUserId(Long id) {
    return downloadTrackingRepository.findByUserId(id);
  }

  public List<UserDownload> findByLibraryId(Long id) {
    return downloadTrackingRepository.findByLibraryId(id);
  }

  public void delete(String downloadUuid) {
    downloadTrackingRepository.deleteById(downloadUuid);
  }
}
