package demo.systems;

import engine.config.SimulationConfig;
import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import engine.ecs.systems.System;
import engine.math.Vector2;
import engine.world.spatial.GridPartition;
import engine.world.Board;
import demo.components.DotComponent;


/**
 * System for particle AI and interaction forces.
 * 
 * <p>Applies attraction/repulsion forces between particles based on
 * their types and distances. Uses spatial partitioning for efficient
 * neighbor queries.</p>
 * 
 * <h3>Force Rules:</h3>
 * <ul>
 *   <li>Distance < MIN_DISTANCE: Strong repulsion (prevents overlap)</li>
 *   <li>MIN_DISTANCE <= distance < MAX_DISTANCE: Interaction force based on type</li>
 *   <li>Distance >= MAX_DISTANCE: No force applied</li>
 * </ul>
 * 
 * @see demo.components.DotType
 */
public class DotSystem extends System {
    /** Cached component ID for TransformComponent */
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    
    /** Cached component ID for PhysicsComponent */
    private static final int PHYSICS_ID = ComponentRegistry.getId(PhysicsComponent.class);
    
    /** Cached component ID for DotComponent */
    private static final int DOT_ID = ComponentRegistry.getId(DotComponent.class);

    /** Spatial partitioning for neighbor queries */
    private final GridPartition gridPartition;
    
    /** World boundary handler for distance calculations */
    private final Board board;

    /** Gravitational constant for force scaling */
    private final float G = SimulationConfig.Simulation.GRAVITY_CONSTANT;

    private final float MIN_DISTANCE = SimulationConfig.Simulation.MIN_INTERACTION_DISTANCE;
    private final float MAX_DISTANCE = SimulationConfig.Simulation.MAX_INTERACTION_DISTANCE;

    /** ThreadLocal direction vector for parallel processing */
    private final ThreadLocal<Vector2> dirThread = ThreadLocal.withInitial(Vector2::new);

    /**
     * Creates a dot system for particle interactions.
     * 
     * @param threading      Enable parallel processing
     * @param gridPartition  Spatial partitioning for neighbor queries
     * @param board          World boundary handler
     */
    public DotSystem(boolean threading, GridPartition gridPartition, Board board) {
        super(ComponentRegistry.idToBit(TRANSFORM_ID) |
                        ComponentRegistry.idToBit(PHYSICS_ID) |
                        ComponentRegistry.idToBit(DOT_ID),
                threading
        );
        this.gridPartition = gridPartition;
        this.board = board;
    }


    /**
     * Processes a single particle, applying forces from nearby particles.
     * 
     * <p>For each nearby particle:</p>
     * <ol>
     *   <li>Calculate distance</li>
     *   <li>If too close: apply repulsion force</li>
     *   <li>If in interaction range: apply type-based attraction/repulsion</li>
     * </ol>
     * 
     * @param dt         Delta time in seconds
     * @param gameObject The particle to process
     */
    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        PhysicsComponent physics = gameObject.getComponent(PHYSICS_ID);
        TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);
        DotComponent dot = gameObject.getComponent(DOT_ID);

        int searchDistance = (int) Math.ceil(MIN_DISTANCE/ gridPartition.getCellSize());

        gridPartition.processNearby(transform, searchDistance, other -> {
            if (gameObject == other) return;

            if (!other.checkSignature(ComponentRegistry.idToBit(TRANSFORM_ID) |
                    ComponentRegistry.idToBit(DOT_ID))) return;

            TransformComponent otherTransform = other.getComponent(TRANSFORM_ID);
            DotComponent otherDot = other.getComponent(DOT_ID);

            float dist = board.getDistance(transform.getPosition(), otherTransform.getPosition());
            if (dist == 0) return;


            if (dist < MIN_DISTANCE) {
                if (dot.getDotType() == otherDot.getDotType()) return;
                Vector2 dir = dirThread.get();
                board.getDirectionVector(transform.getPosition(), otherTransform.getPosition(), dir);
                dir.scl(1 / dist);
                float nearFactor = (MIN_DISTANCE / dist);
                physics.addForce(dir.scl(-nearFactor * nearFactor * G * 2.5f)); //Find a nice MAGIC number

            } else if (dist < MAX_DISTANCE) {
                Vector2 dir = dirThread.get();
                board.getDirectionVector(transform.getPosition(), otherTransform.getPosition(), dir);
                dir.scl(1 / dist);
                float nearFactor = 1 - (Math.abs(dist - MAX_DISTANCE) / (MAX_DISTANCE - MIN_DISTANCE));
                float forceMagnitude = (dot.getDotType().getInteraction(otherDot.getDotType()) * G) * nearFactor;
                physics.addForce(dir.scl(forceMagnitude));
            }
        });
    }
}
