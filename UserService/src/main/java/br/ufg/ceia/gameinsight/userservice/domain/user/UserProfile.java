package br.ufg.ceia.gameinsight.userservice.domain.user;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

/**
 * This class represents the UserProfile entity.
 * <p>
 * This class holds personal details of the user.
 */
public class UserProfile implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The birthdate of the user.
     */
    @Field
    private Date birthdate;

    // Getters and setters

    /**
     * Gets the birthdate of the user.
     *
     * @return The birthdate of the user.
     */
    public Date getBirthdate() {
        return birthdate;
    }

    /**
     * Sets the birthdate of the user.
     *
     * @param birthdate The birthdate of the user.
     */
    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    // Equals, hashCode and toString

    /**
     * Checks if the object is equal to this UserProfile.
     *
     * @param o The object to compare.
     * @return True if the object is equal to this UserProfile, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return birthdate.equals(that.birthdate);
    }

    /**
     * Returns the hash code of this UserProfile.
     *
     * @return The hash code of this UserProfile.
     */
    @Override
    public int hashCode() {
        return Objects.hash(birthdate);
    }

    /**
     * Returns the string representation of this UserProfile.
     *
     * @return The string representation of this UserProfile.
     */
    @Override
    public String toString() {
        return "UserProfile{" +
                "birthdate=" + birthdate +
                '}';
    }
}