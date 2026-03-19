package engine.ecs.components;

/**
 * Component for collision detection using circular bounds.
 * 
 * <p>Defines a circular collision shape centered on the entity's transform position.
 * Used by {@link engine.ecs.systems.CollisionSystem} for broad-phase and narrow-phase
 * collision detection.</p>
 * 
 * @see engine.ecs.systems.CollisionSystem
 */
public class ColliderComponent {
    /** Radius of the circular collision bounds */
    private float radius;

    /**
     * Creates a collider with the specified radius.
     * 
     * @param radius Collision circle radius in world units
     */
    public ColliderComponent(float radius) {
        this.radius = radius;
    }

    /**
     * Returns the collision radius.
     * 
     * @return Radius in world units
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets the collision radius.
     * 
     * @param radius New radius in world units
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }
}
