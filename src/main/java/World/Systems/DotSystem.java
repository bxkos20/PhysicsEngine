package World.Systems;

import GameObject.Components.Core.PhysicsComponent;
import GameObject.Components.Core.TransformComponent;
import GameObject.Components.Dot.DotComponent;
import GameObject.Components.ComponentRegistry;
import GameObject.GameObject;
import World.Board.Board;
import World.Board.Grid.GridPartition;
import com.badlogic.gdx.math.Vector2;

public class DotSystem extends System {
    private final GridPartition gridPartition;
    private final Board board;
    private final float G;

    public DotSystem(GridPartition gridPartition, Board board, float G) {
        super(ComponentRegistry.getBit(TransformComponent.class) |
                ComponentRegistry.getBit(DotComponent.class) |
                ComponentRegistry.getBit(PhysicsComponent.class));
        this.gridPartition = gridPartition;
        this.board = board;
        this.G = G;
    }


    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        PhysicsComponent physics = gameObject.getComponent(PhysicsComponent.class);
        TransformComponent transform = gameObject.getComponent(TransformComponent.class);
        DotComponent dot = gameObject.getComponent(DotComponent.class);

        int searchDistance = (int) Math.ceil(dot.getDotType().MAX_DISTANCE / gridPartition.getCellSize());
        //int searchDistance = 1; //Este numero afecta enormemente al rendimientp
        for (GameObject other : gridPartition.getNearby(transform, searchDistance)) {
            if (gameObject == other) continue;

            if (!other.checkSignature(ComponentRegistry.getBit(TransformComponent.class) |
                    ComponentRegistry.getBit(DotComponent.class))) continue;

            TransformComponent otherTransform = other.getComponent(TransformComponent.class);
            DotComponent otherDot = other.getComponent(DotComponent.class);

            float dist = board.getDistance(transform.getPosition(), otherTransform.getPosition());
            if (dist == 0) continue;


            if (dist < dot.getDotType().MIN_DISTANCE) {
                Vector2 dir = board.getDirectionVector(transform.getPosition(), otherTransform.getPosition()).nor();
                float nearFactor = (dot.getDotType().MIN_DISTANCE / dist) - 1;
                physics.addForce(dir.scl(-nearFactor * G));

            } else if (dist < dot.getDotType().MAX_DISTANCE) {
                Vector2 dir = board.getDirectionVector(transform.getPosition(), otherTransform.getPosition()).nor();
                float mid = (dot.getDotType().MAX_DISTANCE + dot.getDotType().MIN_DISTANCE) / 2;
                float nearFactor = 1 - (Math.abs(dist - mid) / (mid - dot.getDotType().MIN_DISTANCE));
                float forceMagnitude = (dot.getDotType().getInteraction(otherDot.getDotType()) * G) * nearFactor;
                physics.addForce(dir.scl(forceMagnitude));
            }


        }
    }
}
