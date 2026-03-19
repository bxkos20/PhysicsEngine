package engine.simulation;

import backend.libgdx.render.primitives.Circle;
import backend.libgdx.render.primitives.CircleFilled;
import engine.config.SimulationConfig;
import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.RenderComponent;
import engine.ecs.components.TransformComponent;
import engine.graphics.interfaces.IRenderer;
import engine.util.ThreadLocalRandom;
import engine.world.World;
import simulation.components.DotComponent;
import simulation.components.DotType;

import java.util.ArrayList;
import java.util.List;

/**
 * Core simulation controller for particle physics.
 * 
 * <p>Manages world creation, entity spawning, and simulation lifecycle.
 * Uses direct OOP shape creation without intermediate systems.</p>
 * 
 * <h3>Initialization:</h3>
 * <ol>
 *   <li>Create world with dimensions</li>
 *   <li>Spawn particles of each dot type</li>
 *   <li>Assign random positions and components</li>
 * </ol>
 */
public class SimulationCore {
    /** The simulation world containing all entities */
    private World world;
    
    /** Renderer for drawing entities */
    private IRenderer renderer;
    
    /** World width in simulation units */
    private final int worldWidth;
    
    /** World height in simulation units */
    private final int worldHeight;

    /**
     * Creates a simulation core.
     * 
     * @param worldWidth  World width in simulation units
     * @param worldHeight World height in simulation units
     * @param renderer    Renderer for drawing
     */
    public SimulationCore(int worldWidth, int worldHeight, IRenderer renderer) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.renderer = renderer;
    }

    /**
     * Initializes the simulation by spawning all particles.
     * Creates particles for each dot type with random positions.
     */
    public void create() { //TODO: Separete dot related code from the engine package
        
        // Randomize interaction rules between dot types
        DotType.randomizeInteraction();

        int dotPerType = SimulationConfig.Simulation.DOTS_PER_TYPE;
        int doyTypes = SimulationConfig.Simulation.DOT_TYPES;

        for (int i = 0; i < doyTypes; i++) {
            for (int j = 0; j < dotPerType; j++) {
                float x = ThreadLocalRandom.nextFloat() * worldWidth;
                float y = ThreadLocalRandom.nextFloat() * worldHeight;

                GameObject dot = new GameObject();
                dot.addComponent(new ColliderComponent(SimulationConfig.Rendering.DEFAULT_DOT_RADIUS));
                dot.addComponent(new PhysicsComponent(1, 1f, 0.5f));
                dot.addComponent(new TransformComponent(x, y));
                dot.addComponent(new DotComponent(DotType.values()[i]));
                dot.addComponent(new RenderComponent(DotType.values()[i].COLOR,
                        new CircleFilled(SimulationConfig.Rendering.DEFAULT_DOT_RADIUS, 12)));

                if (world != null) {
                    world.addObject(dot);
                }
            }
        }
    }

    /**
     * Updates the simulation world.
     * 
     * @param deltaTime Time step in seconds
     */
    public void update(float deltaTime) {
        if (world != null) {
            world.update(deltaTime);
        }
    }


    final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    final int RENDERER_ID = ComponentRegistry.getId(RenderComponent.class);
    /**
     * Renders all entities in the world.
     * Uses the renderer's tick method for direct rendering.
     */
    public void render() {
        if (world != null && renderer != null) {
            renderer.begin();
            world.forEachObject(obj -> {
                RenderComponent renderComponent = obj.getComponent(RENDERER_ID);
                TransformComponent transformComponent = obj.getComponent(TRANSFORM_ID);
                renderer.draw(renderComponent.shape, transformComponent.getPosition().x, transformComponent.getPosition().y, renderComponent.color);
            });
            renderer.end();
        }
    }

    /**
     * Sets the simulation world.
     * 
     * @param world World instance
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Returns the simulation world.
     * 
     * @return World instance
     */
    public World getWorld() {
        return world;
    }

    public String getProfilingInfo(){
        return (world != null ? world.getProfilingInfo() : "No world") +
                " | " + (renderer != null ? renderer.getProfilingInfo() : "No renderer");
    }
}
