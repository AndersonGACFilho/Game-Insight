package br.ufg.ceia.gameinsight.userservice.dtos;

import java.io.Serial;
import java.io.Serializable;

import br.ufg.ceia.gameinsight.userservice.domain.user.User;
import br.ufg.ceia.gameinsight.userservice.domain.user.UserProfile;
import br.ufg.ceia.gameinsight.userservice.domain.marketplace.MarketplaceProfile;
import br.ufg.ceia.gameinsight.userservice.dtos.MarketplaceProfileDto;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @brief Data Transfer Object for User
 * @details This class is responsible for the data transfer of User objects.
 * It is used to send User data to the client, without sending sensitive
 * information like passwords.
 * @see br.ufg.ceia.gameinsight.userservice.dtos;
 */
public class UserDto implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier for the user.
     */
    private Integer id;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The user profile of the user.
     */
    private UserProfile userProfile;

    /**
     * The marketplace profiles associated with the user (e.g., Steam, PlayStation, Xbox).
     */
    private List<MarketplaceProfileDto> marketplaceProfiles;

    public List<MarketplaceProfileDto> getMarketplaceProfiles() {
        return marketplaceProfiles;
    }

    public void setMarketplaceProfiles(List<MarketplaceProfile> marketplaceProfiles) {
        // Verifica se a lista é nula antes de tentar converter
        if (marketplaceProfiles != null) {
            // Converte a lista de MarketplaceProfile para MarketplaceProfileDto
            this.marketplaceProfiles = marketplaceProfiles.stream()
                    .map(MarketplaceProfileDto::fromMarketplaceProfile)
                    .collect(Collectors.toList());
        } else {
            // Se a lista for nula, define marketplaceProfiles como uma lista vazia
            this.marketplaceProfiles = List.of();
        }
    }

    /**
     * Default constructor
     */
    public UserDto() {
    }

    /**
     * Constructor with parameters
     * @param user the User object to be converted to a UserDto
     */
    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getName();
        this.email = user.getEmail();
        this.userProfile = user.getProfile();
        this.setMarketplaceProfiles(user.getMarketplaceProfiles());
    }

    /**
     * Get the user ID
     * @return the user ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the user ID
     * @param id the user ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Get the username of the user
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username of the user
     * @param username the username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the user profile of the user
     * @return the user profile of the user
     */
    public UserProfile getUserProfile() {
        return userProfile;
    }

    /**
     * Set the user profile of the user
     * @param userProfile the user profile of the user
     */
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    /**
     * Get the email of the user
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email of the user
     * @param email the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    //  Convert User to UserDto
    /**
     * Convert a User object to a UserDto object
     * @param user the User object to be converted
     * @return the UserDto object
     */
    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setUserProfile(user.getProfile());
        userDto.setMarketplaceProfiles(user.getMarketplaceProfiles());
        return userDto;
    }
}
