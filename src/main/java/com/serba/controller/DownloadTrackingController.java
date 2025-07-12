package com.serba.controller;

import com.serba.domain.downloads.UserDownload;
import com.serba.service.DownloadTrackingService;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Controller("tracking")
@RequiredArgsConstructor
public class DownloadTrackingController {
  private final DownloadTrackingService downloadTrackingService;

  @Get
  @Secured("SUPER")
  List<UserDownload> getDownloadTracking() {
    return downloadTrackingService.findAll();
  }

  @Delete("{downloadUuid}")
  @Secured("SUPER")
  void deleteDownloadTracking(@PathVariable String downloadUuid) {
    downloadTrackingService.delete(downloadUuid);
  }

  @Get("user/{userId}")
  @Secured("SUPER")
  List<UserDownload> getUserDownloadTracking(Long userId) {
    return downloadTrackingService.findByUserId(userId);
  }

  @Get("library/{libraryId}")
  @Secured("SUPER")
  List<UserDownload> getLibraryDownloadTracking(Long libraryId) {
    return downloadTrackingService.findByLibraryId(libraryId);
  }
}
