package demos.dots.systems;

import demos.dots.components.DotComponent;
import demos.dots.settings.DotSettings;
import demos.dots.types.DotInteractionManager;
import demos.dots.types.DotType;
import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import engine.ecs.systems.System;
import engine.math.Vector2;
import engine.world.board.Board;
import engine.world.spatial.GridPartition;


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
 * @see DotType
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
    private final float G;

    private final float MIN_DISTANCE;
    private final float MAX_DISTANCE;
    
    private final float MIN_DISTANCE_SQ;
    private final float MAX_DISTANCE_SQ;

    private final DotInteractionManager dotInteractionManager;

    /** ThreadLocal direction vector for parallel processing */
    private final ThreadLocal<Vector2> dirThread = ThreadLocal.withInitial(Vector2::new);

    /**
     * Creates a dot system for particle interactions.
     * 
     * @param threading      Enable parallel processing
     * @param gridPartition  Spatial partitioning for neighbor queries
     * @param board          World boundary handler
     */
    public DotSystem(boolean threading, GridPartition gridPartition, Board board, DotSettings settings) {
        super(threading,
                DotComponent.class,
                PhysicsComponent.class,
                TransformComponent.class
        );
        this.gridPartition = gridPartition;
        this.board = board;

        this.G = settings.gravityConstant;
        this.MIN_DISTANCE = settings.minInteractionDistance;
        this.MAX_DISTANCE = settings.maxInteractionDistance;

        this.MIN_DISTANCE_SQ = MIN_DISTANCE * MIN_DISTANCE;
        this.MAX_DISTANCE_SQ = MAX_DISTANCE * MAX_DISTANCE;

        this.dotInteractionManager = new DotInteractionManager(settings.dotTypes);
    }


    /**
     * Processes a single particle, applying forces from nearby particles.
     * 
     * <p>For each nearby particle:</p>
     * <ol>
     *   <li>Calculate distance</li>
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

        // Intentionally left as MIN_DISTANCE for backward compatibility with previous logic if any,
        // although MAX_DISTANCE might be more correct based on the force rules.
        int searchDistance = (int) Math.ceil(MAX_DISTANCE / gridPartition.getCellSize());

        gridPartition.processNearby(transform, searchDistance, other -> {
            if (gameObject == other) return;

            if (!other.checkSignature(ComponentRegistry.idToBit(TRANSFORM_ID) |
                    ComponentRegistry.idToBit(DOT_ID))) return;

            TransformComponent otherTransform = other.getComponent(TRANSFORM_ID);
            DotComponent otherDot = other.getComponent(DOT_ID);

            Vector2 dir = dirThread.get();
            // This calculates dx, dy with wrap-around and stores in dir
            board.getDirectionVector(transform.getPosition(), otherTransform.getPosition(), dir);
            
            float distSq = dir.x * dir.x + dir.y * dir.y;
            if (distSq == 0) return;

            if (MIN_DISTANCE_SQ < distSq && distSq < MAX_DISTANCE_SQ) {
                float dist = (float) Math.sqrt(distSq);
                dir.scl(1 / dist); // Normalize direction vector
                
                float nearFactor = 1 - (Math.abs(dist - MAX_DISTANCE) / (MAX_DISTANCE - MIN_DISTANCE));
                float interaction = dotInteractionManager.getInteraction(dot.getDotType(), otherDot.getDotType());
                float forceMagnitude = interaction * G * nearFactor;
                
                physics.addForce(dir.scl(forceMagnitude));
            }
        });
    }
}