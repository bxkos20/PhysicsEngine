import GameObject.GameObject;
import World.PhysicsWorld;
import com.badlogic.gdx.math.Vector2;

import javax.sound.midi.Soundbank;

public class Dot extends GameObject {
    private final DotType dotType;

    public Dot(float x, float y, DotType dotType) {
        super(x, y);
        this.dotType = dotType;
        super.withPhysics().withCollider(DotType.RADIUS);
        super.color = dotType.COLOR;
    }

    public DotType getDotType() {
        return dotType;
    }

    @Override
    public void update(float dt, PhysicsWorld world) {
        int searchDistance = (int) Math.ceil(dotType.MAX_DISTANCE / world.gridPartition.getCellSize());
        for (GameObject otherGO : world.gridPartition.getNearby(this, searchDistance)) {
            if (this == otherGO) continue;
            if (!(otherGO instanceof Dot)) continue;

            Dot other = (Dot) otherGO;

            float dist = world.board.getDistance(this.transform.position, other.transform.position);
            if (dist == 0) continue;


            if (dist < dotType.MIN_DISTANCE) {
                Vector2 dir = world.board.getDirectionVector(this.transform.position, other.transform.position).nor();
                float nearFactor = (dotType.MIN_DISTANCE / dist) - 1;
                this.physics.addForce(dir.scl(-nearFactor * world.G));

            } else if (dist < dotType.MAX_DISTANCE) {
                Vector2 dir = world.board.getDirectionVector(this.transform.position, other.transform.position).nor();
                float mid = (dotType.MAX_DISTANCE + dotType.MIN_DISTANCE) / 2;
                float nearFactor = 1 - (Math.abs(dist - mid) / (mid - dotType.MIN_DISTANCE));
                float forceMagnitude = (dotType.getInteraction(other.getDotType()) * world.G) * nearFactor;
                this.physics.addForce(dir.scl(forceMagnitude));
            }


        }
    }
}
