package com.deepanshu.backend.user.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User
{
    @Id
    @GeneratedValue
    private UUID id;
    private String fullName;
    private String email;
    private String password;
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

 }
