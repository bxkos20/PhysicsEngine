package demo;

import engine.app.EngineSimulation;
import engine.app.implementation.ISimulationLogic;
import engine.config.SimulationConfig;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.RenderComponent;
import engine.ecs.components.TransformComponent;
import engine.graphics.shapes.CircleFilled;
import engine.util.ThreadLocalRandom;
import engine.world.Board;
import engine.world.World;
import engine.world.spatial.GridPartition;
import demo.components.DotComponent;
import demo.components.DotType;
import demo.systems.DotSystem;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Simulation logic implementation for the particle demo.
 * 
 * <p>Creates particles with different types and sets up the DotSystem
 * for particle interaction forces.</p>
 */
public class DotSimulationLogic implements ISimulationLogic {

    private DotSystem dotSystem;
    
    @Override
    public void start(World world) {
        dotSystem = new DotSystem(SimulationConfig.Performance.ENABLE_MULTITHREADING,
                world.gridPartition,
                world.board);
        DotType.randomizeInteraction();

        int dotsPerType = SimulationConfig.Simulation.DOTS_PER_TYPE;
        int dotTypes = SimulationConfig.Simulation.DOT_TYPES;

        for (int i = 0; i < dotTypes; i++) {
            for (int j = 0; j < dotsPerType; j++) {
                float x = ThreadLocalRandom.nextFloat() * SimulationConfig.World.WORLD_WIDTH;
                float y = ThreadLocalRandom.nextFloat() * SimulationConfig.World.WORLD_HEIGHT;

                GameObject dot = new GameObject();
                dot.addComponent(new ColliderComponent(SimulationConfig.Rendering.DEFAULT_DOT_RADIUS));
                dot.addComponent(new PhysicsComponent(1, 1f, 0.5f));
                dot.addComponent(new TransformComponent(x, y));
                dot.addComponent(new DotComponent(DotType.values()[i]));
                dot.addComponent(new RenderComponent(DotType.values()[i].COLOR,
                        new CircleFilled(SimulationConfig.Rendering.DEFAULT_DOT_RADIUS, 12)));

                world.addEntity(dot);
            }
        }
    }

    @Override
    public void update(float deltaTime, List<GameObject> gameObjects) {
        dotSystem.update(deltaTime, gameObjects);
    }

    /**
     * Returns profiling information for the last render call.
     *
     * @return Formatted string with render time in milliseconds
     */
    public String getProfilingInfo() {
        return String.format("Simulation: %.2fms", getLastExecutionTimeMs());
    }

    /**
     * Returns the last execution time for profiling.
     *
     * @return Execution time in milliseconds
     */
    private final int EXECUTION_AVERAGING_SIZE = 60;
    private final Queue<Float> executionTimes = new LinkedList<>();

    public float getLastExecutionTimeMs() {
        executionTimes.add(dotSystem.getLastExecutionTimeMs());
        if (executionTimes.size() > EXECUTION_AVERAGING_SIZE) {
            executionTimes.poll();
        }
        float sum = 0;
        for (float time : executionTimes) {
            sum += time;
        }
        return sum / executionTimes.size();
    }
}
