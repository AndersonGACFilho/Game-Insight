package br.ufg.ceia.gameinsight.userservice.domain.user;

import br.ufg.ceia.gameinsight.userservice.domain.profiles.SteamProfile;
import br.ufg.ceia.gameinsight.userservice.domain.profiles.PlaystationProfile;
import br.ufg.ceia.gameinsight.userservice.domain.profiles.XboxProfile;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

/**
 * This class represents the MarketplaceProfiles entity.
 * <p>
 * This class groups profiles for different marketplaces associated with the user.
 */
public class MarketplaceProfiles {

    /**
     * The Steam profile associated with the user.
     */
    @Field
    private SteamProfile steam;

    /**
     * The PlayStation profile associated with the user.
     */
    @Field
    private PlaystationProfile playstation;

    /**
     * The Xbox profile associated with the user.
     */
    @Field
    private XboxProfile xbox;

    // Getters and setters

    /**
     * Retrieves the Steam profile associated with the user.
     *
     * @return The Steam profile associated with the user.
     */
    public SteamProfile getSteam() {
        return steam;
    }

    /**
     * Sets the Steam profile associated with the user.
     *
     * @param steam The Steam profile to associate with the user.
     */
    public void setSteam(SteamProfile steam) {
        this.steam = steam;
    }

    /**
     * Retrieves the PlayStation profile associated with the user.
     *
     * @return The PlayStation profile associated with the user.
     */
    public PlaystationProfile getPlaystation() {
        return playstation;
    }

    /**
     * Sets the PlayStation profile associated with the user.
     *
     * @param playstation The PlayStation profile to associate with the user.
     */
    public void setPlaystation(PlaystationProfile playstation) {
        this.playstation = playstation;
    }

    /**
     * Retrieves the Xbox profile associated with the user.
     *
     * @return The Xbox profile associated with the user.
     */
    public XboxProfile getXbox() {
        return xbox;
    }

    /**
     * Sets the Xbox profile associated with the user.
     *
     * @param xbox The Xbox profile to associate with the user.
     */
    public void setXbox(XboxProfile xbox) {
        this.xbox = xbox;
    }

    // Equals and hashcode

    /**
     * Compares this instance with another object and determines if they are equal.
     *
     * @param o The object to compare this instance with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketplaceProfiles that)) return false;
        return Objects.equals(steam, that.steam) &&
                Objects.equals(playstation, that.playstation) &&
                Objects.equals(xbox, that.xbox);
    }

    /**
     * Generates a hash code for this instance.
     *
     * @return The hash code for this instance.
     */
    @Override
    public int hashCode() {
        return Objects.hash(steam, playstation, xbox);
    }

    // toString
    /**
     * Returns a string representation of this instance.
     *
     * @return A string representation of this instance.
     */
    @Override
    public String toString() {
        return "MarketplaceProfiles{" +
                "steam=" + steam +
                ", playstation=" + playstation +
                ", xbox=" + xbox +
                '}';
    }
}