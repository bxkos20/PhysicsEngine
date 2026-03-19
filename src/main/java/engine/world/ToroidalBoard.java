package engine.world;

import engine.ecs.components.TransformComponent;
import engine.math.Vector2;

/**
 * Board implementation with toroidal (wrap-around) topology.
 * 
 * <p>In a toroidal world, entities that exit one edge reappear on the opposite side.
 * Distance calculations consider the shortest path, which may cross edges.</p>
 * 
 * <h3>Use Case:</h3>
 * <p>Ideal for simulations where entities should not be confined by walls,
 * such as particle simulations or open-world games with seamless wrapping.</p>
 * 
 * @see Board
 */
public class ToroidalBoard extends Board {
    /**
     * Creates a toroidal board with specified dimensions.
     * 
     * @param width  World width
     * @param height World height
     */
    public ToroidalBoard(float width, float height) {
        super(width, height);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Wraps position to world bounds using modulo:
     * pos = (pos % size + size) % size</p>
     */
    @Override
    public void enforceBounds(TransformComponent transform) {
        transform.getPosition().x = (transform.getPosition().x % width + width) % width;
        transform.getPosition().y = (transform.getPosition().y % height + height) % height;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Calculates shortest distance considering wrap-around.</p>
     */
    @Override
    public float getDistance(Vector2 origin, Vector2 target) {
        return (float) Math.sqrt(getDistance2(origin, target));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Calculates shortest squared distance considering wrap-around.
     * Uses: dx -= size * round(dx / size) for toroidal correction.</p>
     */
    @Override
    public float getDistance2(Vector2 origin, Vector2 target) {
        float dx = origin.x - target.x;
        float dy = origin.y - target.y;

        dx -= width * Math.round(dx / width);
        dy -= height * Math.round(dy / height);

        return dx * dx + dy * dy;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Returns the shortest direction vector from origin to target,
     * which may cross world edges.</p>
     */
    @Override
    public void getDirectionVector(Vector2 origin, Vector2 target, Vector2 out) {
        float dx = target.x - origin.x;
        float dy = target.y - origin.y;

        dx -= width * Math.round(dx / width);
        dy -= height * Math.round(dy / height);

        out.set(dx, dy);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Calculates the true midpoint considering toroidal geometry.
     * The midpoint is found by taking half the shortest path vector.</p>
     */
    @Override
    public void getMidPoint(Vector2 origin, Vector2 target, Vector2 out) {
        // Get the direction vector (shortest path)
        getDirectionVector(origin, target, out);

        // Midpoint is: origin + (direction / 2)
        out.scl(0.5f).add(origin);

        // Ensure result is within bounds
        out.x = (out.x % width + width) % width;
        out.y = (out.y % height + height) % height;
    }
}