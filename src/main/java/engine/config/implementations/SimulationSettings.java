package engine.config.implementations;

import engine.config.ISettings;

public class SimulationSettings implements ISettings {
    public float fixedTimestepSimulation;
    public float fixedTimestepRendering;
    public float maxFrameTime;
    public float defaultTimescale;
    public int physicsSubSteps;

    /**
     * Default constructor.
     * Sets standard values for a stable and smooth simulation (60Hz physics, 30Hz render).
     */
    public SimulationSettings() {
        this.fixedTimestepSimulation = 1.0f / 60.0f;
        this.fixedTimestepRendering = 1.0f / 30.0f;
        this.maxFrameTime = 0.25f;
        this.defaultTimescale = 1.0f;
        this.physicsSubSteps = 2;
    }

    /**
     * Fully parameterized constructor.
     * @param fixedTimestepSimulation The fixed time step for physics updates.
     * @param fixedTimestepRendering The fixed time step for rendering updates.
     * @param maxFrameTime The maximum frame time to prevent spiral of death.
     * @param defaultTimescale The initial time scale for the simulation.
     * @param physicsSubSteps The number of sub-steps for physics calculations.
     */
    public SimulationSettings(float fixedTimestepSimulation, float fixedTimestepRendering, float maxFrameTime, float defaultTimescale, int physicsSubSteps) {
        this.fixedTimestepSimulation = fixedTimestepSimulation;
        this.fixedTimestepRendering = fixedTimestepRendering;
        this.maxFrameTime = maxFrameTime;
        this.defaultTimescale = defaultTimescale;
        this.physicsSubSteps = physicsSubSteps;
    }
}