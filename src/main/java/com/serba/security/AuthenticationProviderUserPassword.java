package com.serba.security;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestReactiveAuthenticationProvider;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import org.reactivestreams.Publisher;

import com.serba.entity.UserEntity;
import com.serba.service.UserService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Singleton
@Slf4j
public class AuthenticationProviderUserPassword<B> implements HttpRequestReactiveAuthenticationProvider<B> {
    private final UserService userService;

    public AuthenticationProviderUserPassword(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable HttpRequest<B> httpRequest,
            @NonNull AuthenticationRequest<String, String> authenticationRequest) {
        log.info("Got authentication request: {}", authenticationRequest.getIdentity());
        return Flux.create(emitter -> {
            UserEntity user = userService.findByUsernameAndPassword(
                    authenticationRequest.getIdentity(), authenticationRequest.getSecret());
            if (user != null) {
                log.info("Authentication successful for user: {}", authenticationRequest.getIdentity());
                emitter.next(AuthenticationResponse.success((String) authenticationRequest.getIdentity()));
                emitter.complete();
            } else {
                emitter.error(AuthenticationResponse.exception());
            }
        }, FluxSink.OverflowStrategy.ERROR);
    }
}
