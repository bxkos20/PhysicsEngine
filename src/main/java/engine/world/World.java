package engine.world;

import engine.config.SimulationConfig;
import engine.ecs.GameObject;
import engine.ecs.systems.CollisionSystem;
import engine.ecs.systems.MovementSystem;
import engine.ecs.systems.System;
import engine.physics.Collision;
import engine.world.spatial.GridPartition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Container for all game entities and systems.
 * 
 * <p>Manages entity lifecycle, system execution order, and world geometry.
 * Uses dependency injection for board, collision, and spatial partition.</p>
 * 
 * <h3>Update Pipeline:</h3>
 * <ol>
 *   <li>Add pending entities to main list</li>
 *   <li>Clear and rebuild spatial grid</li>
 *   <li>Run custom logic system (if provided) - applies forces</li>
 *   <li>Run physics integration (MovementSystem) - updates positions</li>
 *   <li>Run collision detection (CollisionSystem) - resolves overlaps</li>
 * </ol>
 */
public class World {
    /** Main entity list */
    private final List<GameObject> gameObjects;
    
    /** Buffer for entities waiting to be added */
    private final List<GameObject> objectsToAdd;

    /** World boundary handler */
    public final Board board;
    
    /** Collision resolution strategy */
    public final Collision collision;
    
    /** Spatial partitioning for neighbor queries */
    public final GridPartition gridPartition;

    /** Physics integration system */
    MovementSystem movementSystem;
    
    /** Collision detection system */
    CollisionSystem collisionSystem;

    /**
     * Creates a world with injected dependencies.
     * 
     * @param board         World boundary handler
     * @param collision     Collision resolution strategy
     * @param gridPartition Spatial partitioning implementation
     */
    public World(Board board, Collision collision, GridPartition gridPartition) {
        this.board = board;
        this.collision = collision;
        this.gridPartition = gridPartition;
        this.gameObjects = new ArrayList<>();
        this.objectsToAdd = new ArrayList<>();
        //TODO: Move to EngineSimulation
        this.movementSystem = new MovementSystem(SimulationConfig.Performance.ENABLE_MULTITHREADING, board);
        this.collisionSystem = new CollisionSystem(true, gridPartition, board, collision);
    }

    /**
     * Queues an entity for addition. Added at start of next update.
     * 
     * @param gameObject Entity to add
     */
    public void addEntity(GameObject gameObject) {
        objectsToAdd.add(gameObject);
    }

    /**
     * Returns the main entity list.
     * 
     * @return List of all active entities
     */
    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    /**
     * Updates all systems for one frame.
     * 
     * @param dt Delta time in seconds
     */
    public void update(float dt) {
        // Phase 0: Add pending entities
        if (!objectsToAdd.isEmpty()) {
            gameObjects.addAll(objectsToAdd);
            objectsToAdd.clear();
        }

        // Phase 1: Rebuild spatial grid
        gridPartition.clear();
        gridPartition.add(gameObjects);

        // Phase 3: Physics integration - update positions
        movementSystem.update(dt, gameObjects);

        // Phase 4: Collision detection and resolution
        collisionSystem.update(dt, gameObjects);
    }

    /**
     * Returns profiling information for all systems.
     * 
     * @return Formatted string with execution times
     */
    public String getProfilingInfo() {
        StringBuilder customInfo = new StringBuilder();
        
        return String.format("%sPhysics: %.2fms | Collisions: %.2fms",
                customInfo.toString(),
                movementSystem.getLastExecutionTimeMs(),
                collisionSystem.getLastExecutionTimeMs());
    }

    public void forEachObject(Consumer<GameObject> action) {
        gameObjects.forEach(action);
    }
}
