package com.deepanshu.backend.auth.service;

import com.deepanshu.backend.auth.dto.AuthResponse;
import com.deepanshu.backend.auth.dto.LoginRequest;
import com.deepanshu.backend.auth.dto.RegisterRequest;
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

    public AuthServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        Optional<User> existingUser  = userRepo.findByEmail(request.getEmail());

        if(existingUser.isPresent())
        {
            throw new EmailAlreadyExistsException("Email already exists.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(AccountStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userRepo.save(user);
        return new AuthResponse("Registration Successful", null);

    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail()).orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!matches)
        {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        String token = jwtService.generateToken(user);
        return new AuthResponse("Login Successful", token);

    }


}
