package engine.ecs.components;

import engine.ecs.systems.implementations.CollisionSystem;

/**
 * Component for collision detection using circular or axis-aligned rectangular bounds.
 * 
 * <p>Defines a collision shape centered on the entity's transform position.
 * Used by {@link CollisionSystem} for broad-phase and narrow-phase
 * collision detection.</p>
 * 
 * @see CollisionSystem
 */
public class ColliderComponent {
    private final boolean circle;
    private float radius;
    private final float width;
    private final float height;

    /**
     * Creates a collider with the specified radius.
     * 
     * @param radius Collision circle radius in world units
     */
    public ColliderComponent(float radius) {
        validatePositive(radius, "radius");
        this.circle = true;
        this.radius = radius;
        this.width = radius * 2;
        this.height = radius * 2;
    }

    /**
     * Creates an axis-aligned rectangular collider centered on the entity.
     *
     * @param width  Rectangle width in world units
     * @param height Rectangle height in world units
     */
    public ColliderComponent(float width, float height) {
        validatePositive(width, "width");
        validatePositive(height, "height");
        this.circle = false;
        this.radius = 0;
        this.width = width;
        this.height = height;
    }

    private static void validatePositive(float value, String name) {
        if (!Float.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException(name + " must be finite and greater than zero");
        }
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
        if (!circle) {
            throw new IllegalStateException("An AABB collider does not have a radius");
        }
        validatePositive(radius, "radius");
        this.radius = radius;
    }

    public boolean isCircle() {
        return circle;
    }

    public float getWidth() {
        return circle ? radius * 2 : width;
    }

    public float getHeight() {
        return circle ? radius * 2 : height;
    }

    /** Returns the maximum distance from the center along either axis. */
    public float getBroadphaseReach() {
        return circle ? radius : Math.max(width, height) / 2f;
    }
}
