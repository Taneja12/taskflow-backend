package com.deepanshu.backend.auth.service;

import com.deepanshu.backend.auth.dto.AuthResponse;
import com.deepanshu.backend.auth.dto.LoginRequest;
import com.deepanshu.backend.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
