package com.ecommerce_project.user_service.config;

import com.ecommerce_project.user_service.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;



// This class configures Spring Security for the user service. It defines which routes are public (like /auth/**) and which require authentication. It also sets up JWT authentication by adding the JwtFilter to the security filter chain. Additionally, it configures CORS to allow requests from the frontend domain and defines a password encoder bean for hashing passwords.
@Configuration //This annotation indicates that this class is a configuration class for Spring. It allows us to define beans and configure settings for the application context. In this case, we use it to set up security configurations for the user service.
@EnableWebSecurity //This annotation enables Spring Security for the application. It allows us to configure security settings such as authentication and authorization rules. By using @EnableWebSecurity, we can customize how Spring Security handles incoming requests and protects our application's endpoints.
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter; //This field is annotated with @Autowired, which allows Spring to automatically inject an instance of the JwtFilter class into this SecurityConfig class. The JwtFilter is a custom filter that we have defined to handle JWT authentication. By autowiring it here, we can add it to the security filter chain later in the configuration to ensure that it is applied to incoming requests for authentication purposes.

    @Bean //This annotation indicates that the method below returns a bean that should be managed by the Spring container. In this case, the method securityFilterChain returns a SecurityFilterChain bean, which is used by Spring Security to define the security configuration for HTTP requests. By annotating it with @Bean, we allow Spring to recognize it as a component of the application context and use it to configure security settings.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // disable csrf — not needed for JWT
        http.csrf(csrf -> csrf.disable()); //This line disables CSRF (Cross-Site Request Forgery) protection in Spring Security. Since we are using JWT for authentication, which is stateless and does not rely on cookies, we do not need CSRF protection. Disabling it simplifies the security configuration and avoids unnecessary checks for CSRF tokens in incoming requests.

        // allow frontend to call this backend from other domains
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // no sessions — JWT handles everything
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));//This line configures Spring Security to not create or use HTTP sessions for authentication. Since we are using JWT, which is stateless and does not rely on server-side sessions, we set the session creation policy to STATELESS. This means that Spring Security will not store any authentication information in a session, and each request must contain a valid JWT token for authentication.

        // public routes and protected routes
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated());

        // run JWT filter before every request
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ─── CORS Configuration ──────────────────────────────────────────────────
    // Tells the browser which frontends are allowed to call this backend.
    // Without this, Vercel requests will be blocked by the browser.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();


        config.setAllowedOrigins(Arrays.asList(
                                        "https://e-commerce-website-eta-nine-52.vercel.app"

        ));

        // Allowed HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers — needed so Authorization header (JWT) is not blocked
        config.setAllowedHeaders(Arrays.asList("*"));

        // Apply to all routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
    // ─────────────────────────────────────────────────────────────────────────

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
