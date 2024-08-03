package br.ufg.ceia.gameinsight.userservice.services;

import br.ufg.ceia.gameinsight.userservice.domain.marketplace.MarketplaceProfile;
import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.domain.user.UserProfile;
import br.ufg.ceia.gameinsight.userservice.dtos.MarketplaceProfileDto;
import br.ufg.ceia.gameinsight.userservice.repositories.UserRepository;
import br.ufg.ceia.gameinsight.userservice.exceptions.ResourceNotFoundException;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

/**
 * This class represents the service for the User entity.
 * <p>
 * This class provides methods to interact with the User entity.
 */
@Service
public class UserService {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * The repository for the user entity.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The service that generates unique identifiers.
     */
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    public User getUser() {
        // Get the authenticated user
        logger.info("Getting the authenticated user");
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        // Get the email of the authenticated user
        logger.info("Getting the email of the authenticated user");
        String authenticatedUserEmail = authentication.getName();
        // Get the user by the email
        logger.info("Getting the user by the email");
        return userRepository.findByEmail(authenticatedUserEmail)
            .orElseThrow(() ->
            new ResourceNotFoundException("User not found with email: " + authenticatedUserEmail));
    }

    /**
     * Get all users.
     * @param page of the page (number of the page).
     * @param size of the page (number of users).
     * @param sort of the page (field name).
     * @param direction of the page (ASC or DESC).
     * @return Page of users.
     */
    public Page<User> getAllUsers(
        int page, int size, String sort, String direction)
    {
        // Create a pageable object
        logger.info("Creating a pageable object");
        PageRequest pageable = PageRequest.of(page, size,
            Sort.Direction.fromString(direction), sort);
        // Get all users
        return userRepository.findAll(pageable);
    }

    /**
     * Creates a new user.
     *
     * @return The created user.
     */
    public User createUser(User user) {
        try {
            // Save the user
            logger.info("Saving the user");

            // Set the user id
            logger.info("Setting the user id");
            user.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));

            // Hash the password
            logger.info("Hashing the password");
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

            // Created At
            logger.info("Setting the created at date on the create user service");
            user.setCreatedAt(new Date());
            // Updated At
            logger.info("Setting the updated at date on the create user service");
            user.setUpdatedAt(new Date());

            // Save the user
            logger.info("Saving the user");
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            logger.error("Error saving the user");
            logger.error(e.getMessage());
            throw new DataIntegrityViolationException("The user already with email: " + user.getEmail(), e);
        }
    }

    /**
     * Gets the user by the user id.
     *
     * @param userId The user id.
     * @return The user with the given user id.
     */
    public User getUser(String userId) {
        // Get the user by the user id
        logger.info("Getting the user by the user id");
        return userRepository.findById(userId).orElseThrow(
            () -> new ResourceNotFoundException("User not found with id " + userId));
    }

    /**
     * Updates the user.
     *
     * @param user The user to be updated.
     */
    public void updateUser(User user) {
        // Get the existing user
        logger.info("Getting the existing user");
        User existingUser = getUser();

        // Update the user if it is not null
        logger.info("Updating the user");
        if (user.getName() != null && !user.getName().isBlank()) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(user.getPassword());
            logger.info("Hashing the new password");
            existingUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }
        // Updated At
        logger.info("Setting the updated at date on the update user service");
        Date date = new Date();
        user.setUpdatedAt(date);

        // Save the user
        logger.info("Updating the user");
        userRepository.save(existingUser);
    }

    /**
     * Gets the user profile.
     *
     * @return The user profile.
     */
    public UserProfile getUserProfile() {
        User user = getUser();
        return user.getProfile();
    }

    /**
     * Updates the user profile.
     *
     * @param userProfile The user profile to update.
     * @return The updated user profile.
     */
    public UserProfile updateUserProfile(UserProfile userProfile) {
        User user = getUser();
        user.setProfile(userProfile);

        // Updated At
        logger.info("Setting the updated at date on the user on the update user profile service");
        Date date = new Date();
        user.setUpdatedAt(date);

        return userRepository.save(user).getProfile();
    }

    /**
     * Gets all marketplace profiles of the authenticated user.
     *
     * @param page The page number.
     * @param size The page size.
     * @param sortBy The field to sort by.
     * @param order The order to sort by.
     * @return The marketplace profiles of the authenticated user.
     */
    public Page<MarketplaceProfile> getMarketplaceProfiles(int page, int size, String sortBy, String order) {
        User user = getUser();
        List<MarketplaceProfile> marketplaceProfiles = user.getMarketplaceProfiles();
        // Apply sorting
        Sort.Direction direction = Sort.Direction.fromString(order);
        Sort sort = Sort.by(direction, sortBy);

        // Apply pagination
        Pageable pageable = PageRequest.of(page, size, sort);
        int start = Math.min((int)pageable.getOffset(), marketplaceProfiles.size());
        int end = Math.min((start + pageable.getPageSize()), marketplaceProfiles.size());
        List<MarketplaceProfile> paginatedList = marketplaceProfiles.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, marketplaceProfiles.size());
    }

    /**
     * Adds a marketplace profile to the authenticated user.
     *
     * @param marketplaceProfile The marketplace profile to add.
     * @return The updated user.
     */
    public User addMarketplaceProfile(MarketplaceProfile marketplaceProfile) {
        User user = getUser();

        // Get the list of marketplace profiles
        List<MarketplaceProfile> marketplaceProfiles = user.getMarketplaceProfiles();

        // Add the new marketplace profile
        marketplaceProfiles.add(marketplaceProfile);

        // Set the updated marketplace profiles
        user.setMarketplaceProfiles(marketplaceProfiles);

        // Save the user and return it
        return userRepository.save(user);
    }

    /**
     * Removes a marketplace profile from the authenticated user by username.
     *
     * @param username The marketplace profile username to remove.
     * @return The updated user.
     */
    public User removeMarketplaceProfile(String username) {
        User user = getUser();
        // Remove the marketplace profile with the given username
        boolean itWasRemoved =
                user.getMarketplaceProfiles().removeIf(profile -> profile.getUsername().equals(username));
        // Save the user and return it
        return itWasRemoved ? userRepository.save(user) : user;
    }

    /**
     * Updates a marketplace profile of the authenticated user.
     *
     * @param username The username of the marketplace profile to update.
     * @param updatedMarketProfile The updated marketplace profile.
     * @return The updated user.
     */
    public User updateMarketplaceProfile(String username, MarketplaceProfile updatedMarketProfile) {
        User user = getUser();

        // Get the list of marketplace profiles
        List<MarketplaceProfile> marketplaceProfiles = user.getMarketplaceProfiles();

        // Update the marketplace profile
        for (int i = 0; i < marketplaceProfiles.size(); i++) {
            if (marketplaceProfiles.get(i).getUsername().equals(username)) {
                // Set the updated marketplace profiles
                marketplaceProfiles.set(i, updatedMarketProfile);
                return userRepository.save(user);
            }
        }

        // Save the user and return it
        return user;
    }
}