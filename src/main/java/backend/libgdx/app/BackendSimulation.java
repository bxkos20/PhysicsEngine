package backend.libgdx.app;

import backend.libgdx.inputs.LibGDXKeyInputs;
import backend.libgdx.render.Renderer;
import backend.libgdx.render.camera.CameraController;
import backend.libgdx.render.camera.LibGDXCamera;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import engine.app.implementation.ISimulationLogic;
import engine.config.SimulationConfig;
import engine.graphics.interfaces.ICamera;
import engine.graphics.interfaces.IRenderer;
import engine.app.EngineSimulation;
import demos.dots.components.DotType;
import engine.inputs.IKeyInput;
import engine.inputs.Key;

/**
 * Main controller for the physics simulation, bridging LibGDX's game loop
 * with the engine-agnostic {@link EngineSimulation}.
 *
 * <p>Implements a fixed timestep game loop with accumulator pattern to ensure
 * deterministic physics regardless of frame rate variations.</p>
 *
 * <h3>Controls:</h3>
 * <ul>
 *   <li>UP/DOWN - Multiply/divide time scale by 2</li>
 *   <li>LEFT/RIGHT - Increment/decrement time scale by 1</li>
 *   <li>P - Toggle pause</li>
 *   <li>R - Toggle rendering</li>
 *   <li>E - Randomize dot interactions</li>
 * </ul>
 *
 * @see EngineSimulation
 * @see engine.world.World
 */
public class BackendSimulation extends ApplicationAdapter {

    /**
     * TODO
     */
    private ISimulationLogic simulationLogic;

    /**
     * Core simulation logic, independent of rendering backend
     */
    private EngineSimulation engineSimulation;

    /**
     * Handles camera pan/zoom input
     */
    private CameraController cameraController;

    /**
     * Display dimensions in pixels
     */
    private final int displayWidht;

    /**
     * Display dimensions in pixels
     */
    private final int displayHeight;

    /**
     * World dimensions in simulation units
     */
    private final int worldWidth;

    /**
     * World dimensions in simulation units
     */
    private final int worldHeight;

    /**
     * Time multiplier for simulation speed control (default: 1.0)
     */
    public float timeScale = SimulationConfig.Simulation.DEFAULT_TIMESCALE;

    /**
     * Toggle rendering on/off for performance testing
     */
    public boolean render = true;

    /**
     * Toggle simulation update on/off
     */
    public boolean pause = false;

    /**
     * Accumulated time for fixed timestep physics
     */
    private float simulationAccumulator = 0f;

    /**
     * Accumulated time for fixed timestep rendering
     */
    private float renderAccumulator = 0f;

    /**
     * Creates the simulation controller with a pre-configured simulation core.
     */
    public BackendSimulation(ISimulationLogic simulationLogic) {
        // Extract dimensions from the simulation core
        this.worldWidth = SimulationConfig.World.WORLD_WIDTH;
        this.worldHeight = SimulationConfig.World.WORLD_HEIGHT;

        // Use actual screen dimensions for camera
        this.displayWidht = SimulationConfig.Display.SCREEN_WIDTH;
        this.displayHeight = SimulationConfig.Display.SCREEN_HEIGHT;

        this.simulationLogic = simulationLogic;

    }

    @Override
    public void create(){
        //Create camera
        ICamera camera = new LibGDXCamera(displayWidht, displayHeight);
        camera.setPosition(worldWidth / 2f, worldHeight / 2f);

        // Create renderer
        IRenderer renderer = new Renderer(camera);
        IKeyInput keyInput = new LibGDXKeyInputs();

        //Create engineSimulation
        this.engineSimulation = new EngineSimulation(renderer, simulationLogic , keyInput);

        // Setup camera controls
        cameraController = new CameraController(camera);
        Gdx.input.setInputProcessor(cameraController);
    }

    /**
     * Main game loop called by LibGDX every frame.
     *
     * <p>Implements fixed timestep pattern:</p>
     * <ol>
     *   <li>Process user input</li>
     *   <li>Update FPS/time scale display</li>
     *   <li>Accumulate frame time</li>
     *   <li>Run physics updates at fixed intervals</li>
     *   <li>Render current state</li>
     * </ol>
     */
    @Override
    public void render() {
        manageInputs();
        // Show FPS in title
        Gdx.graphics.setTitle("Simulation.Simulation - FPS: " + Gdx.graphics.getFramesPerSecond() + " - TimeScale: " + timeScale +
                " | " + engineSimulation.getProfilingInfo());

        float frameTime = Math.min(Gdx.graphics.getDeltaTime(), SimulationConfig.Simulation.MAX_FRAME_TIME);

        renderAccumulator += frameTime;
        if (render) {
            float RENDERER_FIXED_TIME_STEP = SimulationConfig.Simulation.FIXED_TIMESTEP_RENDERING;
            while (renderAccumulator >= RENDERER_FIXED_TIME_STEP) {
                engineSimulation.render();
                renderAccumulator -= RENDERER_FIXED_TIME_STEP;
            }
        }

        if (pause) return;

        // Fixed timestep with accumulator
        simulationAccumulator += frameTime * timeScale;

        float SIMULATION_FIXED_TIME_STEP = SimulationConfig.Simulation.FIXED_TIMESTEP_SIMULATION;
        while (simulationAccumulator >= SIMULATION_FIXED_TIME_STEP) {
            engineSimulation.update(SIMULATION_FIXED_TIME_STEP);
            simulationAccumulator -= SIMULATION_FIXED_TIME_STEP;
        }

    }

    /**
     * Processes keyboard input for simulation control.
     * Handles time scale adjustment, pause, render toggle, and interaction randomization.
     */
    public void manageInputs() { //TODO: A inputClass
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) timeScale *= 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) timeScale /= 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) timeScale += 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) timeScale -= 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) pause = !pause;
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) render = !render;
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) DotType.randomizeInteraction();
    }
}
