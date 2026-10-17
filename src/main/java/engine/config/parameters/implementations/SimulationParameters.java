package engine.config.parameters.implementations;

import engine.config.parameters.IParameters;
import engine.config.settings.implementations.SimulationSettings;

public class SimulationParameters implements IParameters {
    public float fixedTimestepSimulation;
    public float fixedTimestepRendering;
    public float maxFrameTime;
    public float timescale;
    public int physicsSubSteps;
    public boolean pause;
    public boolean render;

    /**
     * Default constructor.
     * 
     * @param settings The SimulationSettings object from which to initialize the parameters.
     */
    public SimulationParameters(SimulationSettings settings) {
        this.fixedTimestepSimulation = settings.fixedTimestepSimulation;
        this.fixedTimestepRendering = settings.fixedTimestepRendering;
        this.maxFrameTime = settings.maxFrameTime;
        this.timescale = settings.defaultTimescale;
        this.physicsSubSteps = settings.physicsSubSteps;
        this.pause = false;
        this.render = true;
    }
}