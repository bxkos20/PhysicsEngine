package engine.world.board;

import engine.ecs.components.TransformComponent;
import engine.math.Vector2;

/**
 * Board implementation with edged topology
 * TODO
 * 
 * @see Board
 */
public class EdgedBoard extends Board {

    /**
     * Creates a toroidal board with specified dimensions.
     *
     * @param width  World width
     * @param height World height
     */
    public EdgedBoard(float width, float height) {
        super(width, height);
    }

    /**
     * {@inheritDoc}
     * 
     * Wraps position to world bounds
     */
    @Override
    public void enforceBounds(TransformComponent transform) {
        if (transform.getPosition().x < 0) transform.getPosition().x = 0;
        if (transform.getPosition().x > width) transform.getPosition().x = width;
        if (transform.getPosition().y < 0) transform.getPosition().y = 0;
        if (transform.getPosition().y > height) transform.getPosition().y = height;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Calculates shortest distance.</p>
     */
    @Override
    public float getDistance(Vector2 origin, Vector2 target) {
        return (float) Math.sqrt(getDistance2(origin, target));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Calculates shortest squared distance </p>
     */
    @Override
    public float getDistance2(Vector2 origin, Vector2 target) {
        float dx = origin.x - target.x;
        float dy = origin.y - target.y;

        return dx * dx + dy * dy;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Returns the shortest direction vector from origin to target</p>
     */
    @Override
    public void getDirectionVector(Vector2 origin, Vector2 target, Vector2 out) {
        float dx = target.x - origin.x;
        float dy = target.y - origin.y;

        out.set(dx, dy);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Calculates the true midpoint
     * The midpoint is found by taking half the shortest path vector.</p>
     */
    @Override
    public void getMidPoint(Vector2 origin, Vector2 target, Vector2 out) {
        // Get the direction vector (shortest path)
        getDirectionVector(origin, target, out);

        // Midpoint is: origin + (direction / 2)
        out.scl(0.5f).add(origin);
    }
}