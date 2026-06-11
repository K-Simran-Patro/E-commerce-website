package com.ecommerce_project.user_service.security;

import com.ecommerce_project.user_service.entity.User;
import com.ecommerce_project.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;


//important classes in Spring Security because it tells Spring how to find a user from the database when someone tries to log in. By implementing UserDetailsService, we can define the logic for retrieving user information (like username, password, and roles) based on the email address provided during authentication. This allows Spring Security to authenticate users against our database and manage their roles and permissions effectively.

@Service
public class UserDetailsServiceImpl implements UserDetailsService //This class implements the UserDetailsService interface from Spring Security, which is used to load user-specific data during the authentication process. By implementing this interface, we can define how to retrieve user information (like username, password, and roles) from our database or any other source when a user attempts to authenticate.
{

    @Autowired
    private UserRepository userRepository; //This field is annotated with @Autowired, which allows Spring to automatically inject an instance of UserRepository into this class. The UserRepository is used to interact with the database and retrieve user information based on the email address provided during authentication.

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException //This method is overridden from the UserDetailsService interface and is called by Spring Security during the authentication process. It takes an email as input (which serves as the username in this case) and attempts to load the corresponding user details from the database. If a user with the given email is found, it returns a UserDetails object containing the user's email, password hash, and authorities (roles). If no user is found, it throws a UsernameNotFoundException, which indicates that authentication has failed due to an invalid username.
    {

        User user = userRepository.findByEmail(email); //This line uses the userRepository to find a User entity in the database based on the provided email address. The findByEmail method is a custom query method defined in the UserRepository interface that retrieves a user by their email. If a user with the specified email exists, it will be returned; otherwise, this will return null.

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }
        
        // The UserDetails object is created using the user's email as the username, the password hash for authentication, and a list of authorities (roles) that the user has. In this case, we create a single authority based on the user's role (e.g., "ROLE_CUSTOMER" or "ROLE_ADMIN") by prefixing the role with "ROLE_" and converting it to uppercase. This allows Spring Security to handle authorization based on the user's role when processing authenticated requests.
        return new org.springframework.security.core.userdetails.User//This line creates and returns a new UserDetails object using the org.springframework.security.core.userdetails.User class, which is a built-in implementation of the UserDetails interface provided by Spring Security. This object contains the user's email (as the username), password hash, and authorities (roles) that will be used by Spring Security for authentication and authorization purposes.
        (
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())) //Creates a new SimpleGrantedAuthority object with the user's role, prefixed with "ROLE_" and converted to uppercase. This is a common convention in Spring Security to represent roles as authorities. For example, if the user's role is "customer", this will create an authority of "ROLE_CUSTOMER". This authority can then be used by Spring Security to perform authorization checks based on the user's role when they access protected resources in the application.
        );
    }
}