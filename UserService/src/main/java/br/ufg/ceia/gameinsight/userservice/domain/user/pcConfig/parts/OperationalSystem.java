package br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts;

/**
 * Represents the operational system of a user's PC.
 * <br>
 * This class is part of the User's PC configuration.
 * <br>
 * This class includes the operational system of the user's PC.
 */
public class OperationalSystem {
    /**
     * The name of the operational system.
     */
    private String name;

    public OperationalSystem() {
    }

    /**
     * Creates an operational system with the given name.
     *
     * @param name The name of the operational system.
     */
    public OperationalSystem(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the operational system.
     *
     * @return The name of the operational system.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the operational system.
     *
     * @param name The name of the operational system.
     */
    public void setName(String name) {
        this.name = name;
    }
}
