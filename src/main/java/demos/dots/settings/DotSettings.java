package demos.dots.settings;

import engine.config.ISettings;

public class DotSettings implements ISettings {
    public float defaultDotRadius;
    public int totalDots;
    public int dotTypes;
    public float gravityConstant;
    public float minInteractionDistance;
    public float maxInteractionDistance;

    /**
     * Default constructor.
     * Sets default values for an interesting and visually appealing dot simulation.
     */
    public DotSettings() {
        this.defaultDotRadius = 5.0f;
        this.totalDots = 2500;
        this.dotTypes = 5;
        this.gravityConstant = 50.0f;
        this.minInteractionDistance = 15.0f;
        this.maxInteractionDistance = 100.0f;
    }

    /**
     * Fully parameterized constructor.
     * @param defaultDotRadius The radius of the dots.
     * @param totalDots The total number of dots in the simulation.
     * @param dotTypes The number of different types of dots.
     * @param gravityConstant The strength of the gravitational-like interaction.
     * @param minInteractionDistance The minimum distance for interaction calculations.
     * @param maxInteractionDistance The maximum distance for interaction calculations.
     */
    public DotSettings(float defaultDotRadius, int totalDots, int dotTypes, float gravityConstant, float minInteractionDistance, float maxInteractionDistance) {
        this.defaultDotRadius = defaultDotRadius;
        this.totalDots = totalDots;
        this.dotTypes = dotTypes;
        this.gravityConstant = gravityConstant;
        this.minInteractionDistance = minInteractionDistance;
        this.maxInteractionDistance = maxInteractionDistance;
    }
}