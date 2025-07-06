package com.serba.controller;

import com.serba.domain.downloads.FileDownload;
import com.serba.domain.files.SystemFileFolder;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;
import com.serba.security.AuthorizationUtils;
import com.serba.service.LibraryService;
import com.serba.service.UserLibraryAccessService;
import com.serba.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Controller("libraries")
@RequiredArgsConstructor
public class LibraryController {
  private final LibraryService libraryService;
  private final UserService userService;
  private final UserLibraryAccessService userLibraryAccessService;

  @Post
  @Secured("SUPER")
  LibraryEntity createLibrary(@Body LibraryEntity libraryEntity) {
    return libraryService.createLibrary(libraryEntity);
  }

  @Put
  @Secured("SUPER")
  LibraryEntity updateLibrary(@Body LibraryEntity libraryEntity) {
    return libraryService.updateLibrary(libraryEntity);
  }

  @Get
  @Secured(SecurityRule.IS_AUTHENTICATED)
  List<LibraryEntity> findAll(Authentication authentication) {
    return libraryService.findAll().stream()
        .filter(
            lib ->
                userLibraryAccessService.hasViewAccess(
                    (Long) authentication.getAttributes().get("UID"), lib.getId()))
        .toList();
  }

  @Get("{id}")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  LibraryEntity findById(@PathVariable Long id, Authentication authentication) {
    AuthorizationUtils.unauthorizedIfFalse(
        userLibraryAccessService.hasViewAccess(
            (Long) authentication.getAttributes().get("UID"), id));

    return libraryService
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Library not found with id: " + id));
  }

  @Delete
  @Secured("SUPER")
  void deleteLibrary(@Body LibraryEntity libraryEntity) {
    libraryService.deleteLibrary(libraryEntity);
  }

  @Get("{id}/files")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  List<SystemFileFolder> getLibraryFiles(
      @PathVariable Long id,
      @QueryValue(defaultValue = "/") String path,
      Authentication authentication)
      throws IOException {
    AuthorizationUtils.unauthorizedIfFalse(
        userLibraryAccessService.hasViewAccess(
            (Long) authentication.getAttributes().get("UID"), id));

    LibraryEntity library =
        libraryService
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Library not found with id: " + id));

    return libraryService.getLibraryFiles(library, path);
  }

  @Get("{id}/download")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  HttpResponse<StreamedFile> downloadFile(
      @PathVariable Long id,
      @QueryValue(defaultValue = "/") String path,
      Authentication authentication)
      throws IOException {
    AuthorizationUtils.unauthorizedIfFalse(
        userLibraryAccessService.hasViewAccess(
            (Long) authentication.getAttributes().get("UID"), id));

    LibraryEntity library =
        libraryService
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Library not found with id: " + id));
    UserEntity user = userService.findByUsername(authentication.getName());

    FileDownload fileDownload = libraryService.downloadLibraryFile(library, path, user);
    InputStream stream = fileDownload.getStream();
    String filename = fileDownload.getFilename();

    return HttpResponse.ok(
        new StreamedFile(stream, MediaType.APPLICATION_OCTET_STREAM_TYPE).attach(filename));
  }
}
