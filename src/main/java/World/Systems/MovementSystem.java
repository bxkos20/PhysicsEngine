package World.Systems;

import GameObject.Components.ComponentRegistry;
import GameObject.Components.Core.PhysicsComponent;
import GameObject.Components.Core.TransformComponent;
import GameObject.GameObject;
import World.Board.Board;

public class MovementSystem extends System{
    private Board board;
    public MovementSystem(Board board) {
        super(ComponentRegistry.getBit(TransformComponent.class)
                | ComponentRegistry.getBit(PhysicsComponent.class));
        this.board = board;
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        PhysicsComponent physics = gameObject.getComponent(PhysicsComponent.class);
        TransformComponent transform = gameObject.getComponent(TransformComponent.class);

        physics.getVelocity().mulAdd(physics.getVelocity(), physics.getFriction() * -1);
        physics.getVelocity().mulAdd(physics.getSumForces(), dt / physics.getMass());

        transform.getPosition().add(physics.getVelocity().x * dt, physics.getVelocity().y * dt);
        board.enforceBounds(transform);

        physics.getSumForces().set(0, 0);
    }
}
