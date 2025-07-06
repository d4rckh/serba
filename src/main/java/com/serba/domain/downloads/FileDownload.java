package com.serba.domain.downloads;

import java.io.InputStream;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDownload {
  private InputStream stream;
  private String filename;
}
