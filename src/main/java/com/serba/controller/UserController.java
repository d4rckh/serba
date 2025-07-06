package com.serba.controller;

import com.serba.domain.CreateUserRequest;
import com.serba.entity.UserEntity;
import com.serba.service.UserService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Controller("users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @Secured("SUPER")
  @Post
  public UserEntity createUser(@Body CreateUserRequest request) {
    return userService.createUser(request);
  }

  @Secured("SUPER")
  @Put("password")
  public UserEntity updatePassword(@Body CreateUserRequest request) {
    return userService.updatePassword(request);
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Get("me")
  public UserEntity getCurrentUser(Authentication authentication) {
    String username = authentication.getName();
    return userService.findByUsername(username);
  }

  @Secured("SUPER")
  @Get
  public List<UserEntity> findAll() {
    return userService.findAll();
  }

  @Secured("SUPER")
  @Delete
  public void deleteUser(@Body UserEntity user) {
    userService.deleteUser(user);
  }
}
