package br.ufg.ceia.gameinsight.userservice.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.security.Key;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * The security configuration for the application.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * The JWT secret.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Configures the JWT authentication filter.
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain.
     * @throws ServletException If an error occurs.
     * @throws IOException If an error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Claims claims = parseToken(token);
            if (claims != null) {
                // Get the user email
                logger.info("Getting the user email");
                String userEmail = claims.getSubject();
                logger.info("User email: " + userEmail);
                // Create the authentication
                logger.info("Creating the authentication");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userEmail, null, Collections.emptyList());
                // Set the details
                logger.info("Setting the details");
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Set the authentication
                logger.info("Setting the authentication");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }
        filterChain.doFilter(request, response);
    }

    /**
     * Parses the token.
     *
     * @param token The token to be parsed.
     * @return The claims.
     */
    private Claims parseToken(String token) {
        try {
            // Parse the token
            logger.info("Parsing the token");
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            // Get the claims
            logger.info("Key: " + key.toString());
            // Create the parser
            JwtParser parser = Jwts.parser()
                    .setSigningKey(key)
                    .build();
            return parser.parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }
}