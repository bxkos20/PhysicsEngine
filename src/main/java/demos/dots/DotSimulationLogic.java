package demos.dots;

import demos.dots.settings.DotSettings;
import engine.app.implementation.ISimulationLogic;
import engine.config.Settings;
import engine.config.implementations.PerformanceSettings;
import engine.config.implementations.WorldSettings;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.RenderComponent;
import engine.ecs.components.TransformComponent;
import engine.graphics.shapes.CircleFilled;
import engine.inputs.IKeyInput;
import engine.util.ThreadLocalRandom;
import engine.world.World;
import demos.dots.components.DotComponent;
import demos.dots.types.DotType;
import demos.dots.systems.DotSystem;

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
    public void start(World world, IKeyInput keyInput, Settings settings) {
        DotSettings dotSettings = settings.get(DotSettings.class);

        dotSystem = new DotSystem(settings.get(PerformanceSettings.class).enableMultithreading,
                world.gridPartition,
                world.board,
                dotSettings
                );

        int dotTypes = dotSettings.dotTypes;
        int dotsPerType = dotSettings.totalDots / dotTypes;

        WorldSettings worldSettings = settings.get(WorldSettings.class);
        for (int i = 0; i < dotTypes; i++) {
            for (int j = 0; j < dotsPerType; j++) {
                float x = ThreadLocalRandom.nextFloat() * worldSettings.height;
                float y = ThreadLocalRandom.nextFloat() * worldSettings.width;

                GameObject dot = new GameObject();
                dot.addComponent(new ColliderComponent(dotSettings.defaultDotRadius));
                dot.addComponent(new PhysicsComponent(1, 1f, 0.5f));
                dot.addComponent(new TransformComponent(x, y));
                dot.addComponent(new DotComponent(DotType.values()[i]));
                dot.addComponent(new RenderComponent(DotType.values()[i].COLOR,
                        new CircleFilled(dotSettings.defaultDotRadius, 12)));

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
