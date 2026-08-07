package com.deepanshu.backend.user.controller;

import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.user.dto.UserResponse;
import com.deepanshu.backend.user.repo.UserRepo;
import com.deepanshu.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name ="Users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get Authenticated User")
    public ResponseEntity<UserResponse> getUser(){
        return ResponseEntity.ok(userService.getAuthenticatedUser());
    }
}
