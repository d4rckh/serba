package com.serba.repository;

import com.serba.domain.downloads.UserDownload;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;

import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InMemoryDownloadTrackingRepository implements DownloadTrackingRepository {

  private final Map<String, UserDownload> downloads = new ConcurrentHashMap<>();

  @Override
  public String trackDownload(UserEntity user, LibraryEntity library, String path, long totalBytes) {
    String uuid = UUID.randomUUID().toString();
    UserDownload download = UserDownload.builder()
        .user(user)
        .library(library)
        .path(path)
        .totalBytes(totalBytes)
        .bytesRead(0)
        .startedAt(Instant.now())
        .build();
    downloads.put(uuid, download);
    return uuid;
  }

  @Override
  public UserDownload updateDownloadProgress(String downloadUuid, long bytesRead) {
    return downloads.computeIfPresent(downloadUuid, (uuid, download) -> {
      download.setBytesRead(bytesRead);
      return download;
    });
  }

  @Override
  public UserDownload markCompleted(String downloadUuid) {
    return downloads.computeIfPresent(downloadUuid, (id, download) -> {
      download.setCompletedAt(Instant.now());
      return download;
    });
  }

  @Override
  public UserDownload markFailed(String downloadUuid) {
    return downloads.computeIfPresent(downloadUuid, (id, download) -> {
      download.setCompletedAt(Instant.now()); // Still mark as ended
      return download;
    });
  }

  @Override
  public List<UserDownload> findAll() {
    return downloads.values().stream().toList();
  }

  @Override
  public List<UserDownload> findByUser(UserEntity user) {
    return downloads.values().stream()
        .filter(d -> d.getUser().equals(user))
        .toList();
  }

  @Override
  public List<UserDownload> findByLibrary(LibraryEntity library) {
    return downloads.values().stream()
        .filter(d -> d.getLibrary().equals(library))
        .toList();
  }

  @Override
  public UserDownload findById(String downloadUuid) {
    return downloads.get(downloadUuid);
  }
}
