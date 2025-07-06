package com.serba.streams;

import com.serba.service.FileCompletionHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;

public class ProgressInputStream extends InputStream {
  private final InputStream delegate;
  private final BiConsumer<Long, Long> onProgress;
  private final FileCompletionHandler handler;

  private long bytesRead = 0;
  private final long totalSize;
  private boolean completed = false;
  private boolean closed = false;

  public ProgressInputStream(
      InputStream delegate,
      long totalSize,
      BiConsumer<Long, Long> onProgress,
      FileCompletionHandler handler) {
    this.delegate = delegate;
    this.totalSize = totalSize;
    this.onProgress = onProgress;
    this.handler = handler;
  }

  @Override
  public int read() throws IOException {
    int byteRead = delegate.read();
    if (byteRead != -1) {
      bytesRead++;
      onProgress.accept(bytesRead, totalSize);
      checkCompletion();
    }
    return byteRead;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int n = delegate.read(b, off, len);
    if (n > 0) {
      bytesRead += n;
      onProgress.accept(bytesRead, totalSize);
      checkCompletion();
    }
    return n;
  }

  private void checkCompletion() {
    if (!completed && bytesRead >= totalSize) {
      completed = true;
      handler.run();
    }
  }

  @Override
  public void close() throws IOException {
    if (!closed) {
      closed = true;
      delegate.close();
      if (!completed) {
        handler.setSuccessful(false);
        handler.run();
      }
    }
  }
}
