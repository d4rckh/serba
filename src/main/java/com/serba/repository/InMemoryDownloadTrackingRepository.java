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
  public String trackDownload(
      UserEntity user, LibraryEntity library, String path, String realSystemPath, long totalBytes) {
    String uuid = UUID.randomUUID().toString();
    UserDownload download = UserDownload.builder()
        .user(user)
        .library(library)
        .path(path)
        .realSystemPath(realSystemPath)
        .totalBytes(totalBytes)
        .bytesRead(0)
        .startedAt(Instant.now())
        .uuid(uuid)
        .build();

    downloads.put(uuid, download);
    return uuid;
  }

  @Override
  public UserDownload updateDownloadProgress(String downloadUuid, long bytesRead) {
    return downloads.computeIfPresent(
        downloadUuid,
        (key, download) -> {
          download.setBytesRead(bytesRead);
          return download;
        });
  }

  @Override
  public UserDownload markCompleted(String downloadUuid) {
    return downloads.computeIfPresent(
        downloadUuid,
        (key, download) -> {
          download.setCompletedAt(Instant.now());
          return download;
        });
  }

  @Override
  public UserDownload markFailed(String downloadUuid) {
    return downloads.computeIfPresent(
        downloadUuid,
        (key, download) -> {
          download.setCompletedAt(Instant.now());
          return download;
        });
  }

  @Override
  public List<UserDownload> findAll() {
    return downloads
        .values()
        .stream()
        .sorted((a, b) -> b.getStartedAt().compareTo(a.getStartedAt()))
        .toList();
  }

  @Override
  public List<UserDownload> findByUserId(Long userId) {
    return downloads.values().stream().filter(d -> d.getUser().getId().equals(userId)).toList();
  }

  @Override
  public List<UserDownload> findByLibraryId(Long libraryId) {
    return downloads.values().stream()
        .filter(d -> d.getLibrary().getId().equals(libraryId))
        .toList();
  }

  @Override
  public UserDownload findById(String downloadUuid) {
    return downloads.get(downloadUuid);
  }

  @Override
  public void deleteById(String downloadUuid) {
    downloads.remove(downloadUuid);
  }
}
