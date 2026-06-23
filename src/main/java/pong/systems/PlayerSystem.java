package pong.systems;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import engine.ecs.systems.System;
import engine.inputs.IKeyInput;
import pong.components.PlayerComponent;

public class PlayerSystem extends System {

    // Cached component ID for Components
    private static final int PLAYER_ID = ComponentRegistry.getId(PlayerComponent.class);
    private static final int PHYSICS_ID = ComponentRegistry.getId(PhysicsComponent.class);
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    private static final int FORCE = 1000;

    private final IKeyInput keyInput;

    /**
     * Creates a system with the specified component signature.
     *      Enable parallel processing
     */
    public PlayerSystem(IKeyInput keyInput) {
        super(false,
                PlayerComponent.class,
                TransformComponent.class,
                PhysicsComponent.class
        );
        this.keyInput = keyInput;
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        PlayerComponent playerComponent = gameObject.getComponent(PLAYER_ID);
        PhysicsComponent physicsComponent = gameObject.getComponent(PHYSICS_ID);

        if (keyInput.isPress(playerComponent.keyUp)){
            physicsComponent.addForce(0, FORCE * physicsComponent.getMass());
        } else if (keyInput.isPress(playerComponent.keyDown)){
            physicsComponent.addForce(0, -FORCE * physicsComponent.getMass());
        }

        TransformComponent transformComponent = gameObject.getComponent(TRANSFORM_ID);
        transformComponent.setPosition(playerComponent.xPosition, transformComponent.getPosition().y);

    }
}
