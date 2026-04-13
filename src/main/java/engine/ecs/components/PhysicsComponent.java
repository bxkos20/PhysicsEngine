package engine.ecs.components;

import engine.math.Vector2;

/**
 * Component for physics simulation (velocity, forces, mass, material properties).
 * 
 * <p>Used by {@link engine.ecs.systems.MovementSystem} to update entity positions
 * based on accumulated forces, and by {@link engine.ecs.systems.CollisionSystem}
 * for collision response.</p>
 * 
 * <h3>Physics Properties:</h3>
 * <ul>
 *   <li>velocity - Current linear velocity (units/second)</li>
 *   <li>sumForces - Accumulated forces for this frame</li>
 *   <li>mass - Entity mass (affects acceleration and collision response)</li>
 *   <li>restitution - Bounciness (0 = inelastic, 1 = perfectly elastic)</li>
 *   <li>friction - Surface friction (0 = frictionless, 1 = max friction)</li>
 * </ul>
 */
public class PhysicsComponent {
    /** Current linear velocity in units/second */
    private Vector2 velocity;
    
    /** Accumulated forces to be applied this frame */
    private Vector2 sumForces;
    
    /** Entity mass in arbitrary units */
    private float mass;
    
    /** Coefficient of restitution (bounciness): 0 = inelastic, 1 = perfectly elastic */
    private float restitution;
    
    /** Friction coefficient: 0 = frictionless, 1 = maximum friction */
    private float friction;

    /**
     * Creates a physics component with specified mass and material properties.
     * 
     * @param mass       Entity mass (affects acceleration: a = F/m)
     * @param restitution Bounciness coefficient [0, 1]
     * @param friction   Friction coefficient [0, 1]
     */
    public PhysicsComponent(float mass, float restitution, float friction) {
        this.velocity = new Vector2();
        this.sumForces = new Vector2();
        this.mass = mass;
        this.restitution = restitution;
        this.friction = friction;
    }

    /**
     * Adds a force to the accumulated force vector.
     * Forces are cleared after each physics update.
     * 
     * @param force Force vector to add
     */
    public void addForce(Vector2 force) {
        sumForces.add(force);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
    }

    public Vector2 getSumForces() {
        return sumForces;
    }

    public void setSumForces(Vector2 sumForces) {
        this.sumForces.set(sumForces);
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }
}
