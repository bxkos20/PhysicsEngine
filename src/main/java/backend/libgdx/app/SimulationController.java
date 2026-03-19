package backend.libgdx.app;

import backend.libgdx.factories.LibGDXShapeFactory;
import backend.libgdx.render.Renderer;
import backend.libgdx.render.camera.CameraController;
import backend.libgdx.render.camera.LibGDXCamera;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import engine.config.SimulationConfig;
import engine.factories.ShapeFactory;
import engine.graphics.interfaces.ICamera;
import engine.graphics.interfaces.IRenderer;
import engine.physics.ElasticCollision;
import engine.simulation.SimulationCore;
import engine.spatial.ToroidalGridPartition;
import engine.world.ToroidalBoard;
import engine.world.World;
import simulation.components.DotType;

public class SimulationController extends ApplicationAdapter {
    private SimulationCore simulationCore;
    private CameraController cameraController;
    private final int width;
    private final int height;
    private final int worldWidth;
    private final int worldHeight;

    // Simulation controls
    public float timeScale = SimulationConfig.Simulation.DEFAULT_TIMESCALE;
    public boolean render = true;
    public boolean pause = false;
    private float accumulator = 0f;

    public SimulationController(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        // Use actual screen dimensions for camera
        this.width = SimulationConfig.Display.SCREEN_WIDTH;
        this.height = SimulationConfig.Display.SCREEN_HEIGHT;
    }

    @Override
    public void create() {
        // Create LibGDX-specific implementations
        ShapeFactory shapeFactory = new LibGDXShapeFactory();
        
        // Create camera with actual screen dimensions
        ICamera camera = new LibGDXCamera(width, height);
        
        // Create renderer
        IRenderer renderer = new Renderer(width, height, camera);
        
        // Create simulation core - pure engine logic!
        simulationCore = new SimulationCore(worldWidth, worldHeight, shapeFactory, renderer);
        
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

    @Override
    public void render() {
        manageInputs();
        // Show FPS in title
        Gdx.graphics.setTitle("Simulation.Simulation - FPS: " + Gdx.graphics.getFramesPerSecond() + " - TimeScale: " + timeScale +
                " | " + (simulationCore.getWorld() != null ? simulationCore.getWorld().getProfilingInfo() : "No world"));
        if (pause) return;

        // Fixed timestep with accumulator
        float frameTime = Math.min(Gdx.graphics.getDeltaTime(), SimulationConfig.Simulation.MAX_FRAME_TIME);
        accumulator += frameTime * timeScale;

        float FIXED_TIME_STEP = SimulationConfig.Simulation.FIXED_TIMESTEP;
        while (accumulator >= FIXED_TIME_STEP) {
            simulationCore.update(FIXED_TIME_STEP);
            accumulator -= FIXED_TIME_STEP;
        }

        if (render) {
            simulationCore.render();
        }
    }

    public void manageInputs(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) timeScale *= 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) timeScale /= 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) timeScale += 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) timeScale -= 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) pause = !pause;
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) render = !render;
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) DotType.randomizeInteraction();
    }
}
