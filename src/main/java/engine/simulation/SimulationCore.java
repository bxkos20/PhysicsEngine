package engine.simulation;

import engine.config.SimulationConfig;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.RenderData;
import engine.ecs.components.TransformComponent;
import engine.ecs.systems.RenderSystem;
import engine.factories.ShapeFactory;
import engine.graphics.interfaces.IRenderer;
import engine.util.ThreadLocalRandom;
import engine.world.World;
import simulation.components.DotComponent;
import simulation.components.DotType;

public class SimulationCore {
    private World world;
    private IRenderer renderer;
    private RenderSystem renderSystem;
    private final int worldWidth;
    private final int worldHeight;
    
    private final int totalDots = SimulationConfig.Simulation.TOTAL_DOTS;

    public SimulationCore(int worldWidth, int worldHeight, ShapeFactory shapeFactory, IRenderer renderer) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.renderer = renderer;
        this.renderSystem = new RenderSystem(renderer, shapeFactory); // Pass factory instance
    }

    public void create() {
        // Setup world - pure engine logic, no LibGDX!
        // Note: Board, collision, and grid partition setup should be injected
        // For now, we'll create them here but this could be improved
        
        // Create dots using factory - no backend dependencies!
        DotType.randomizeInteraction();

        int dotPerType = totalDots / DotType.values().length;

        for (int i = 0; i < DotType.values().length; i++) {
            for (int j = 0; j < dotPerType; j++) {
                float x = ThreadLocalRandom.nextFloat() * worldWidth;
                float y = ThreadLocalRandom.nextFloat() * worldHeight;

                GameObject dot = new GameObject();
                dot.addComponent(new ColliderComponent(5));
                dot.addComponent(new PhysicsComponent(1, 1f, 0.5f));
                dot.addComponent(new TransformComponent(x, y));
                dot.addComponent(new DotComponent(DotType.values()[i]));
                
                // Use proper ECS RenderData component
                String shapeType = "circle";
                float[] shapeParams = {
                    SimulationConfig.Rendering.DEFAULT_DOT_RADIUS,
                    SimulationConfig.Rendering.DEFAULT_CIRCLE_SEGMENTS,
                    SimulationConfig.Rendering.DEFAULT_LINE_WIDTH
                }; // radius, quality, lineWidth
                dot.addComponent(new RenderData(DotType.values()[i].COLOR, shapeType, shapeParams));

                // World should be injected, but for now we'll store reference
                if (world != null) {
                    world.addObject(dot);
                }
            }
        }
    }

    public void update(float deltaTime) {
        if (world != null) {
            world.update(deltaTime);
        }
    }

    public void render() {
        if (world != null && renderer != null) {
            renderSystem.render(world.getGameObjects());
        }
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
