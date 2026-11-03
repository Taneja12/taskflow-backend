package com.deepanshu.backend.auth.service;

import com.deepanshu.backend.auth.dto.AuthResponse;
import com.deepanshu.backend.auth.dto.LoginRequest;
import com.deepanshu.backend.auth.dto.LoginResult;
import com.deepanshu.backend.auth.dto.RegisterRequest;
import com.deepanshu.backend.auth.entity.PasswordResetToken;
import com.deepanshu.backend.auth.entity.RefreshToken;
import com.deepanshu.backend.auth.repo.PasswordResetTokenRepo;
import com.deepanshu.backend.common.exception.AccountBlockedException;
import com.deepanshu.backend.common.exception.EmailAlreadyExistsException;
import com.deepanshu.backend.common.exception.InvalidCredentialsException;
import com.deepanshu.backend.common.security.JwtService;
import com.deepanshu.backend.notification.service.EmailService;
import com.deepanshu.backend.user.entity.AccountStatus;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.user.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    public AuthServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService, PasswordResetTokenRepo passwordResetTokenRepo, EmailService emailService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenRepo = passwordResetTokenRepo;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        Optional<User> existingUser  = userRepo.findByEmail(request.getEmail().toLowerCase());

        if(existingUser.isPresent())
        {
            throw new EmailAlreadyExistsException("Email already exists.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(AccountStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userRepo.save(user);
        return new AuthResponse("Registration Successful", null);

    }

    @Override
    public LoginResult login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail().toLowerCase()).orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));

        if(user.getStatus()==AccountStatus.BLOCKED)
        {
            throw new AccountBlockedException("Your account is blocked");
        }

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!matches)
        {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        AuthResponse response = new AuthResponse("Login Successful", accessToken);

        return new LoginResult(response,refreshToken.getToken());
    }

    @Override
    public LoginResult refresh(String refreshToken) {
        RefreshToken verifiedToken = refreshTokenService.verifyRefreshToken(refreshToken);
        refreshTokenService.revokeToken(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(verifiedToken.getUser());
        String accessToken = jwtService.generateToken(verifiedToken.getUser());
        AuthResponse response = new AuthResponse("Access token",accessToken);
        return new LoginResult(response, newRefreshToken.getToken());
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    @Override
    @Transactional
    public void forget_password(String email) {

//        find user using email
        User user = userRepo.findByEmail(email.toLowerCase()).orElse(null);
        log.info("User found: {}", user);
        if(user==null)
        {
            log.info("User not found ");
            return;
        }

        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);

        String token = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(randomBytes);

        String tokenHash = hashToken(token);

        passwordResetTokenRepo.deleteAllByUser(user);

        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setUser(user);
        resetToken.setTokenHash(tokenHash);
        resetToken.setUsed(false);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        passwordResetTokenRepo.save(resetToken);

        String resetLink = frontendUrl + "/reset-password?token=" + token;
        log.info("reset link: {}", resetLink);

        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    @Override
    @Transactional
    public void reset_password(String newPassword, String token) {

        // 1. Hash the received token
        String tokenHash = hashToken(token);

        // 2. Find token in database
        PasswordResetToken resetToken =
                passwordResetTokenRepo.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid password reset token"));

        // 3. Check whether token is already used
        if (resetToken.isUsed()) {
            throw new RuntimeException("Password reset token already used");
        }

        // 4. Check token expiry
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Password reset token has expired");
        }

        // 5. Get associated user
        User user = resetToken.getUser();

        // 6. Encode new password
        user.setPassword(passwordEncoder.encode(newPassword));

        // 7. Update password
        userRepo.save(user);

        // 8. Mark token as used
        resetToken.setUsed(true);

        passwordResetTokenRepo.save(resetToken);
    }


    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }

}
