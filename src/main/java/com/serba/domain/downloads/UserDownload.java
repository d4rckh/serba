package com.serba.domain.downloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;
import io.micronaut.serde.annotation.Serdeable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;

@Data
@Serdeable
@Builder
public class UserDownload {
  private UserEntity user;
  private LibraryEntity library;
  private String path;
  private String realSystemPath;

  private long bytesRead;
  private long totalBytes;

  private Instant startedAt;
  private Instant completedAt;

  @JsonProperty("averageSpeed")
  public long getAverageSpeed() {
    if (startedAt == null || bytesRead <= 0) {
      return 0;
    }
    long seconds =
        Duration.between(startedAt, Objects.isNull(completedAt) ? Instant.now() : completedAt)
            .getSeconds();
    return seconds > 0 ? bytesRead / seconds : bytesRead;
  }
}
