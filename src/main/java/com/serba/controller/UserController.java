package com.serba.controller;

import java.util.List;

import com.serba.domain.CreateUserRequest;
import com.serba.entity.UserEntity;
import com.serba.service.UserService;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;

@Controller("users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @Secured(SecurityRule.IS_ANONYMOUS)
  @Post
  public UserEntity createUser(@Body CreateUserRequest request) {
    return userService.createUser(request);
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Get("me")
  public UserEntity getCurrentUser(Authentication authentication) {
    String username = authentication.getName();
    return userService.findByUsername(username);
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Get
  public List<UserEntity> findAll() {
    return userService.findAll();
  }
}
