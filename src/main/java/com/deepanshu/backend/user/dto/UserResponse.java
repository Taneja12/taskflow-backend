package com.deepanshu.backend.user.dto;

import com.deepanshu.backend.user.entity.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private UUID id;
    private String fullName;
    private String email;
    private String profileImageUrl;
    private AccountStatus status;
}
