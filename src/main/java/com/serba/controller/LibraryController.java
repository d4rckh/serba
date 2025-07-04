package com.serba.controller;

import com.serba.entity.LibraryEntity;
import com.serba.service.LibraryService;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;

@Controller("libraries")
@RequiredArgsConstructor
public class LibraryController {
  private final LibraryService libraryService;

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
  LibraryEntity findAll(Long id) {
    return libraryService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Library not found with id: " + id));
  }

  @Get("{id}")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  LibraryEntity findById(@PathVariable Long id) {
    return libraryService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Library not found with id: " + id));
  }
}