package com.deepanshu.backend.auth.dto;

public record LoginResult (AuthResponse authResponse, String refreshToken){
}
