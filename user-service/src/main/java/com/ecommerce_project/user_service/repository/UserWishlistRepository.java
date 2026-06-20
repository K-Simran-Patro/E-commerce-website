package com.ecommerce_project.user_service.repository;

import com.ecommerce_project.user_service.entity.UserWishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserWishlistRepository extends JpaRepository<UserWishlist, UUID> {

    List<UserWishlist> findByUserUserId(UUID userId);

    UserWishlist findByUserUserIdAndVariantId(UUID userId, Long variantId);
}