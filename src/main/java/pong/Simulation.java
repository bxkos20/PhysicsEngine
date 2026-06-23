package pong;

import engine.app.implementation.ISimulationLogic;
import engine.config.Settings;
import engine.config.implementations.WorldSettings;
import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.RenderComponent;
import engine.ecs.components.TransformComponent;
import engine.graphics.Color;
import engine.graphics.shapes.Circle;
import engine.inputs.IKeyInput;
import engine.inputs.Key;
import engine.world.World;
import pong.components.PlayerComponent;
import pong.systems.PlayerSystem;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Simulation logic implementation for the particle demo.
 * 
 * <p>Creates particles with different types and sets up the DotSystem
 * for particle interaction forces.</p>
 */
public class Simulation implements ISimulationLogic {

    final Color PRIMARY_COLOR = new Color(1,1,1,1); //White

    PlayerSystem playerSystem;

    GameObject player1 = new GameObject();
    GameObject player2 = new GameObject();
    GameObject ball = new GameObject();

    @Override
    public void start(World world, IKeyInput keyInput, Settings settings) {
        WorldSettings worldSettings = settings.get(WorldSettings.class);

        playerSystem = new PlayerSystem(keyInput);

        player1.addComponent(new ColliderComponent(50));
        player1.addComponent(new PlayerComponent(Key.W, Key.S, 200));
        player1.addComponent(new TransformComponent((float) 200, (float) worldSettings.height / 2));
        player1.addComponent(new PhysicsComponent(100, 1, 0.5f));
        player1.addComponent(new RenderComponent(PRIMARY_COLOR,
                //new Rect(50, 150, 10)));
                new Circle(50, 32, 10)));


        world.addEntity(player1);

        player2.addComponent(new ColliderComponent(50));
        player2.addComponent(new PlayerComponent(Key.O, Key.L, worldSettings.width - 200));
        player2.addComponent(new TransformComponent(worldSettings.width - 200, (float) worldSettings.height / 2));
        player2.addComponent(new PhysicsComponent(100, 1, 0.5f));
        player2.addComponent(new RenderComponent(PRIMARY_COLOR,
                //new Rect(50, 150, 10)));
                new Circle(50, 32, 10)));


        world.addEntity(player2);

        final int BALL_RADIUS = 20;
        ball.addComponent(new ColliderComponent(BALL_RADIUS));
        ball.addComponent(new PhysicsComponent(1, 1, 0));
        ball.addComponent(new TransformComponent((float) worldSettings.width / 2, (float) worldSettings.height / 2));
        ball.addComponent(new RenderComponent(PRIMARY_COLOR,
                new Circle(BALL_RADIUS, 32, 10)));

        PhysicsComponent physicsComponent = ball.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        physicsComponent.setVelocity(200, 0);

                world.addEntity(ball);
    }

    @Override
    public void update(float deltaTime, List<GameObject> gameObjects) {
        playerSystem.update(deltaTime, gameObjects);
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
        executionTimes.add(playerSystem.getLastExecutionTimeMs());
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
