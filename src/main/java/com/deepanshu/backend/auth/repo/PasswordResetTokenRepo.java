package com.deepanshu.backend.auth.repo;

import com.deepanshu.backend.auth.entity.PasswordResetToken;
import com.deepanshu.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, UUID> {
    void deleteAllByUser(User user);

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
