package com.deepanshu.backend.auth.service;

import com.deepanshu.backend.auth.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    LoginResult login(LoginRequest request);

    LoginResult refresh(String refreshToken);

    void logout(String refreshToken);

    void forget_password(String email);

    void reset_password(String newPassword, String token);

    void validate_reset_token(String token);
}
