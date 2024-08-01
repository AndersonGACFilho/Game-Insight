package br.ufg.ceia.gameinsight.userservice.dtos;

import br.ufg.ceia.gameinsight.userservice.domain.games.Game;
import br.ufg.ceia.gameinsight.userservice.domain.marketplace.MarketplaceProfile;
import br.ufg.ceia.gameinsight.userservice.domain.marketplace.MarketplaceType;

import java.io.Serializable;
import java.util.List;

/**
 * @brief Data Transfer Object for MarketplaceProfile
 * @details This class is responsible for the data transfer of MarketplaceProfile objects.
 * It is used to send MarketplaceProfile data to the client.
 */
public class MarketplaceProfileDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The username of the user on Marketplace.
     */
    private String username;

    /**
     * The type of the Marketplace can be either Steam, PlayStation, or Xbox.
     */
    private MarketplaceType marketplaceType;

    /**
     * The list of games associated with the user's Marketplace profile.
     */
    private List<Game> games;

    /**
     * Default constructor
     */
    public MarketplaceProfileDto() {
    }

    /**
     * Constructor with parameters
     * @param marketplaceProfile the MarketplaceProfile object to be converted to a MarketplaceProfileDto
     */
    public MarketplaceProfileDto(MarketplaceProfile marketplaceProfile) {
        this.username = marketplaceProfile.getUsername();
        this.marketplaceType = marketplaceProfile.getMarketplaceType();
        this.games = marketplaceProfile.getGames();
    }

    /**
     * Get the username of the user on Marketplace.
     * @return The username of the user on Marketplace.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username of the user on Marketplace.
     * @param username The username of the user on Marketplace.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the type of the Marketplace.
     * @return The type of the Marketplace.
     */
    public MarketplaceType getMarketplaceType() {
        return marketplaceType;
    }

    /**
     * Set the type of the Marketplace.
     * @param marketplaceType The type of the Marketplace.
     */
    public void setMarketplaceType(MarketplaceType marketplaceType) {
        this.marketplaceType = marketplaceType;
    }

    /**
     * Get the list of games associated with the user's Marketplace profile.
     * @return The list of games associated with the user's Marketplace profile.
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     * Set the list of games associated with the user's Marketplace profile.
     * @param games The list of games associated with the user's Marketplace profile.
     */
    public void setGames(List<Game> games) {
        this.games = games;
    }

    /**
     * Convert a MarketplaceProfile object to a MarketplaceProfileDto object
     * @param marketplaceProfile the MarketplaceProfile object to be converted
     * @return the MarketplaceProfileDto object
     */
    public static MarketplaceProfileDto fromMarketplaceProfile(MarketplaceProfile marketplaceProfile) {
        MarketplaceProfileDto dto = new MarketplaceProfileDto();
        dto.setUsername(marketplaceProfile.getUsername());
        dto.setMarketplaceType(marketplaceProfile.getMarketplaceType());
        dto.setGames(marketplaceProfile.getGames());
        return dto;
    }
}
