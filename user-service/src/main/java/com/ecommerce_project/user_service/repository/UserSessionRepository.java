package com.ecommerce_project.user_service.repository;

import com.ecommerce_project.user_service.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    List<UserSession> findByUserUserId(UUID userId);

    UserSession findByToken(String token);
}