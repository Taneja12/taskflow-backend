package com.deepanshu.backend.auth.service;

import com.deepanshu.backend.auth.dto.AuthResponse;
import com.deepanshu.backend.auth.dto.LoginRequest;
import com.deepanshu.backend.auth.dto.LoginResult;
import com.deepanshu.backend.auth.dto.RegisterRequest;
import com.deepanshu.backend.auth.entity.RefreshToken;
import com.deepanshu.backend.common.exception.AccountBlockedException;
import com.deepanshu.backend.common.exception.EmailAlreadyExistsException;
import com.deepanshu.backend.common.exception.InvalidCredentialsException;
import com.deepanshu.backend.common.security.JwtService;
import com.deepanshu.backend.user.entity.AccountStatus;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.user.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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
    public AuthResponse refresh(String refreshToken) {
        RefreshToken verifiedToken = refreshTokenService.verifyRefreshToken(refreshToken);
        String accessToken = jwtService.generateToken(verifiedToken.getUser());
        return new AuthResponse("Access token",accessToken);
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }


}
