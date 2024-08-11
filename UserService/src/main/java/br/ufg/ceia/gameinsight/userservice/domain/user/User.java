package br.ufg.ceia.gameinsight.userservice.domain.user;
import br.ufg.ceia.gameinsight.userservice.domain.marketplace.MarketplaceProfile;
import br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.UserPc;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

/**
 * This class represents the User entity.
 * <p>
 * This class is used to represent the User JSON object in the MongoDB database.
 */
@Document(collection = "users")
public class User implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the sequence used to generate unique identifiers for users.
     */
    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";

    /**
     * The unique identifier for the user.
     */
    @Id
    private Long id;

    /**
     * The name of the user.
     */
    @Field
    private String name;

    /**
     * The email address of the user.
     */
    @Indexed(unique = true)
    private String email;

    /**
     * The hashed password of the user.
     */
    @Field
    private String password;

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
    private List<MarketplaceProfile> marketplaceProfiles;

    /**
     * The friend list of the user.
     */
    @Field
    private List<Friend> friends;

    /**
     * Pc Configuration
     */
    @Field
    private UserPc userPc;


    // Getters and setters

    /**
     * Gets the unique identifier for the user.
     *
     * @return The unique identifier for the user.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param id The unique identifier for the user.
     */
    public void setId(Long id) {
        this.id = id;
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
     * Gets the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The password of the user.
     */
    public void setPassword(String password) {
        this.password = password;
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
    public List<MarketplaceProfile> getMarketplaceProfiles() {
        return marketplaceProfiles != null ? marketplaceProfiles : new ArrayList<MarketplaceProfile>();
    }

    /**
     * Sets the marketplace profiles associated with the user.
     *
     * @param marketplaceProfiles The marketplace profiles associated with the user.
     */
    public void setMarketplaceProfiles( List<MarketplaceProfile>  marketplaceProfiles) {
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

    /**
     * Gets the PC configuration of the user.
     *
     * @return The PC configuration of the user.
     */
    public UserPc getUserPc() {
        return userPc;
    }

    /**
     * Sets the PC configuration of the user.
     *
     * @param userPc The PC configuration of the user.
     */
    public void setUserPc(UserPc userPc) {
        this.userPc = userPc;
    }

    // Adders and removers
    /**
     * Adds a friend to the user's friend list.
     *
     * @param friend The friend to add.
     */
    public void addFriend(Friend friend) {
        friends.add(friend);
    }

    /**
     * Removes a friend from the user's friend list.
     *
     * @param friend The friend to remove.
     */
    public void removeFriend(Friend friend) {
        friends.remove(friend);
    }

    /**
     * Adds a marketplace profile to the user's marketplace profiles.
     *
     * @param marketplaceProfile The marketplace profile to add.
     */
    public void addMarketplaceProfile(MarketplaceProfile marketplaceProfile) {
        marketplaceProfiles.add(marketplaceProfile);
    }

    /**
     * Removes a marketplace profile from the user's marketplace profiles.
     *
     * @param marketplaceProfile The marketplace profile to remove.
     */
    public void removeMarketplaceProfile(MarketplaceProfile marketplaceProfile) {
        marketplaceProfiles.remove(marketplaceProfile);
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

        if (!Objects.equals(id, user.id))
            return false;
        if (!Objects.equals(name, user.name))
            return false;
        if (!Objects.equals(email, user.email))
            return false;
        if (!Objects.equals(password, user.password))
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
        return Objects.hash( id, name, email,
                password, createdAt, updatedAt,
            profile, marketplaceProfiles, friends);
    }
}