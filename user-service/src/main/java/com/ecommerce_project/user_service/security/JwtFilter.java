package com.ecommerce_project.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


//it checks every incoming request for a valid JWT token and tells Spring Security who the user is.

@Component //This annotation indicates that this class is a Spring component, which allows Spring to automatically detect and manage it as a bean in the application context. By annotating the JwtFilter class with @Component, we enable Spring to recognize it as a filter that should be applied to incoming HTTP requests for JWT validation and authentication purposes.
public class JwtFilter extends OncePerRequestFilter //Custom filter that intercepts incoming HTTP requests to validate JWT tokens and set the authentication context for the request. It extends OncePerRequestFilter, which ensures that the filter is executed once per request. The filter checks for the presence of a JWT token in the Authorization header, validates it, and if valid, extracts the user's email and role to set up the Spring Security context for the request.
{

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class); //Creates a logger instance for this class, which can be used to log messages for debugging and monitoring purposes. This allows us to log information about the JWT validation process, such as whether a token is valid or if there are any issues with it.

    @Autowired
    private JwtUtil jwtUtil; //Used for JWT operations like validation and extracting claims.

    @Autowired
    private UserDetailsServiceImpl userDetailsService; //Used to load user details from database.

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException //This method is overridden from the OncePerRequestFilter class and is called for each incoming HTTP request. It checks for the presence of a JWT token in the Authorization header, validates it, and if valid, extracts the user's email and role to set up the Spring Security context for the request. If the token is invalid or missing, it simply continues the filter chain without setting an authentication context, allowing Spring Security to handle unauthorized access as needed.
                                    {

        String authHeader = request.getHeader("Authorization"); //Retrieves the value of the "Authorization" header from the incoming HTTP request. This header is expected to contain the JWT token in the format "Bearer <token>". The filter will check this header to see if a token is present and valid for authentication purposes.

        if (authHeader != null && authHeader.startsWith("Bearer ")) //Checks if the Authorization header is present and starts with "Bearer ", which indicates that it contains a JWT token. If this condition is true, it means that the request includes a token that can be validated and used for authentication.
            {

            String token = authHeader.substring(7); //Extracts the actual JWT token from the Authorization header by removing the "Bearer " prefix. This is done using the substring method, which takes the part of the string starting from index 7 (the length of "Bearer ") to the end of the string. The resulting token string will be used for validation and extracting user information.
            
            // Validates the token using the jwtUtil. If the token is valid, it extracts the email and role from the token, loads the user details from the database, and sets up the Spring Security context with the user's authentication information. This allows Spring Security to recognize the user as authenticated for the duration of the request and apply any necessary authorization checks based on their role. If the token is invalid, it logs a warning message and continues the filter chain without setting an authentication
            if (jwtUtil.isTokenValid(token)) {

                String email = jwtUtil.extractEmail(token);
                logger.info("JWT valid for user: {}", email);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);//Sets the authentication information in the Spring Security context, which allows the application to recognize the user as authenticated for the duration of the request. This is done by creating a UsernamePasswordAuthenticationToken with the user's details and authorities, and then setting it in the SecurityContextHolder. Once this is set, Spring Security can perform authorization checks based on the user's roles and permissions when they access protected resources in the application.

            } else {
                logger.warn("Invalid JWT token received");
            }
        }

        filterChain.doFilter(request, response);//Continues the filter chain, allowing the request to proceed to the next filter or ultimately to the controller that will handle it. If the JWT token was valid and the authentication context was set, Spring Security will recognize the user as authenticated when processing the request. If the token was invalid or missing, the request will continue without an authentication context, and Spring Security will handle it as an unauthenticated request, potentially returning a 401 Unauthorized response if access to protected resources is attempted.
    }
}