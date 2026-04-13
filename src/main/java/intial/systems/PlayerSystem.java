package intial.systems;

import engine.ecs.GameObject;
import engine.ecs.systems.System;

public class PlayerSystem extends System {
    /**
     * Creates a system with the specified component signature.
     *
     * @param threading         Enable parallel processing
     */
    public PlayerSystem(boolean threading) {
        super(0
                , threading);
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        for (int i = 0; i < ; i++) {

        }
    }
}
