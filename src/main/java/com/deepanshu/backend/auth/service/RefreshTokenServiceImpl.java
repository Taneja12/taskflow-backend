package com.deepanshu.backend.auth.service;

import com.deepanshu.backend.auth.entity.RefreshToken;
import com.deepanshu.backend.auth.repo.RefreshTokenRepository;
import com.deepanshu.backend.common.exception.InvalidRefreshTokenException;
import com.deepanshu.backend.user.entity.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final SecureRandom secureRandom = new SecureRandom();

    private final RefreshTokenRepository refreshRepo;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshRepo) {
        this.refreshRepo = refreshRepo;
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    @Override
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(generateToken());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(30));
        refreshToken.setRevoked(false);
        return refreshRepo.save(refreshToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshRepo.findByTokenAndRevokedFalse(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
        if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }
        return refreshToken;
    }

    @Override
    public void revokeToken(String token) {
        refreshRepo.findByToken(token)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshRepo.save(refreshToken);
                });
    }
}
