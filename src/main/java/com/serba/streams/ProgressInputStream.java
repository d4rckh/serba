package com.serba.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;

public class ProgressInputStream extends InputStream {
  private final InputStream delegate;
  private final BiConsumer<Long, Long> onProgress;
  private final Runnable onComplete;
  private final Runnable onCloseEarly;

  private long bytesRead = 0;
  private final long totalSize;
  private boolean completed = false;
  private boolean closed = false;

  public ProgressInputStream(
      InputStream delegate,
      long totalSize,
      BiConsumer<Long, Long> onProgress,
      Runnable onComplete,
      Runnable onCloseEarly) {
    this.delegate = delegate;
    this.totalSize = totalSize;
    this.onProgress = onProgress;
    this.onComplete = onComplete;
    this.onCloseEarly = onCloseEarly;
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
      onComplete.run();
    }
  }

  @Override
  public void close() throws IOException {
    if (!closed) {
      closed = true;
      delegate.close();
      if (!completed) {
        onCloseEarly.run();
      }
    }
  }
}
