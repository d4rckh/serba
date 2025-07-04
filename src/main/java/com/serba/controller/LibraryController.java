package com.serba.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import com.serba.domain.files.SystemFileFolder;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;
import com.serba.service.LibraryService;
import com.serba.service.UserService;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;

@Controller("libraries")
@RequiredArgsConstructor
public class LibraryController {
  private final LibraryService libraryService;
  private final UserService userService;

  @Post
  @Secured(SecurityRule.IS_AUTHENTICATED)
  LibraryEntity createLibrary(@Body LibraryEntity libraryEntity) {
    return libraryService.createLibrary(libraryEntity);
  }

  @Put
  @Secured(SecurityRule.IS_AUTHENTICATED)
  LibraryEntity updateLibrary(@Body LibraryEntity libraryEntity) {
    return libraryService.updateLibrary(libraryEntity);
  }

  @Get
  @Secured(SecurityRule.IS_AUTHENTICATED)
  List<LibraryEntity> findAll() {
    return libraryService.findAll();
  }

  @Get("{id}")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  LibraryEntity findById(@PathVariable Long id) {
    return libraryService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Library not found with id: " + id));
  }

  @Get("{id}/files")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  List<SystemFileFolder> getLibraryFiles(@PathVariable Long id, @QueryValue(defaultValue = "/") String path)
      throws IOException {
    LibraryEntity library = libraryService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Library not found with id: " + id));
    return libraryService.getLibraryFiles(library, path);
  }

  @Get("{id}/download")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  HttpResponse<StreamedFile> downloadFile(
      @PathVariable Long id,
      @QueryValue(defaultValue = "/") String path,
      Authentication authentication) throws IOException {
    LibraryEntity library = libraryService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Library not found with id: " + id));
    UserEntity user = userService.findByUsername(authentication.getName());

    InputStream stream = libraryService.downloadLibraryFile(library, path, user);

    String filename = Paths.get(path).getFileName().toString();

    return HttpResponse.ok(
        new StreamedFile(stream, MediaType.APPLICATION_OCTET_STREAM_TYPE)
            .attach(filename));
  }
}