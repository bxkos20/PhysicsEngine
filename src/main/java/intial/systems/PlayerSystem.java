package intial.systems;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import engine.ecs.systems.System;
import engine.inputs.IKeyInput;
import engine.math.Vector2;
import intial.components.PlayerComponent;

public class PlayerSystem extends System {

    /** Cached component ID for TransformComponent */
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);

    /** Cached component ID for PlayerComponent */
    private static final int PLAYER_ID = ComponentRegistry.getId(PlayerComponent.class);

    private final IKeyInput keyInput;

    /**
     * Creates a system with the specified component signature.
     */
    public PlayerSystem(IKeyInput keyInput) {
        super(false,
                PlayerComponent.class,
                TransformComponent.class
        );
        this.keyInput = keyInput;
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        PlayerComponent playerComponent = gameObject.getComponent(PLAYER_ID);
        TransformComponent transformComponent = gameObject.getComponent(TRANSFORM_ID);

        Vector2 pos = transformComponent.getPosition();

        if (keyInput.isPress(playerComponent.keyUp)){
            transformComponent.setPosition(pos.add(0,10));
        } else if (keyInput.isPress(playerComponent.keyDown)){
            transformComponent.setPosition(pos.add(0,-10));
        }

    }
}
