package backend.libgdx.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import backend.libgdx.inputs.LibGDXKeyInputs;
import backend.libgdx.render.Renderer;
import backend.libgdx.render.camera.CameraController;
import backend.libgdx.render.camera.LibGDXCamera;
import engine.app.EngineRunner;
import engine.app.EngineSimulation;
import engine.app.implementation.ISimulationLogic;
import engine.config.parameters.Parameters;
import engine.config.settings.Settings;
import engine.config.settings.implementations.RenderingSettings;
import engine.config.settings.implementations.ScreenSettings;
import engine.config.settings.implementations.WorldSettings;
import engine.graphics.interfaces.ICamera;
import engine.graphics.interfaces.IRenderer;
import engine.inputs.IKeyInput;

/**
 * Adapts LibGDX's application lifecycle and rendering/input services to the engine.
 *
 * @see EngineSimulation
 * @see engine.world.World
 */
public class BackendSimulation extends ApplicationAdapter {

    /**
     * Specific simulation logic, independent of the engine and backend
     */
    private final ISimulationLogic simulationLogic;

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

    private EngineRunner engineRunner;

    private final Settings settings;

    private final Parameters parameters;

    /**
     * Creates the simulation controller with a pre-configured simulation core.
     */
    public BackendSimulation(ISimulationLogic simulationLogic, Settings settings, Parameters parameters) {
        this.settings = settings;
        this.parameters = parameters;

        // Extract dimensions from the simulation core
        WorldSettings worldSettings = settings.get(WorldSettings.class);
        this.worldWidth = worldSettings.width;
        this.worldHeight = worldSettings.height;

        // Use actual screen dimensions for camera
        ScreenSettings screenSettings = settings.get(ScreenSettings.class);
        this.displayWidht = screenSettings.width;
        this.displayHeight = screenSettings.height;

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
        this.engineSimulation = new EngineSimulation(renderer, simulationLogic , keyInput, settings, parameters);

        // Setup camera controls
        cameraController = new CameraController(camera);
        Gdx.input.setInputProcessor(cameraController);

        engineRunner = new EngineRunner(engineSimulation, parameters);
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
        Gdx.graphics.setTitle("Simulation.Simulation - FPS: " + Gdx.graphics.getFramesPerSecond() +
                " | " + engineSimulation.getProfilingInfo());

        engineRunner.run(Gdx.graphics.getDeltaTime());
    }
}