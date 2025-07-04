package com.serba.controller;

import java.util.List;

import com.serba.domain.downloads.UserDownload;
import com.serba.service.DownloadTrackingService;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;

@Controller("tracking")
@RequiredArgsConstructor
public class DownloadTrackingController {
  private final DownloadTrackingService downloadTrackingService;

  @Get
  @Secured(SecurityRule.IS_AUTHENTICATED)
  List<UserDownload> getDownloadTracking() {
    return downloadTrackingService.findAll();
  }

  @Get("user/{userId}")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  List<UserDownload> getUserDownloadTracking(Long userId) {
    return downloadTrackingService.findByUserId(userId);
  }

  @Get("library/{libraryId}")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  List<UserDownload> getLibraryDownloadTracking(Long libraryId) {
    return downloadTrackingService.findByLibraryId(libraryId);
  }
}
