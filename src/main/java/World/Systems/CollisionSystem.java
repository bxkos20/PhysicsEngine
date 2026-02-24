package World.Systems;

import GameObject.Components.Core.ColliderComponent;
import GameObject.Components.Core.TransformComponent;
import GameObject.GameObject;
import GameObject.Components.ComponentRegistry;
import World.Board.Board;
import World.Board.Grid.GridPartition;
import World.Collision.Collision;

public class CollisionSystem extends System{
    private GridPartition gridPartition;
    private Board board;
    private Collision collision;

    public CollisionSystem(GridPartition gridPartition, Board board, Collision collision) {
        super(ComponentRegistry.getBit(TransformComponent.class) |
                ComponentRegistry.getBit(ColliderComponent.class), false);
        this.gridPartition = gridPartition;
        this.board = board;
        this.collision = collision;
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        TransformComponent transform = gameObject.getComponent(TransformComponent.class);

        for (GameObject other : gridPartition.getNearby(transform, 1)){
            if (gameObject == other) continue;
            if (!other.checkSignature(ComponentRegistry.getBit(TransformComponent.class) |
                    ComponentRegistry.getBit(ColliderComponent.class))) continue;

            collision.solveCollision(gameObject, other, board);
        }
    }
}
