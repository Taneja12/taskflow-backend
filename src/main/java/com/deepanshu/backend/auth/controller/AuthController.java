package com.deepanshu.backend.auth.controller;


import com.deepanshu.backend.auth.dto.*;
import com.deepanshu.backend.auth.service.AuthService;
import com.deepanshu.backend.common.exception.InvalidRefreshTokenException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Authentication")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @Value("${auth.refresh-cookie.name}")
    private String refreshCookieName;

    @Value("${auth.refresh-cookie.path}")
    private String refreshCookiePath;

    @Value("${auth.refresh-cookie.max-age-seconds}")
    private long refreshCookieMaxAge;

    @Value("${auth.refresh-cookie.http-only}")
    private boolean refreshCookieHttpOnly;

    @Value("${auth.refresh-cookie.secure}")
    private boolean refreshCookieSecure;

    @Value("${auth.refresh-cookie.same-site}")
    private String refreshCookieSameSite;

    private final AuthService authService;

    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request)
    {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED) ;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request
    ){
        LoginResult result = authService.login(request);
        ResponseCookie refreshCookie = ResponseCookie
                .from(refreshCookieName, result.refreshToken())
                .httpOnly(refreshCookieHttpOnly)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(Duration.ofSeconds(refreshCookieMaxAge))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(result.authResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException("Refresh token is missing");
        }
        LoginResult result = authService.refresh(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie
                .from(refreshCookieName, result.refreshToken())
                .httpOnly(refreshCookieHttpOnly)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(Duration.ofSeconds(refreshCookieMaxAge))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(result.authResponse());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refreshToken", required = false)
            String refreshToken
    ) {

        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logout(refreshToken);
        }

        ResponseCookie clearCookie = ResponseCookie
                .from(refreshCookieName, "")
                .httpOnly(refreshCookieHttpOnly)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(Duration.ZERO)
                .build();

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
    }

    @PostMapping("/forget_password")
    public ResponseEntity<Void> forget_password(@RequestBody ForgetPasswordRequest request)
    {
        authService.forget_password(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset_password")
    public ResponseEntity<Void> reset_password(@RequestBody ResetPasswordRequest request, @RequestParam String token)
    {
        authService.reset_password(request.getPassword(), token);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/greet")
//    public String greet()
//    {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return "Hello Brother " + authentication.getName();
//    }


}
