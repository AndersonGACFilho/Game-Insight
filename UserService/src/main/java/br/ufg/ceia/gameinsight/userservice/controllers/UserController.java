package br.ufg.ceia.gameinsight.userservice.controllers;

import br.ufg.ceia.gameinsight.userservice.configs.JwtResponse;
import br.ufg.ceia.gameinsight.userservice.domain.marketplace.MarketplaceProfile;
import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.domain.user.UserProfile;
import br.ufg.ceia.gameinsight.userservice.dtos.LoginRequest;
import br.ufg.ceia.gameinsight.userservice.dtos.MarketplaceProfileDto;
import br.ufg.ceia.gameinsight.userservice.dtos.UserDto;
import br.ufg.ceia.gameinsight.userservice.dtos.UserProfileDto;
import br.ufg.ceia.gameinsight.userservice.exceptions.BadCredentialsException;
import br.ufg.ceia.gameinsight.userservice.exceptions.ResourceNotFoundException;
import br.ufg.ceia.gameinsight.userservice.services.AuthenticationService;
import br.ufg.ceia.gameinsight.userservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.dao.DataIntegrityViolationException;
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
        // Check if the user is valid
        if (user.getEmail() == null || user.getPassword() == null
            || user.getEmail().isBlank() || user.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            User createdUser = userService.createUser(user);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(createdUser.getId()).toUri();
            return ResponseEntity.created(location).build();
        }
        catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
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
     * Updates the logged user.
     *
     * @param user The user to be updated.
     */
    @PutMapping("/me")
    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        // Update the user
        logger.info("Updating the logged user");
        // Check if the user is valid
        try {
            userService.updateUser(user);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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

    @GetMapping("/me/profile")
    public ResponseEntity<UserProfileDto> getUserProfile() {
        logger.info("Getting the user profile");
        UserProfile userProfile = userService.getUserProfile();
        return ResponseEntity.ok(new UserProfileDto(userProfile));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<UserProfileDto> updateUserProfile(@RequestBody UserProfileDto userProfileDto) {
        logger.info("Updating the user profile");
        UserProfile updatedProfile = userService.updateUserProfile(userProfileDto.toUserProfile());
        return ResponseEntity.ok(new UserProfileDto(updatedProfile));
    }

    /**
     * Get all marketplace profiles of the logged user.
     *
     * @return The marketplace profiles of the logged user.
     */
    @GetMapping("/marketplace")
    public ResponseEntity<Page<MarketplaceProfileDto>> getMarketplaceProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {
        try {
            Page<MarketplaceProfile> marketplaceProfiles =
                    userService.getMarketplaceProfiles(page, size, sortBy, order);
            return ResponseEntity.ok(marketplaceProfiles.map(MarketplaceProfileDto::new));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
        try {
            User user = userService.getUser(); // Ensure we get the user first
            MarketplaceProfile marketplaceProfile = new MarketplaceProfile();
            marketplaceProfile.setUsername(marketplaceProfileDto.getUsername());
            marketplaceProfile.setMarketplaceType(marketplaceProfileDto.getMarketplaceType());
            User updatedUser = userService.addMarketplaceProfile(marketplaceProfile);
            return ResponseEntity.ok(new UserDto(updatedUser));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if user is not found
        }
    }

    /**
     * Removes a marketplace profile from the logged user.
     *
     * @param username The marketplace profile username.
     * @return The updated user.
     */
    @DeleteMapping("/marketplace/{username}")
    public ResponseEntity<Void> removeMarketplaceProfile(@PathVariable String username) {
        try {
            User updatedUser = userService.removeMarketplaceProfile(username);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Update user marketplace profile by marketplace profile username.
     */
    @PutMapping("/marketplace/{username}")
    public ResponseEntity<Void> updateByMarketplaceProfileUsername(
            @RequestBody MarketplaceProfile marketplaceProfileDto,
            @PathVariable String username)
    {
        try {
            User updatedUser = userService.updateMarketplaceProfile(username, marketplaceProfileDto);
            return ResponseEntity.noContent().build();

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}