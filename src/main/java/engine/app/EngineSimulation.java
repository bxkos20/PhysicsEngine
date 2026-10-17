package engine.app;

import java.util.ArrayList;
import java.util.List;

import engine.app.implementation.ISimulationLogic;
import engine.config.parameters.Parameters;
import engine.config.settings.Settings;
import engine.config.settings.implementations.PerformanceSettings;
import engine.config.settings.implementations.WorldSettings;
import engine.ecs.GameObject;
import engine.ecs.systems.SystemECS;
import engine.ecs.systems.implementations.CollisionSystem;
import engine.ecs.systems.implementations.MovementSystem;
import engine.ecs.systems.implementations.RendererSystem;
import engine.graphics.interfaces.IRenderer;
import engine.inputs.IKeyInput;
import engine.util.Profiler;
import engine.world.World;
import engine.world.WorldFactory;
import engine.world.board.Board;

/**
 * Core simulation controller for particle physics.
 *
 * <p>Manages world creation, entity spawning, and simulation lifecycle.
 * Uses pluggable simulation logic via ISimulationLogic interface.</p>
 *
 * <h3>Initialization:</h3>
 * <ol>
 *   <li>Create world with dimensions</li>
 *   <li>Delegate entity creation to simulation logic</li>
 *   <li>Setup custom systems if provided</li>
 * </ol>
 */
public class EngineSimulation {

    private final ISimulationLogic simulationLogic;
    private final World world;
    private final RendererSystem rendererSystem;
    private final ArrayList<SystemECS> systems = new ArrayList<>();

    private final Profiler worldUpdateProfiler;
    private final Profiler simLogicUpdateProfiler;

    /**
     * Creates a simulation core.
     *
     * @param renderer Renderer for drawing
     */
    public EngineSimulation(IRenderer renderer, ISimulationLogic simulationLogic, IKeyInput keyInput, Settings settings, Parameters parameters) {
        if (renderer == null) throw new IllegalArgumentException("Renderer cannot be null");

        this.simulationLogic = simulationLogic;
        this.rendererSystem = new RendererSystem(renderer);

        // Inicializamos los nuevos profilers
        this.worldUpdateProfiler = new Profiler(60);
        this.simLogicUpdateProfiler = new Profiler(60);

        this.world = WorldFactory.createWorldWhitSettings(settings.get(WorldSettings.class));

        boolean multithreading = settings.get(PerformanceSettings.class).enableMultithreading;
        Board board = world.board;

        systems.add(new MovementSystem(multithreading, board));
        systems.add(new CollisionSystem(multithreading, world.gridPartition, board, world.collision));

        simulationLogic.start(world, keyInput, settings, parameters);
    }

    /**
     * Updates the simulation world.
     *
     * @param dt Time step in seconds
     */
    public void update(float dt) {
        worldUpdateProfiler.startExecution();
        world.update(dt);
        worldUpdateProfiler.endExecution();

        List<GameObject> gameObjects = world.getGameObjects();

        for (int i = 0; i < systems.size(); i++) {
            systems.get(i).update(dt, gameObjects);
        }

        simLogicUpdateProfiler.startExecution();
        simulationLogic.update(dt, gameObjects);
        simLogicUpdateProfiler.endExecution();
    }


    /**
     * Renders all entities in the world.
     * Uses the renderer's tick method for direct rendering.
     */
    public void render(float dt) {
        rendererSystem.update(dt, world.getGameObjects());
    }

    /**
     * Returns the simulation world.
     *
     * @return World instance
     */
    public World getWorld() {
        return world;
    }

    /**
     * Returns profiling information for all registered systems and main loop components.
     */
    public String getProfilingInfo() {
        StringBuilder info = new StringBuilder();

        // Sistemas de lógica (Movement, Collision)
        for (SystemECS system : systems) {
            info.append(String.format("%s: %.2fms | ",
                    system.getName(),
                    system.getLastExecutionTimeMs()));
        }

        // Sistema de renderizado
        if (rendererSystem != null) {
            info.append(String.format("%s: %.2fms | ",
                    rendererSystem.getName(),
                    rendererSystem.getLastExecutionTimeMs()));
        }

        // World Update y SimLogic Update usando los nuevos profilers
        info.append(String.format("WorldUpdate: %.2fms | ", worldUpdateProfiler.getExecutionAverageTimeMs()));
        info.append(String.format("SimLogicUpdate: %.2fms | ", simLogicUpdateProfiler.getExecutionAverageTimeMs()));

        // Información de profiling interna de la simulación (si la tuviera)
        String simLogicInfo = simulationLogic.getProfilingInfo();
        if (simLogicInfo != null && !simLogicInfo.isEmpty()) {
            info.append(simLogicInfo).append(" | ");
        }

        // Limpieza final
        if (info.length() > 3) {
            info.setLength(info.length() - 3);
        }

        return info.toString();
    }
}