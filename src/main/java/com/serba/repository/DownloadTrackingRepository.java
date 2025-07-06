package com.serba.repository;

import com.serba.domain.downloads.UserDownload;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;
import java.util.List;

public interface DownloadTrackingRepository {
  String trackDownload(
      UserEntity user, LibraryEntity library, String path, String realSystemPath, long totalBytes);

  UserDownload updateDownloadProgress(String downloadUuid, long bytesRead);

  List<UserDownload> findAll();

  List<UserDownload> findByUserId(Long userId);

  List<UserDownload> findByLibraryId(Long libraryId);

  UserDownload findById(String downloadUuid);

  UserDownload markCompleted(String downloadUuid);

  UserDownload markFailed(String downloadUuid);
}
