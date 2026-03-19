package engine.ecs.components;

import engine.math.Vector2;

/**
 * Component for spatial position in 2D world space.
 * 
 * <p>Every renderable or physics-simulated entity requires this component.
 * Defines the entity's position for rendering, collision detection, and physics.</p>
 * 
 * @see engine.ecs.systems.MovementSystem
 */
public class TransformComponent {
    /** Position in 2D world coordinates */
    private Vector2 position;

    /**
     * Creates a transform at the specified position.
     * 
     * @param x World X coordinate
     * @param y World Y coordinate
     */
    public TransformComponent(float x, float y) {
        this.position = new Vector2(x, y);
    }

    /**
     * Returns the current position.
     * 
     * @return Position vector (mutable reference)
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Sets the position from a vector.
     * 
     * @param position New position vector
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    /**
     * Sets the position from coordinates.
     * 
     * @param x New X coordinate
     * @param y New Y coordinate
     */
    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }
}