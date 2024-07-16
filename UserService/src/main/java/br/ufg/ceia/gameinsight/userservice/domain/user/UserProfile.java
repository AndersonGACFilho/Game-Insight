package br.ufg.ceia.gameinsight.userservice.domain.user;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * This class represents the UserProfile entity.
 * <p>
 * This class holds personal details of the user.
 */
public class UserProfile {

    /**
     * The first name of the user.
     */
    @Field
    private String firstName;

    /**
     * The last name of the user.
     */
    @Field
    private String lastName;

    /**
     * The birthdate of the user.
     */
    @Field
    private Date birthdate;

    /**
     * The phone number of the user.
     */
    @Field
    private String phoneNumber;

    // Getters and setters
}