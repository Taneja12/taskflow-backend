package com.deepanshu.backend.user.service;

import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.user.dto.UserResponse;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.user.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final HelperService helperService;

    public UserServiceImpl(HelperService helperService) {
        this.helperService = helperService;
    }

    private UserResponse mapToResponse(User user)
    {
        return new UserResponse(
          user.getId(),
          user.getFullName(),
          user.getEmail(),
          user.getProfileImageUrl(),
          user.getStatus()
        );
    }

    @Override
    public UserResponse getAuthenticatedUser() {
        return mapToResponse(helperService.getCurrentUser());
    }
}
