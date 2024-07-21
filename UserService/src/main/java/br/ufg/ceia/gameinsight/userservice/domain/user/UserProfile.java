package br.ufg.ceia.gameinsight.userservice.domain.user;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Objects;

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
    @Indexed(unique = true)
    private String phoneNumber;

    // Getters and setters

    /**
     * Gets the first name of the user.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName The first name of the user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName The last name of the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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

    /**
     * Gets the phone number of the user.
     *
     * @return The phone number of the user.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber The phone number of the user.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
        return firstName.equals(that.firstName) &&
                lastName.equals(that.lastName) &&
                birthdate.equals(that.birthdate) &&
                phoneNumber.equals(that.phoneNumber);
    }

    /**
     * Returns the hash code of this UserProfile.
     *
     * @return The hash code of this UserProfile.
     */
    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, birthdate, phoneNumber);
    }

    /**
     * Returns the string representation of this UserProfile.
     *
     * @return The string representation of this UserProfile.
     */
    @Override
    public String toString() {
        return "UserProfile{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthdate=" + birthdate +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}