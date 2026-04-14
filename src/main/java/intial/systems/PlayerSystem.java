package intial.systems;

import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.TransformComponent;
import engine.ecs.systems.System;
import engine.inputs.IKeyInput;
import intial.components.PlayerComponent;

public class PlayerSystem extends System {
    /**
     * Creates a system with the specified component signature.
     *
     * @param threading         Enable parallel processing
     */
    public PlayerSystem(boolean threading) {
        super(false,
                PlayerComponent.class,
                TransformComponent.class,
                ColliderComponent.class
        );
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        if (IKeyInput.)
    }
}
