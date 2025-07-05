package com.serba.domain.files;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

@Data
@Serdeable
public class SystemFileFolder {
  private String name;
  private String path;
  private long size;
  private SystemFileFolderType type;
}
