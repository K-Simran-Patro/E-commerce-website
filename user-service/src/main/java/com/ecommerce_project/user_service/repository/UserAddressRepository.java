package com.ecommerce_project.user_service.repository;

import com.ecommerce_project.user_service.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    List<UserAddress> findByUserUserId(UUID userId);

    UserAddress findByAddressIdAndUserUserId(UUID addressId, UUID userId);
}