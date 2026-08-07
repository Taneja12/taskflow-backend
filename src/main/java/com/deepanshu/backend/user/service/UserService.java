package com.deepanshu.backend.user.service;

import com.deepanshu.backend.user.dto.UserResponse;

public interface UserService {
    UserResponse getAuthenticatedUser();
}
