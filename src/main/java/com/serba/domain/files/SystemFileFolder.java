package com.serba.domain.files;

import java.util.List;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

@Data
@Serdeable
public class SystemFileFolder {
  private String name;
  private String path;
  
  private SystemFileFolderType type;
}
