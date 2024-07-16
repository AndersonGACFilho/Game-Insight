package br.ufg.ceia.gameinsight.userservice.domain.user;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the User entity.
 * <p>
 * This class is used to represent the User JSON object in the MongoDB database.
 */
@Document(collection = "users")
public class User {

    /**
     * The unique identifier for the user.
     */
    @Id
    private String userId;

    /**
     * The name of the user.
     */
    @Field
    private String name;

    /**
     * The email address of the user.
     */
    @Field
    private String email;

    /**
     * The hashed password of the user.
     */
    @Field
    private String passwordHash;

    /**
     * The date when the user was created.
     */
    @Field
    private Date createdAt;

    /**
     * The date when the user was last updated.
     */
    @Field
    private Date updatedAt;

    /**
     * The profile of the user containing personal details.
     */
    @Field
    private UserProfile profile;

    /**
     * The marketplace profiles associated with the user (e.g., Steam, PlayStation, Xbox).
     */
    @Field
    private MarketplaceProfiles marketplaceProfiles;

    /**
     * The friend list of the user.
     */
    @Field
    private List<Friend> friends;

    // Getters and setters

    /**
     * Gets the unique identifier for the user.
     *
     * @return The unique identifier for the user.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param userId The unique identifier for the user.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the name of the user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name The name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the hashed password of the user.
     *
     * @return The hashed password of the user.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the hashed password of the user.
     *
     * @param password The password of the user.
     */
    public void setPasswordHash(String password) {
        // Hash the password
        this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Gets the date when the user was created.
     *
     * @return The date when the user was created.
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the date when the user was created.
     *
     * @param createdAt The date when the user was created.
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the date when the user was last updated.
     *
     * @return The date when the user was last updated.
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the date when the user was last updated.
     *
     * @param updatedAt The date when the user was last updated.
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the profile of the user containing personal details.
     *
     * @return The profile of the user containing personal details.
     */
    public UserProfile getProfile() {
        return profile;
    }

    /**
     * Sets the profile of the user containing personal details.
     *
     * @param profile The profile of the user containing personal details.
     */
    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    /**
     * Gets the marketplace profiles associated with the user.
     *
     * @return The marketplace profiles associated with the user.
     */
    public MarketplaceProfiles getMarketplaceProfiles() {
        return marketplaceProfiles;
    }

    /**
     * Sets the marketplace profiles associated with the user.
     *
     * @param marketplaceProfiles The marketplace profiles associated with the user.
     */
    public void setMarketplaceProfiles(MarketplaceProfiles marketplaceProfiles) {
        this.marketplaceProfiles = marketplaceProfiles;
    }

    /**
     * Gets the friend list of the user.
     *
     * @return The friend list of the user.
     */
    public List<Friend> getFriends() {
        return friends;
    }

    /**
     * Sets the friend list of the user.
     *
     * @param friends The friend list of the user.
     */
    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    // Equals and hashCode

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(userId, user.userId))
            return false;
        if (!Objects.equals(name, user.name))
            return false;
        if (!Objects.equals(email, user.email))
            return false;
        if (!Objects.equals(passwordHash, user.passwordHash))
            return false;
        if (!Objects.equals(createdAt, user.createdAt))
            return false;
        if (!Objects.equals(updatedAt, user.updatedAt))
            return false;
        if (!Objects.equals(profile, user.profile))
            return false;
        if (!Objects.equals(marketplaceProfiles, user.marketplaceProfiles))
            return false;

        return Objects.equals(friends, user.friends);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email,
            passwordHash, createdAt, updatedAt,
            profile, marketplaceProfiles, friends);
    }
}