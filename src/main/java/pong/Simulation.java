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
import pong.settings.PongSettings;
import pong.systems.PlayerSystem;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Simulation logic implementation for a game of Pong.
 */
public class Simulation implements ISimulationLogic {

    final Color PRIMARY_COLOR = new Color(1, 1, 1, 1); //White

    PlayerSystem playerSystem;

    GameObject player1 = new GameObject();
    GameObject player2 = new GameObject();
    GameObject ball = new GameObject();

    @Override
    public void start(World world, IKeyInput keyInput, Settings settings) {
        WorldSettings worldSettings = settings.get(WorldSettings.class);
        PongSettings pongSettings = settings.get(PongSettings.class);

        if (worldSettings == null || pongSettings == null) {
            throw new IllegalStateException("Pong simulation requires both WorldSettings and PongSettings.");
        }

        playerSystem = new PlayerSystem(keyInput);

        // Player 1
        player1.addComponent(new ColliderComponent(pongSettings.playerRadius));
        player1.addComponent(new PlayerComponent(Key.W, Key.S, pongSettings.playerSpeed));
        player1.addComponent(new TransformComponent(pongSettings.playerXOffset, (float) worldSettings.height / 2));
        player1.addComponent(new PhysicsComponent(pongSettings.playerMass, 1, 0.5f));
        player1.addComponent(new RenderComponent(PRIMARY_COLOR,
                new Circle(pongSettings.playerRadius, pongSettings.circleSegments, pongSettings.circleLineWidth)));
        world.addEntity(player1);

        // Player 2
        player2.addComponent(new ColliderComponent(pongSettings.playerRadius));
        player2.addComponent(new PlayerComponent(Key.O, Key.L, pongSettings.playerSpeed));
        player2.addComponent(new TransformComponent(worldSettings.width - pongSettings.playerXOffset, (float) worldSettings.height / 2));
        player2.addComponent(new PhysicsComponent(pongSettings.playerMass, 1, 0.5f));
        player2.addComponent(new RenderComponent(PRIMARY_COLOR,
                new Circle(pongSettings.playerRadius, pongSettings.circleSegments, pongSettings.circleLineWidth)));
        world.addEntity(player2);

        // Ball
        ball.addComponent(new ColliderComponent(pongSettings.ballRadius));
        ball.addComponent(new PhysicsComponent(pongSettings.ballMass, 1, 0));
        ball.addComponent(new TransformComponent((float) worldSettings.width / 2, (float) worldSettings.height / 2));
        ball.addComponent(new RenderComponent(PRIMARY_COLOR,
                new Circle(pongSettings.ballRadius, pongSettings.circleSegments, pongSettings.circleLineWidth)));

        PhysicsComponent physicsComponent = ball.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        physicsComponent.setVelocity(pongSettings.ballInitialSpeedX, pongSettings.ballInitialSpeedY);
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