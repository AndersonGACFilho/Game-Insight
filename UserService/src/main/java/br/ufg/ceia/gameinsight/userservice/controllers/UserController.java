package br.ufg.ceia.gameinsight.userservice.controllers;

import br.ufg.ceia.gameinsight.userservice.configs.JwtResponse;
import br.ufg.ceia.gameinsight.userservice.domain.marketplace.MarketplaceProfile;
import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.dtos.LoginRequest;
import br.ufg.ceia.gameinsight.userservice.dtos.MarketplaceProfileDto;
import br.ufg.ceia.gameinsight.userservice.dtos.UserDto;
import br.ufg.ceia.gameinsight.userservice.exceptions.BadCredentialsException;
import br.ufg.ceia.gameinsight.userservice.exceptions.ResourceNotFoundException;
import br.ufg.ceia.gameinsight.userservice.services.AuthenticationService;
import br.ufg.ceia.gameinsight.userservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * This class represents the user controller.
 * <p>
 * This class is responsible for handling the user operations.
 */
@RestController()
@RequestMapping("/users")
public class UserController {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * The service that handles the authentication operations.
     */
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * The service that handles the user operations.
     */
    @Autowired
    private UserService userService;

    // Mapping for the user operations

    /**
     * Creates a new user.
     *
     * @param user The user to be created.
     */
    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        // Create a new user
        logger.info("Creating a new user: " + user.getEmail());
        User createdUser = userService.createUser(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Gets the logged user.
     * @return The logged user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getLoggedUser() {
        // Get the secret key
        logger.info("Getting the logged user");
        // Get the logged user
        User user = userService.getUser();
        // Return the user
        return ResponseEntity.ok(new UserDto(user));
    }

    /**
     * Get all users.
     *
     * @param page of the page (number of the page).
     * @param size of the page (number of users).
     * @param sortBy of the page (field name).
     * @param order of the page (ASC or DESC).
     * @return Page of users.
     */
    @GetMapping("/all")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {
        // Get all users
        Page<User> users = userService.getAllUsers(page, size, sortBy, order);
        // Return the users
        return ResponseEntity.ok(users.map(UserDto::new));
    }

    /**
     * Login the user.
     *
     * @param loginRequest The login request.
     * @return The token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            String token = authenticationService.authenticateUser(loginRequest);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            logger.error("Error authenticating the user", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new BadCredentialsException("Invalid email or password"));
        }
    }

     /**
     * Adds a marketplace profile to the logged user.
     *
     * @param marketplaceProfileDto The marketplace profile DTO.
     * @return The updated user.
     */
    @PostMapping("/marketplace")
    public ResponseEntity<UserDto> addMarketplaceProfile(@RequestBody MarketplaceProfileDto marketplaceProfileDto) {
        MarketplaceProfile marketplaceProfile = new MarketplaceProfile();
        marketplaceProfile.setUsername(marketplaceProfileDto.getUsername());
        marketplaceProfile.setMarketplaceType(marketplaceProfileDto.getMarketplaceType());
        /*marketplaceProfile.setGames(marketplaceProfileDto.getGames());*/
        User updatedUser = userService.addMarketplaceProfile(marketplaceProfile);
        return ResponseEntity.ok(new UserDto(updatedUser));
    }

    /**
     * Removes a marketplace profile from the logged user.
     *
     * @param username The marketplace profile username.
     * @return The updated user.
     */
    @DeleteMapping("/marketplace/{username}")
    public ResponseEntity<UserDto> removeMarketplaceProfile(@PathVariable String username) {
        User updatedUser = userService.removeMarketplaceProfile(username);
        return ResponseEntity.ok(new UserDto(updatedUser));
    }

    /**
     * Update user marketplace profile by marketplace profile username.
     */
    @PutMapping("/marketplace/{username}")
    public ResponseEntity<UserDto> getUserByMarketplaceProfileUsername(@RequestBody MarketplaceProfileDto marketplaceProfileDto, @PathVariable String username) {
        User updatedUser = userService.updateMarketplaceProfile(username, marketplaceProfileDto);
        return ResponseEntity.ok(new UserDto(updatedUser));
    }
}