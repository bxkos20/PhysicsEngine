package engine.app;

import engine.app.implementation.ISimulationLogic;
import engine.config.SimulationConfig;
import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.RenderComponent;
import engine.ecs.components.TransformComponent;
import engine.graphics.interfaces.IRenderer;
import engine.inputs.IKeyInput;
import engine.physics.ElasticCollision;
import engine.world.board.ToroidalBoard;
import engine.world.World;
import engine.world.spatial.ToroidalGridPartition;

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

    /**
     * TODO
     */
    private final ISimulationLogic simulationLogic;

    /**
     * The simulation world containing all entities
     */
    private final World world;

    /**
     * Renderer for drawing entities
     */
    private final IRenderer renderer;

    /**
     * World width in simulation units
     */
    private final int worldWidth;

    /**
     * World height in simulation units
     */
    private final int worldHeight;

    /**
     * Creates a simulation core.
     *
     * @param renderer Renderer for drawing
     */
    public EngineSimulation(IRenderer renderer, ISimulationLogic simulationLogic, IKeyInput keyInput) {
        if (renderer == null) throw new IllegalArgumentException("Renderer cannot be null");

        this.worldWidth = SimulationConfig.World.WORLD_WIDTH;
        this.worldHeight = SimulationConfig.World.WORLD_HEIGHT;
        this.renderer = renderer;
        this.simulationLogic = simulationLogic;

        this.world = new World( //Idk if this should be created here
                new ToroidalBoard(worldWidth, worldHeight),
                new ElasticCollision(),
                new ToroidalGridPartition(worldWidth, worldHeight, SimulationConfig.Performance.GRID_CELL_SIZE)
        );

        simulationLogic.start(world, keyInput);
    }


    /**
     * Updates the simulation world.
     *
     * @param deltaTime Time step in seconds
     */
    public void update(float deltaTime) {
        world.update(deltaTime);
        simulationLogic.update(deltaTime, world.getGameObjects());
    }


    final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    final int RENDERER_ID = ComponentRegistry.getId(RenderComponent.class);

    /**
     * Renders all entities in the world.
     * Uses the renderer's tick method for direct rendering.
     */
    public void render() { //TODO: Implement a render system
        renderer.begin();
        world.forEachObject(obj -> {
            RenderComponent renderComponent = obj.getComponent(RENDERER_ID);
            TransformComponent transformComponent = obj.getComponent(TRANSFORM_ID);
            renderer.draw(renderComponent.shape, transformComponent.getPosition().x, transformComponent.getPosition().y, renderComponent.color);
        });
        renderer.end();
    }

    /**
     * Adds an entity to the simulation world.
     *
     * @param entity Entity to add
     */
    public void addEntity(GameObject entity) {
        world.addEntity(entity);
    }

    /**
     * Returns the simulation world.
     *
     * @return World instance
     */
    public World getWorld() {
        return world;
    }

    public String getProfilingInfo() {
        return (world.getProfilingInfo() + " | " + renderer.getProfilingInfo()) + " | " + simulationLogic.getProfilingInfo();
    }
}
