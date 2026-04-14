package engine.ecs.systems;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import engine.world.board.Board;

/**
 * System that updates entity positions based on physics.
 * 
 * <p>Processes entities with {@link TransformComponent} and {@link PhysicsComponent}.
 * Uses semi-implicit Euler integration for physics simulation:</p>
 * 
 * <ol>
 *   <li>Update velocity: v = v + (F/m) * dt</li>
 *   <li>Apply friction: v = v * (1 - friction * dt)</li>
 *   <li>Update position: p = p + v * dt</li>
 *   <li>Enforce world bounds via {@link Board}</li>
 *   <li>Clear accumulated forces</li>
 * </ol>
 * 
 * @see PhysicsComponent
 * @see TransformComponent
 */
public class MovementSystem extends System {
    /** Cached component ID for TransformComponent */
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    
    /** Cached component ID for PhysicsComponent */
    private static final int PHYSICS_ID = ComponentRegistry.getId(PhysicsComponent.class);

    /** Board for boundary enforcement */
    private final Board board;

    /**
     * Creates a movement system.
     * 
     * @param threading Enable parallel processing
     * @param board     Board for boundary enforcement
     */
    public MovementSystem(boolean threading, Board board) {
        super(threading,
                TransformComponent.class,
                PhysicsComponent.class);
        this.board = board;
    }

    /**
     * Processes a single entity's physics update.
 * 
     * <p>Integration steps:</p>
     * <ol>
     *   <li>Calculate inverse mass (0 for static objects with mass <= 0)</li>
     *   <li>Update velocity from accumulated forces</li>
     *   <li>Apply friction damping</li>
     *   <li>Update position from velocity</li>
     *   <li>Enforce world bounds</li>
     *   <li>Clear forces for next frame</li>
     * </ol>
     * 
     * @param dt         Delta time in seconds
     * @param gameObject Entity with transform and physics components
     */
    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        PhysicsComponent physics = gameObject.getComponent(PHYSICS_ID);
        TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);

        // Update velocity: v = v + (F / m) * dt
        float invMass = (physics.getMass() <= 0) ? 0 : (1f / physics.getMass());
        physics.getVelocity().mulAdd(physics.getSumForces(), invMass * dt);

        // Apply friction/damping: v = v * (1 - friction * dt)
        // Reduces velocity gradually without risking direction inversion
        float frictionFactor = Math.max(0, 1 - physics.getFriction() * dt);
        physics.getVelocity().scl(frictionFactor);

        // Update position: p = p + v * dt
        transform.getPosition().mulAdd(physics.getVelocity(), dt);

        // Enforce world bounds
        board.enforceBounds(transform);

        // Clear accumulated forces for next frame
        physics.getSumForces().set(0, 0);
    }
}
