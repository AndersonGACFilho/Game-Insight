package br.ufg.ceia.gameinsight.userservice.repositories;

import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * This interface represents the repository for the User entity.
 * <p>
 * This interface provides methods to interact with the User entity in the database.
 */
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds all users.
     *
     * @param pageable The pagination information.
     * @return A page of all users.
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Finds a user by their unique identifier.
     *
     * @param Id The unique identifier of the user.
     * @return The user with the specified unique identifier.
     */
    Optional<User> findById(String Id);

    /**
     * Finds a user by their name.
     *
     * @param name The name of the user.
     * @return The user with the specified name.
     */
    List<User> findByName(String name);

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user.
     * @return The user with the specified email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all users with a name that contains the specified name.
     *
     * @param name The name to search for.
     * @param pageable The pagination information.
     * @return A page of users with names that contain the specified name.
     */
    Page<User> findByNameContaining(String name, Pageable pageable);
}