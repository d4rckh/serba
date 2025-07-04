package com.serba.repository;

import java.util.List;

import com.serba.domain.downloads.UserDownload;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;

public interface DownloadTrackingRepository {
  String trackDownload(UserEntity user, LibraryEntity library, String path, long totalBytes);

  UserDownload updateDownloadProgress(String downloadUuid, long bytesRead);

  List<UserDownload> findAll();

  List<UserDownload> findByUserId(Long userId);

  List<UserDownload> findByLibraryId(Long libraryId);

  UserDownload findById(String downloadUuid);

  UserDownload markCompleted(String downloadUuid);

  UserDownload markFailed(String downloadUuid);
}
