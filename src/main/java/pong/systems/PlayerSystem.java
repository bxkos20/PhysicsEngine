package pong.systems;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.systems.System;
import engine.inputs.IKeyInput;
import pong.components.PlayerComponent;

public class PlayerSystem extends System {

    // Cached component ID for Components
    private static final int PLAYER_ID = ComponentRegistry.getId(PlayerComponent.class);
    private static final int PHYSICS_ID = ComponentRegistry.getId(PhysicsComponent.class);

    private final IKeyInput keyInput;

    /**
     * Creates a system with the specified component signature.
     *      Enable parallel processing
     */
    public PlayerSystem(IKeyInput keyInput) {
        super(false,
                PlayerComponent.class,
                PhysicsComponent.class
        );
        this.keyInput = keyInput;
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        PlayerComponent playerComponent = gameObject.getComponent(PLAYER_ID);
        PhysicsComponent physicsComponent = gameObject.getComponent(PHYSICS_ID);

        // Reset vertical velocity to prevent continuous movement
        physicsComponent.getVelocity().y = 0;

        if (keyInput.isPress(playerComponent.keyUp)){
            physicsComponent.getVelocity().y = playerComponent.speed;
        } else if (keyInput.isPress(playerComponent.keyDown)){
            physicsComponent.getVelocity().y = -playerComponent.speed;
        }
    }
}