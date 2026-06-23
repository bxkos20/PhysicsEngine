package engine.config.implementations;

import engine.config.ISettings;

public class WorldSettings implements ISettings {
    public int width;
    public int height;

    /**
     * Default constructor.
     * Sets a reasonable default world size for a sandbox environment.
     */
    public WorldSettings() {
        this.width = 1000;
        this.height = 1000;
    }

    /**
     * Fully parameterized constructor.
     * @param width The width of the simulation world in simulation units.
     * @param height The height of the simulation world in simulation units.
     */
    public WorldSettings(int width, int height) {
        this.width = width;
        this.height = height;
    }
}