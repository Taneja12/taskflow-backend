package com.deepanshu.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String message;

    @Value("${jwt.secret}")
    private String token;
}
