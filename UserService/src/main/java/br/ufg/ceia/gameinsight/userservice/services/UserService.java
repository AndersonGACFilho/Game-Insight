package br.ufg.ceia.gameinsight.userservice.services;

import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.repositories.UserRepository;
import br.ufg.ceia.gameinsight.userservice.exceptions.ResourceNotFoundException;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

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
            logger.info("User: " + user.getEmail());

            // Set the user id
            logger.info("Setting the user id");
            logger.info("User: " + user.getId());
            user.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
            logger.info("New id: " + user.getId());
            // Hash the password
            logger.info("Hashing the password");
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            logger.info("User: " + user.getPassword());
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
     * @param userId The user id.
     * @param user The user to be updated.
     * @return The updated user.
     */
    public User updateUser(String userId, User user) {
        // Get the existing user
        logger.info("Getting the existing user");
        User existingUser = getUser(userId);
        // Set the id of the existing user to the new user
        logger.info("Setting the id of the existing user to the new user");
        existingUser.setId(user.getId());

        // Update the user
        logger.info("Updating the user");
        return userRepository.save(existingUser);
    }
}
