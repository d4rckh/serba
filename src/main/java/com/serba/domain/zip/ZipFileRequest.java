package com.serba.domain.zip;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

@Data
@Serdeable
public class ZipFileRequest {
  private Long libraryId;
  private String libraryPath;
}
