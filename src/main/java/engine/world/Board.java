package engine.world;

import engine.ecs.components.TransformComponent;
import engine.math.Vector2;

/**
 * Abstract base class for world boundary handling.
 * 
 * <p>Defines distance calculations and boundary enforcement for different
 * world topologies (e.g., bounded, toroidal). Used by physics and collision
 * systems for coordinate transformations.</p>
 * 
 * @see ToroidalBoard
 */
public abstract class Board {
    /** World width */
    protected float width;
    
    /** World height */
    protected float height;

    /**
     * Creates a board with specified dimensions.
     * 
     * @param width  World width
     * @param height World height
     */
    public Board(float width, float height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Calculates distance between two points.
     * 
     * @param origin First point
     * @param target Second point
     * @return Distance in world units
     */
    public abstract float getDistance(Vector2 origin, Vector2 target);

    /**
     * Calculates squared distance between two points.
     * Faster than getDistance() as it avoids sqrt.
     * 
     * @param origin First point
     * @param target Second point
     * @return Squared distance
     */
    public abstract float getDistance2(Vector2 origin, Vector2 target);

    /**
     * Calculates direction vector from origin to target.
     * 
     * @param origin Origin point
     * @param target Target point
     * @param out    Output vector
     */
    public abstract void getDirectionVector(Vector2 origin, Vector2 target, Vector2 out);

    /**
     * Calculates midpoint between two points.
     * 
     * @param origin First point
     * @param target Second point
     * @param out    Output vector
     */
    public abstract void getMidPoint(Vector2 origin, Vector2 target, Vector2 out);

    /**
     * Enforces world boundaries on a transform.
     * May wrap position (toroidal) or clamp (bounded).
     * 
     * @param transform Transform to constrain
     */
    public abstract void enforceBounds(TransformComponent transform);
}