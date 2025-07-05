package com.serba.controller;

import java.util.List;

import com.serba.entity.UserLibraryAccessEntity;
import com.serba.service.UserLibraryAccessService;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import lombok.RequiredArgsConstructor;

@Controller("user-library-access")
@RequiredArgsConstructor
public class UserLibraryAccessController {
  private final UserLibraryAccessService userLibraryAccessService;

  @Get("library/{libraryId}")
  @Secured("SUPER")
  public List<UserLibraryAccessEntity> findByLibraryId(@PathVariable Long libraryId) {
    return userLibraryAccessService.findByLibraryId(libraryId);
  }

  @Get("user/{userId}")
  @Secured("SUPER")
  public List<UserLibraryAccessEntity> findByUserId(@PathVariable Long userId) {
    return userLibraryAccessService.findByUserId(userId);
  }

  @Put
  @Secured("SUPER")
  public UserLibraryAccessEntity updateUserLibraryAccess(@Body UserLibraryAccessEntity userLibraryAccess) {
    return userLibraryAccessService.updateUserLibraryAccess(userLibraryAccess);
  }
}
