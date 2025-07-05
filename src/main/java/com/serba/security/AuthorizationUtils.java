package com.serba.security;

public class AuthorizationUtils {
  public static void unauthorizedIfFalse(boolean condition) {
    if (!condition) {
      throw new RuntimeException("You are not authorized to perform this action");
    }
  }
}
