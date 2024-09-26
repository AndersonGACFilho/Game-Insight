package br.ufg.ceia.gameinsight.userservice.services;

import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.dtos.LoginRequest;
import br.ufg.ceia.gameinsight.userservice.exceptions.BadCredentialsException;
import br.ufg.ceia.gameinsight.userservice.exceptions.ResourceNotFoundException;
import br.ufg.ceia.gameinsight.userservice.repositories.UserRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

/**
 * This class represents the service for the authentication.
 * <p>
 * This class provides methods to authenticate the user.
 */
@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    /**
     * The repository for the user entity.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The secret key for the JWT token.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * The expiration time for the JWT token.
     */
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Authenticates the user.
     *
     * @param loginRequest The login request.
     * @return The JWT token.
     */
    public String authenticateUser(LoginRequest loginRequest) {
        logger.info("Authenticating the user with email: " + loginRequest.getEmail());
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));
        logger.info("User found with email: " + loginRequest.getEmail());
        logger.info("Checking the password");
        if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            logger.error("Invalid password");
            logger.error("Password tried: " + loginRequest.getPassword());
            throw new BadCredentialsException("Invalid email or password");
        }
        logger.info("Password is correct");
        return generateJwtToken(user);
    }

    /**
     * Generates a JWT token for the user.
     *
     * @param user The user.
     * @return The JWT token.
     */
    private String generateJwtToken(User user) {
        // Generate the JWT token
        logger.info("Generating JWT token for user: " + user.getEmail());
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        // Create the JWT token
        logger.info("Creating the JWT token");
        Instant now = Instant.now();

        // Create the JWT token
        logger.info("Creating the JWT token");
        JwtBuilder builder = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds( jwtExpiration )))
                .signWith(key);

        // Return the JWT token
        logger.info("Returning the JWT token");
        return builder.compact();
    }

    public String getSecretKey() {
        return jwtSecret;
    }
}
