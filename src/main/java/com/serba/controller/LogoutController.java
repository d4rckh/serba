package com.serba.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.session.Session;

@Controller("signout")
public class LogoutController {

  @Post
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public HttpResponse<?> logout(Session session) {
    if (session != null) {
      session.clear();
    }

    return HttpResponse.ok();
  }
}
