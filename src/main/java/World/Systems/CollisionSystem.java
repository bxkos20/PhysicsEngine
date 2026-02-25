package World.Systems;

import GameObject.Components.Core.ColliderComponent;
import GameObject.Components.Core.TransformComponent;
import GameObject.GameObject;
import GameObject.Components.ComponentRegistry;
import World.Board.Board;
import World.Board.Grid.GridPartition;
import World.Collision.Collision;

public class CollisionSystem extends System{
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    private static final int COLLIDER_ID = ComponentRegistry.getId(ColliderComponent.class);

    private final GridPartition gridPartition;
    private final Board board;
    private final Collision collision;

    public CollisionSystem(GridPartition gridPartition, Board board, Collision collision) {
        super(ComponentRegistry.idToBit(TRANSFORM_ID) |
                ComponentRegistry.idToBit(COLLIDER_ID),
                false
        );
        this.gridPartition = gridPartition;
        this.board = board;
        this.collision = collision;
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);

        gridPartition.processNearby(transform, 1, other -> {
            if (gameObject == other || gameObject.getId() < other.getId()) return;

            if (!other.checkSignature(ComponentRegistry.idToBit(TRANSFORM_ID) |
                    ComponentRegistry.idToBit(COLLIDER_ID))) return;

            collision.solveCollision(gameObject, other, board);
        });
    }
}
