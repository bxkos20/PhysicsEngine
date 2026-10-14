package engine.ecs.systems.implementations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.TransformComponent;
import engine.ecs.systems.System;
import engine.physics.Collision;
import engine.world.board.Board;
import engine.world.spatial.GridPartition;

/**
 * System for detecting and resolving collisions between entities.
 * 
 * <p>Processes entities with {@link TransformComponent} and {@link ColliderComponent}.
 * Uses spatial partitioning for efficient broad-phase collision detection.</p>
 * 
 * <h3>Threading Strategy:</h3>
 * <ul>
 *   <li>Detection phase: Parallel processing with manual thread pool</li>
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
    private final ThreadLocal<List<GameObject>> pairsThread = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<Set<Integer>> nearbyIdsThread = ThreadLocal.withInitial(HashSet::new);

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
        super(threading,
                TransformComponent.class,
                ColliderComponent.class
        );
        this.gridPartition = gridPartition;
        this.board = board;
        this.collision = collision;
    }

    /**
     * Updates collision detection and resolution using custom logic.
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
    protected void processUpdate(float dt, List<GameObject> gameObjects) {
        float maximumReach = getMaximumReach(gameObjects);
        if (THREADING) {
            processParallelCollisionDetection(gameObjects, maximumReach);
        } else {
            processSequentialCollisionDetection(gameObjects, maximumReach);
        }
    }

    private float getMaximumReach(List<GameObject> gameObjects) {
        float maximumReach = 0;
        for (GameObject gameObject : gameObjects) {
            if (!gameObject.checkSignature(REQUIRED_SIGNATURE)) continue;
            ColliderComponent collider = gameObject.getComponent(COLLIDER_ID);
            maximumReach = Math.max(maximumReach, collider.getBroadphaseReach());
        }
        return maximumReach;
    }

    private int getSearchDistance(ColliderComponent collider, float maximumReach) {
        float reach = collider.getBroadphaseReach() + maximumReach;
        return Math.max(1, (int) Math.ceil(reach / gridPartition.getCellSize()));
    }
    
    /**
     * Parallel collision detection using base class helper methods.
     * Eliminates GC pressure from parallelStream() and lambda allocations.
     */
    private void processParallelCollisionDetection(List<GameObject> gameObjects, float maximumReach) {
        // Collect collision pairs from all threads
        List<GameObject> allCollisions = new ArrayList<>();
        
        // Use base class helper method for parallel processing with results
        processInParallelWithResults(gameObjects, allCollisions, (items, startIdx, endIdx, results) -> {
            List<GameObject> threadCollisions = pairsThread.get();
            threadCollisions.clear();
            
            for (int i = startIdx; i < endIdx; i++) {
                GameObject gameObject = items.get(i);
                if (!gameObject.checkSignature(REQUIRED_SIGNATURE)) continue;
                
                TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);
                ColliderComponent collider = gameObject.getComponent(COLLIDER_ID);
                Set<Integer> nearbyIds = nearbyIdsThread.get();
                nearbyIds.clear();
                
                gridPartition.processNearby(transform, getSearchDistance(collider, maximumReach), other -> {
                    // Skip duplicates and non-colliding entities
                    if (gameObject.getId() >= other.getId() || !nearbyIds.add(other.getId())
                            || !other.checkSignature(REQUIRED_SIGNATURE)) return;
                    
                    if (collision.isColliding(gameObject, other, board)) {
                        threadCollisions.add(gameObject);
                        threadCollisions.add(other);
                    }
                });
            }
            
            // Safely add this thread's collisions to the shared list
            synchronized (allCollisions) {
                results.addAll(threadCollisions);
            }
        });
        
        // Sequential resolution phase to avoid race conditions
        for (int i = 0; i < allCollisions.size(); i += 2) {
            GameObject a = allCollisions.get(i);
            GameObject b = allCollisions.get(i + 1);
            collision.solveCollision(a, b, board);
        }
    }
    
    /**
     * Sequential collision detection and resolution.
     */
    private void processSequentialCollisionDetection(List<GameObject> gameObjects, float maximumReach) {
        for (GameObject gameObject : gameObjects) {

            if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);
                ColliderComponent collider = gameObject.getComponent(COLLIDER_ID);
                Set<Integer> nearbyIds = nearbyIdsThread.get();
                nearbyIds.clear();

                gridPartition.processNearby(transform, getSearchDistance(collider, maximumReach), other -> {
                    // Skip duplicates and check signatures
                    if (gameObject.getId() >= other.getId() || !nearbyIds.add(other.getId())
                            || !other.checkSignature(REQUIRED_SIGNATURE)) return;

                    if (collision.isColliding(gameObject, other, board))
                        collision.solveCollision(gameObject, other, board);
                });
            }
        }
    }

    /**
     * Not used - collision system overrides update() directly.
     */
    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
    }
}
