package com.ecommerce_project.user_service.service;

import com.ecommerce_project.user_service.dto.UserAddressRequest;
import com.ecommerce_project.user_service.dto.UserAddressResponse;
import com.ecommerce_project.user_service.entity.User;
import com.ecommerce_project.user_service.entity.UserAddress;
import com.ecommerce_project.user_service.repository.UserAddressRepository;
import com.ecommerce_project.user_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserAddressService {

    private static final Logger logger = LoggerFactory.getLogger(UserAddressService.class);

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;

    // Add new address
    public UserAddressResponse addAddress(UUID userId, UserAddressRequest request) {

        logger.info("Adding address for user: {}", userId);

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setCountry(request.getCountry());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        address.setIsActive(true);
        address.setCreatedAt(OffsetDateTime.now());
        address.setUpdatedAt(OffsetDateTime.now());
        address.setCreatedBy(user.getEmail());
        address.setModifiedBy(user.getEmail());

        userAddressRepository.save(address);

        logger.info("Address added successfully for user: {}", userId);

        return mapToResponse(address);
    }

    // Get all addresses for a user
    public List<UserAddressResponse> getAddresses(UUID userId) {

        logger.info("Fetching addresses for user: {}", userId);

        List<UserAddress> addresses = userAddressRepository.findByUserUserId(userId);
        List<UserAddressResponse> responses = new ArrayList<>();

        for (UserAddress address : addresses) {
            if (address.getIsActive()) {
                responses.add(mapToResponse(address));
            }
        }

        return responses;
    }

    // Update address
    public UserAddressResponse updateAddress(UUID userId, UUID addressId, UserAddressRequest request) {

        logger.info("Updating address: {} for user: {}", addressId, userId);

        UserAddress address = userAddressRepository.findByAddressIdAndUserUserId(addressId, userId);

        if (address == null) {
            throw new RuntimeException("Address not found");
        }

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setCountry(request.getCountry());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        address.setUpdatedAt(OffsetDateTime.now());
        address.setModifiedBy(userId.toString());

        userAddressRepository.save(address);

        logger.info("Address updated successfully: {}", addressId);

        return mapToResponse(address);
    }

    // Delete address — soft delete
    public String deleteAddress(UUID userId, UUID addressId) {

        logger.info("Deleting address: {} for user: {}", addressId, userId);

        UserAddress address = userAddressRepository.findByAddressIdAndUserUserId(addressId, userId);

        if (address == null) {
            throw new RuntimeException("Address not found");
        }

        address.setIsActive(false);
        address.setUpdatedAt(OffsetDateTime.now());
        address.setModifiedBy(userId.toString());

        userAddressRepository.save(address);

        logger.info("Address deleted successfully: {}", addressId);

        return "Address deleted successfully";
    }

    // Map entity to response
    private UserAddressResponse mapToResponse(UserAddress address) {
        UserAddressResponse response = new UserAddressResponse();
        response.setAddressId(address.getAddressId());
        response.setFullName(address.getFullName());
        response.setPhone(address.getPhone());
        response.setAddressLine(address.getAddressLine());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPincode(address.getPincode());
        response.setCountry(address.getCountry());
        response.setIsDefault(address.getIsDefault());
        response.setIsActive(address.getIsActive());
        return response;
    }
}