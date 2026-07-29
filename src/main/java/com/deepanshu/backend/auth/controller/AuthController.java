package com.deepanshu.backend.auth.controller;


import com.deepanshu.backend.auth.dto.AuthResponse;
import com.deepanshu.backend.auth.dto.LoginRequest;
import com.deepanshu.backend.auth.dto.RegisterRequest;
import com.deepanshu.backend.auth.service.AuthService;
import com.deepanshu.backend.common.exception.EmailAlreadyExistsException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Authentication")
public class AuthController {

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
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @GetMapping("/greet")
    public String greet()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Hello Brother " + authentication.getName();
    }
}
