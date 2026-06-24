package backend.libgdx.app;

import backend.libgdx.inputs.LibGDXKeyInputs;
import backend.libgdx.render.Renderer;
import backend.libgdx.render.camera.CameraController;
import backend.libgdx.render.camera.LibGDXCamera;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import engine.app.EngineSimulation;
import engine.app.implementation.ISimulationLogic;
import engine.config.Settings;
import engine.config.implementations.RenderingSettings;
import engine.config.implementations.ScreenSettings;
import engine.config.implementations.SimulationSettings;
import engine.config.implementations.WorldSettings;
import engine.graphics.interfaces.ICamera;
import engine.graphics.interfaces.IRenderer;
import engine.inputs.IKeyInput;

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
     * Specific simulation logic, independent of the engine and backend
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
     * Handles rendering
     */
    private IRenderer renderer;

    /**
     * Handles key inputs by the user
     */
    private IKeyInput keyInput;

    /**
     * Screen dimensions in pixels
     */
    private final int displayWidht;

    /**
     * Screen dimensions in pixels
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
    public float timeScale;

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

    private final float maxFrameRate;
    private final float RENDERER_FIXED_TIME_STEP;
    private final float SIMULATION_FIXED_TIME_STEP;

    private final Settings settings;

    /**
     * Creates the simulation controller with a pre-configured simulation core.
     */
    public BackendSimulation(ISimulationLogic simulationLogic, Settings settings) {
        this.settings = settings;

        // Extract dimensions from the simulation core
        WorldSettings worldSettings = settings.get(WorldSettings.class);
        this.worldWidth = worldSettings.width;
        this.worldHeight = worldSettings.height;

        // Use actual screen dimensions for camera
        ScreenSettings screenSettings = settings.get(ScreenSettings.class);
        this.displayWidht = screenSettings.width;
        this.displayHeight = screenSettings.height;

        SimulationSettings simulationSettings = settings.get(SimulationSettings.class);
        this.timeScale = simulationSettings.defaultTimescale;
        this.maxFrameRate = simulationSettings.maxFrameTime;
        this.RENDERER_FIXED_TIME_STEP = simulationSettings.fixedTimestepRendering;
        this.SIMULATION_FIXED_TIME_STEP = simulationSettings.fixedTimestepSimulation;

        this.simulationLogic = simulationLogic;
    }

    @Override
    public void create(){
        //Create camera
        ICamera camera = new LibGDXCamera(displayWidht, displayHeight);
        camera.setPosition(worldWidth / 2f, worldHeight / 2f);

        // Create renderer and keyInput
        renderer = new Renderer(camera, settings.get(RenderingSettings.class));
        keyInput = new LibGDXKeyInputs();

        //Create engineSimulation
        this.engineSimulation = new EngineSimulation(renderer, simulationLogic , keyInput, settings);

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
        // Show FPS in title
        Gdx.graphics.setTitle("Simulation.Simulation - FPS: " + Gdx.graphics.getFramesPerSecond() + " - TimeScale: " + timeScale +
                " | " + engineSimulation.getProfilingInfo());

        float frameTime = Math.min(Gdx.graphics.getDeltaTime(), maxFrameRate);

        renderAccumulator += frameTime;
        if (render) {
            while (renderAccumulator >= RENDERER_FIXED_TIME_STEP) {
                engineSimulation.render(RENDERER_FIXED_TIME_STEP);
                renderAccumulator -= RENDERER_FIXED_TIME_STEP;
            }
        }

        if (pause) return;

        // Fixed timestep with accumulator
        simulationAccumulator += frameTime * timeScale;

        while (simulationAccumulator >= SIMULATION_FIXED_TIME_STEP) {
            engineSimulation.update(SIMULATION_FIXED_TIME_STEP);
            simulationAccumulator -= SIMULATION_FIXED_TIME_STEP;
        }

    }
}