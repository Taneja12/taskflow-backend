package com.deepanshu.backend.auth.service;

import com.deepanshu.backend.auth.entity.RefreshToken;
import com.deepanshu.backend.user.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeToken(String token);
}
