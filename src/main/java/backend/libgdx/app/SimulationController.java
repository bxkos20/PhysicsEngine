package backend.libgdx.app;

import backend.libgdx.render.Renderer;
import backend.libgdx.render.camera.CameraController;
import backend.libgdx.render.camera.LibGDXCamera;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import engine.config.SimulationConfig;
import engine.graphics.interfaces.ICamera;
import engine.graphics.interfaces.IRenderer;
import engine.physics.ElasticCollision;
import engine.simulation.SimulationCore;
import engine.spatial.ToroidalGridPartition;
import engine.world.ToroidalBoard;
import engine.world.World;
import simulation.components.DotType;

/**
 * Main controller for the physics simulation, bridging LibGDX's game loop
 * with the engine-agnostic {@link SimulationCore}.
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
 * @see SimulationCore
 * @see engine.world.World
 */
public class SimulationController extends ApplicationAdapter { //TODO: A lot of things can be moved to SimulationCore
    /**
     * Core simulation logic, independent of rendering backend
     */
    private SimulationCore simulationCore;

    /**
     * Handles camera pan/zoom input
     */
    private CameraController cameraController;

    /**
     * Display dimensions in pixels
     */
    private final int width;

    /**
     * Display dimensions in pixels
     */
    private final int height;

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
     * Creates the simulation controller with specified world dimensions.
     *
     * @param worldWidth  Width of the simulation world in units
     * @param worldHeight Height of the simulation world in units
     */
    public SimulationController(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        // Use actual screen dimensions for camera
        this.width = SimulationConfig.Display.SCREEN_WIDTH;
        this.height = SimulationConfig.Display.SCREEN_HEIGHT;
    }

    /**
     * Initializes all simulation components.
     * Called once by LibGDX when the application starts.
     *
     * <p>Creates and wires together:</p>
     * <ul>
     *   <li>Camera with viewport configuration</li>
     *   <li>Renderer with shader setup</li>
     *   <li>World with board, collision, and spatial partitioning</li>
     * </ul>
     */
    @Override
    public void create() {
        // Create camera with actual screen dimensions
        ICamera camera = new LibGDXCamera(width, height);

        camera.setPosition(worldWidth / 2f, worldHeight / 2f);

        // Create renderer
        IRenderer renderer = new Renderer(camera);

        // Create simulation core - pure engine logic!
        simulationCore = new SimulationCore(worldWidth, worldHeight, renderer);

        // Setup world components
        ToroidalBoard board = new ToroidalBoard(
                SimulationConfig.World.WORLD_WIDTH,
                SimulationConfig.World.WORLD_HEIGHT
        );
        ElasticCollision collision = new ElasticCollision();
        ToroidalGridPartition gridPartition = new ToroidalGridPartition(
                SimulationConfig.World.WORLD_WIDTH,
                SimulationConfig.World.WORLD_HEIGHT,
                SimulationConfig.Performance.GRID_CELL_SIZE
        );
        World world = new World(board, collision, gridPartition);

        // Inject world into simulation core
        simulationCore.setWorld(world);

        // Create simulation
        simulationCore.create();

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
                " | " + simulationCore.getProfilingInfo());

        float frameTime = Math.min(Gdx.graphics.getDeltaTime(), SimulationConfig.Simulation.MAX_FRAME_TIME);
        renderAccumulator += frameTime;
        if (render) {
            float RENDERER_FIXED_TIME_STEP = SimulationConfig.Simulation.FIXED_TIMESTEP_RENDERING;
            while (renderAccumulator >= RENDERER_FIXED_TIME_STEP) {
                simulationCore.render();
                renderAccumulator -= RENDERER_FIXED_TIME_STEP;
            }
        }

        if (pause) return;

        // Fixed timestep with accumulator
        simulationAccumulator += frameTime * timeScale;

        float SIMULATION_FIXED_TIME_STEP = SimulationConfig.Simulation.FIXED_TIMESTEP_SIMULATION;
        while (simulationAccumulator >= SIMULATION_FIXED_TIME_STEP) {
            simulationCore.update(SIMULATION_FIXED_TIME_STEP);
            simulationAccumulator -= SIMULATION_FIXED_TIME_STEP;
        }

    }

    /**
     * Processes keyboard input for simulation control.
     * Handles time scale adjustment, pause, render toggle, and interaction randomization.
     */
    public void manageInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) timeScale *= 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) timeScale /= 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) timeScale += 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) timeScale -= 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) pause = !pause;
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) render = !render;
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) DotType.randomizeInteraction();
    }
}
