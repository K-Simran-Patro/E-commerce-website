package com.ecommerce_project.user_service.repository;

import com.ecommerce_project.user_service.entity.UserCart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserCartRepository extends JpaRepository<UserCart, UUID> {

    List<UserCart> findByUserUserId(UUID userId);

    UserCart findByUserUserIdAndVariantId(UUID userId, UUID variantId);
}