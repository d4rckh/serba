package com.serba.controller;

import io.micronaut.http.annotation.*;
import io.micronaut.http.*;
import jakarta.inject.Inject;
import io.micronaut.core.io.ResourceLoader;

import java.io.InputStream;
import java.util.Optional;

@Controller
public class SpaFallbackController {

    @Inject
    ResourceLoader resourceLoader;

    private static final String INDEX_PATH = "public/index.html";

    private HttpResponse<?> serveIndex() {
        Optional<InputStream> resource = resourceLoader.getResourceAsStream("classpath:" + INDEX_PATH);
        return resource.map(inputStream -> HttpResponse.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(inputStream))
                .orElse(HttpResponse.notFound());
    }

    @Get("login")
    HttpResponse<?> loginFallback() {
        return serveIndex();
    }
    
    @Get("admin")
    HttpResponse<?> adminFallback() {
        return serveIndex();
    }

    @Get()
    HttpResponse<?> indexFallback() {
        return serveIndex();
    }
}
