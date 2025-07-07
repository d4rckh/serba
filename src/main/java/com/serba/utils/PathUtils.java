package com.serba.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {
  public static Path resolveSafePath(String baseDir, String relativePath) {
    Path base = Paths.get(baseDir).toAbsolutePath().normalize();

    if (relativePath == null
        || relativePath.isEmpty()
        || relativePath.equals("/")
        || relativePath.equals("\\")) {
      return base;
    }

    while (relativePath.startsWith("/") || relativePath.startsWith("\\")) {
      relativePath = relativePath.substring(1);
    }

    Path resolved = base.resolve(relativePath).normalize();

    if (!resolved.startsWith(base)) {
      throw new SecurityException("Path traversal attempt detected: " + relativePath);
    }

    return resolved;
  }
}
