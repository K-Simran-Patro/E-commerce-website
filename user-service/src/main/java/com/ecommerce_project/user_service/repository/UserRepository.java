package com.ecommerce_project.user_service.repository;

import com.ecommerce_project.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);

    User findByPhone(String phone);
}