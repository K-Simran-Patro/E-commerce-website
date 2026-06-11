package com.ecommerce_project.user_service.repository;

import com.ecommerce_project.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>  //   This interface extends JpaRepository, which provides standard CRUD operations for the User entity. The UUID type indicates that the primary key of the User entity is of type UUID. By extending JpaRepository, we can perform database operations on User entities without needing to write boilerplate code for common queries. Additionally, we have defined custom query methods (findByEmail and findByPhone) to retrieve users based on their email or phone number, which are commonly used for authentication and registration processes.
{

    User findByEmail(String email); //Method to find a user by their email address. This is used during registration to check if an email is already registered and during login to retrieve the user details for authentication.

    User findByPhone(String phone); //Method to find a user by their phone number. This can be used as an alternative to email for registration and login, allowing users to authenticate using their phone number instead of an email address.
}