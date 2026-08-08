package com.deepanshu.backend.auth.service;

import com.deepanshu.backend.auth.dto.AuthResponse;
import com.deepanshu.backend.auth.dto.LoginRequest;
import com.deepanshu.backend.auth.dto.LoginResult;
import com.deepanshu.backend.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    LoginResult login(LoginRequest request);

    LoginResult refresh(String refreshToken);

    void logout(String refreshToken);
}
