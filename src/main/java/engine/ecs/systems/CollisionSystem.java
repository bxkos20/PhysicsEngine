package engine.ecs.systems;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.TransformComponent;
import engine.physics.Collision;
import engine.spatial.GridPartition;
import engine.world.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * System for detecting and resolving collisions between entities.
 * 
 * <p>Processes entities with {@link TransformComponent} and {@link ColliderComponent}.
 * Uses spatial partitioning for efficient broad-phase collision detection.</p>
 * 
 * <h3>Threading Strategy:</h3>
 * <ul>
 *   <li>Detection phase: Parallel processing with ThreadLocal storage</li>
 *   <li>Resolution phase: Sequential to avoid race conditions</li>
 * </ul>
 * 
 * @see Collision
 * @see GridPartition
 */
public class CollisionSystem extends System {
    /** Cached component ID for TransformComponent */
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    
    /** Cached component ID for ColliderComponent */
    private static final int COLLIDER_ID = ComponentRegistry.getId(ColliderComponent.class);
    
    /** ThreadLocal list for parallel collision pair collection */
    private ThreadLocal<List<GameObject>> pairsThread = ThreadLocal.withInitial(ArrayList::new);

    /** Spatial partitioning for broad-phase collision detection */
    private final GridPartition gridPartition;
    
    /** Board for world coordinate handling (toroidal support) */
    private final Board board;
    
    /** Collision detection and resolution strategy */
    private final Collision collision;

    /**
     * Creates a collision system.
     * 
     * @param threading      Enable parallel processing
     * @param gridPartition  Spatial partitioning for broad-phase
     * @param board          Board for world coordinates
     * @param collision      Collision resolution strategy
     */
    public CollisionSystem(boolean threading, GridPartition gridPartition, Board board, Collision collision) {
        super(ComponentRegistry.idToBit(TRANSFORM_ID) |
                        ComponentRegistry.idToBit(COLLIDER_ID),
                threading
        );
        this.gridPartition = gridPartition;
        this.board = board;
        this.collision = collision;
    }

    /**
     * Updates collision detection and resolution.
     * 
     * <p>Two-phase approach:</p>
     * <ol>
     *   <li>Detection: Find all colliding pairs using spatial partitioning</li>
     *   <li>Resolution: Apply collision response to each pair</li>
     * </ol>
     * 
     * @param dt          Delta time (unused but required by signature)
     * @param gameObjects All game objects to check
     */
    @Override
    public void update(float dt, List<GameObject> gameObjects) {
        long start = java.lang.System.nanoTime();
        if (THREADING) {
            // Phase 1: Parallel detection
            List<GameObject> pendingCollisions = gameObjects.parallelStream()
                    .filter(go -> go.checkSignature(REQUIRED_SIGNATURE))
                    .flatMap(gameObject -> {
                        TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);
                        List<GameObject> collisions = new ArrayList<>();
                        
                        gridPartition.processNearby(transform, 1, other -> {
                            // Skip duplicates and non-colliding entities
                            if (gameObject.getId() >= other.getId() || !other.checkSignature(REQUIRED_SIGNATURE)) return;
                            
                            if (collision.isColliding(gameObject, other, board)) {
                                collisions.add(gameObject);
                                collisions.add(other);
                            }
                        });
                        
                        return collisions.stream();
                    })
                    .collect(Collectors.toList());

            // Phase 2: Sequential resolution
            for (int i = 0; i < pendingCollisions.size(); i += 2) {
                GameObject a = pendingCollisions.get(i);
                GameObject b = pendingCollisions.get(i + 1);
                collision.solveCollision(a, b, board);
            }

        } else {
            // Sequential processing
            for (int i = 0; i < gameObjects.size(); i++) {
                GameObject gameObject = gameObjects.get(i);

                if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                    TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);

                    gridPartition.processNearby(transform, 1, other -> {
                        // Skip duplicates and check signatures
                        if (gameObject.getId() >= other.getId() || !other.checkSignature(REQUIRED_SIGNATURE)) return;

                        if (collision.isColliding(gameObject, other, board))
                            collision.solveCollision(gameObject, other, board);
                    });
                }
            }
        }
        this.lastExecutionTimeMs = (java.lang.System.nanoTime() - start) / 1_000_000f;
    }

    /**
     * Not used - collision system overrides update() directly.
     */
    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
    }
}
