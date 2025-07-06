package com.serba.security;

import com.serba.entity.UserEntity;
import com.serba.service.UserService;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestReactiveAuthenticationProvider;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Singleton
@Slf4j
public class AuthenticationProviderUserPassword<B>
    implements HttpRequestReactiveAuthenticationProvider<B> {
  private final UserService userService;

  public AuthenticationProviderUserPassword(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Publisher<AuthenticationResponse> authenticate(
      @Nullable HttpRequest<B> httpRequest,
      @NonNull AuthenticationRequest<String, String> authenticationRequest) {
    return Flux.create(
        emitter -> {
          Optional<UserEntity> userOptional = userService.findByUsernameAndPassword(
              authenticationRequest.getIdentity(), authenticationRequest.getSecret());

          if (userOptional.isEmpty()) {
            log.warn("Authentication failure for user: {}", authenticationRequest.getIdentity());
            emitter.error(AuthenticationResponse.exception());
            return;
          }

          UserEntity user = userOptional.get();

          if (user != null) {
            log.info("Authentication successful for user: {}", authenticationRequest.getIdentity());

            List<String> roles = user.getSuperUser().equals(true) ? List.of("SUPER") : List.of();

            emitter.next(
                AuthenticationResponse.success(
                    (String) authenticationRequest.getIdentity(),
                    roles,
                    Map.of("UID", user.getId())));
            emitter.complete();
          } else {
            emitter.error(AuthenticationResponse.exception());
          }
        },
        FluxSink.OverflowStrategy.ERROR);
  }
}
