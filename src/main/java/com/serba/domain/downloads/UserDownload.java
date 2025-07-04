package com.serba.domain.downloads;

import java.time.Instant;

import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDownload {
  private UserEntity user;
  private LibraryEntity library;
  private String path;

  private long bytesRead;
  private long totalBytes;

  private Instant startedAt;
  private Instant completedAt;
}
