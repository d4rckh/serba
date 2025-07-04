package com.serba.domain;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

@Data
@Serdeable
public class CreateUserRequest {
  private String username;
  private String password;
}
