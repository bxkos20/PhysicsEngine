package World.Systems;

import GameObject.Components.Core.ColliderComponent;
import GameObject.Components.Core.PhysicsComponent;
import GameObject.Components.Core.TransformComponent;
import GameObject.Components.Dot.DotComponent;
import GameObject.Components.ComponentRegistry;
import GameObject.GameObject;
import World.Board.Board;
import World.Board.Grid.GridPartition;
import com.badlogic.gdx.math.Vector2;

public class DotSystem extends System {
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    private static final int PHYSICS_ID = ComponentRegistry.getId(PhysicsComponent.class);
    private static final int DOT_ID = ComponentRegistry.getId(DotComponent.class);

    private final GridPartition gridPartition;
    private final Board board;
    private final float G = 25f;

    public DotSystem(boolean threading, GridPartition gridPartition, Board board) {
        super(ComponentRegistry.idToBit(TRANSFORM_ID) |
                        ComponentRegistry.idToBit(PHYSICS_ID) |
                        ComponentRegistry.idToBit(DOT_ID),
                threading
        );
        this.gridPartition = gridPartition;
        this.board = board;
    }


    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        PhysicsComponent physics = gameObject.getComponent(PHYSICS_ID);
        TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);
        DotComponent dot = gameObject.getComponent(DOT_ID);

        int searchDistance = (int) Math.ceil(dot.getDotType().MAX_DISTANCE / gridPartition.getCellSize());

        gridPartition.processNearby(transform, searchDistance, other -> {
            if (gameObject == other) return;

            if (!other.checkSignature(ComponentRegistry.idToBit(TRANSFORM_ID) |
                    ComponentRegistry.idToBit(DOT_ID))) return;

            TransformComponent otherTransform = other.getComponent(TRANSFORM_ID);
            DotComponent otherDot = other.getComponent(DOT_ID);

            float dist = board.getDistance(transform.getPosition(), otherTransform.getPosition());
            if (dist == 0) return;


            if (dist < dot.getDotType().MIN_DISTANCE) {
                Vector2 dir = board.getDirectionVector(transform.getPosition(), otherTransform.getPosition()).scl(1 / dist);
                float nearFactor = (dot.getDotType().MIN_DISTANCE / dist) - 1;
                physics.addForce(dir.scl(-nearFactor * G));

            } else if (dist < dot.getDotType().MAX_DISTANCE) {
                Vector2 dir = board.getDirectionVector(transform.getPosition(), otherTransform.getPosition()).scl(1 / dist);
                float mid = (dot.getDotType().MAX_DISTANCE + dot.getDotType().MIN_DISTANCE) / 2;
                float nearFactor = 1 - (Math.abs(dist - mid) / (mid - dot.getDotType().MIN_DISTANCE));
                float forceMagnitude = (dot.getDotType().getInteraction(otherDot.getDotType()) * G) * nearFactor;
                physics.addForce(dir.scl(forceMagnitude));
            }
        });
    }
}
