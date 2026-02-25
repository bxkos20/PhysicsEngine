package World.Systems;

import GameObject.Components.ComponentRegistry;
import GameObject.Components.Core.PhysicsComponent;
import GameObject.Components.Core.TransformComponent;
import GameObject.GameObject;
import World.Board.Board;

public class MovementSystem extends System {
    private Board board;

    public MovementSystem(boolean threading, Board board) {
        super(ComponentRegistry.getBit(TransformComponent.class)
                | ComponentRegistry.getBit(PhysicsComponent.class), threading);
        this.board = board;
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        PhysicsComponent physics = gameObject.getComponent(PhysicsComponent.class);
        TransformComponent transform = gameObject.getComponent(TransformComponent.class);

        // Actualizar Velocidad (v = v + (F / m) * dt)
        float invMass = (physics.getMass() <= 0) ? 0 : (1f / physics.getMass());
        physics.getVelocity().mulAdd(physics.getSumForces(), invMass * dt);

        // 3. Aplicar Fricción/Damping (v = v * (1 - friction * dt))
        // Esto reduce la velocidad gradualmente sin riesgo de invertir el sentido
        float frictionFactor = Math.max(0, 1 - physics.getFriction() * dt);
        physics.getVelocity().scl(frictionFactor);

        // 4. Actualizar Posición (p = p + v * dt)
        transform.getPosition().mulAdd(physics.getVelocity(), dt);

        board.enforceBounds(transform);

        physics.getSumForces().set(0, 0);
    }
}
