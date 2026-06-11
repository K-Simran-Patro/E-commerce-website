package com.ecommerce_project.user_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component //Create an object of this class and manage it as a Spring bean, allowing it to be injected into other components (like controllers and services) where JWT-related functionality is needed.
public class JwtUtil {

    @Value("${jwt.secret}") //Injects the value of the "jwt.secret" property from the application's configuration (e.g., application.properties or application.yml) into this field. This secret key is used to sign and verify JWT tokens, ensuring their integrity and authenticity.
    private String secret;

    @Value("${jwt.expiration}") //Injects the value of the "jwt.expiration" property from the application's configuration into this field. This value represents the expiration time for generated JWT tokens (in milliseconds). When a token is generated, it will include an expiration timestamp based on the current time plus this duration, after which the token will no longer be valid.
    private long expiration;

    private SecretKey getSigningKey() //Utility method to convert the secret string into a SecretKey object that can be used for signing and verifying JWT tokens. It uses the Keys.hmacShaKeyFor method from the jjwt library to create a key suitable for HMAC-SHA algorithms based on the provided secret string.
    {
        return Keys.hmacShaKeyFor(secret.getBytes());//Converts the secret string into a byte array and creates a SecretKey object that can be used for signing and verifying JWT tokens. This method is called internally when generating and validating tokens to ensure that the same key is used consistently throughout the application.
    }

    public String generateToken(String email, String role) //Generates a JWT token for a given email and role. The token includes the email as the subject, the role as a custom claim, the issued at timestamp, and the expiration timestamp. The token is signed using the secret key to ensure its integrity and authenticity. This method is typically called during the login process to create a token that can be returned to the client and used for subsequent authenticated requests.
    {
        return Jwts.builder() //Creates a new JWT token builder using the Jwts.builder() method from the jjwt library. This builder allows us to set various claims and properties of the token before signing and compacting it into a string.
                .subject(email)//   Sets the "subject" (sub) claim of the JWT token to the provided email. This identifies the principal (user) that the token represents and can be used later to retrieve user information during authentication and authorization processes.
                .claim("role", role)//Adds a custom claim named "role" to the JWT token, which contains the user's role (e.g., "customer", "admin"). This allows the application to include additional information about the user in the token, which can be used for authorization decisions when processing authenticated requests.
                .issuedAt(new Date()) //Sets the "issued at" (iat) claim to the current date and time, indicating when the token was generated. This can be used later to determine the age of the token and to enforce expiration policies.
                .expiration(new Date(System.currentTimeMillis() + expiration)) //Sets the expiration time for the token by adding the configured expiration duration (in milliseconds) to the current system time. This ensures that the token will only be valid for a certain period after it is issued, enhancing security by limiting the window of time during which a stolen token could be used.
                .signWith(getSigningKey()) //Signs the JWT token using the secret key obtained from the getSigningKey() method. This ensures that the token cannot be tampered with, as any changes to the token would invalidate the signature. The resulting token can be verified later using the same secret key to confirm its authenticity.
                .compact(); //Builds and returns the JWT token as a compact string. The token includes the email as the subject, the role as a custom claim, the issued at timestamp, and the expiration timestamp. It is signed using the secret key to ensure that it cannot be tampered with. The resulting token can be sent to clients after successful authentication and used for subsequent requests that require authentication.
    }

    public String extractEmail(String token) //Extracts the email from a given JWT token. This method is used to retrieve the user's email address from the token during authentication processes.
    {
        return Jwts.parser() //Creates a new JWT parser using the Jwts.parser() method from the jjwt library. This parser allows us to parse and validate JWT tokens, as well as extract claims and other information from them.
                .verifyWith(getSigningKey()) //Configures the parser to use the secret key obtained from the getSigningKey() method for verifying the signature of the JWT token. This ensures that the token is valid and has not been tampered with before extracting any information from it.
                .build() //Builds the JWT parser with the specified verification key and returns a JwtParser object that can be used to parse and validate JWT tokens.
                .parseSignedClaims(token) //Parses the provided JWT token string and verifies its signature using the configured signing key. If the token is valid, it returns a Jws<Claims> object that contains the claims and other information extracted from the token.
                .getPayload() //Retrieves the payload (claims) from the parsed JWT token. The payload contains the claims that were set when the token was generated, such as the subject (email) and any custom claims (like role).
                .getSubject(); //Extracts and returns the "subject" (sub) claim from the JWT token's payload, which in this case is the email address of the user. This method is typically used during authentication to identify which user is making a request based on the token they provided.
    }

    public String extractRole(String token) //Extracts the role from a given JWT token. This method is used to retrieve the user's role from the token during authentication processes.
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}