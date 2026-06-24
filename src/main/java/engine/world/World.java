package engine.world;

import engine.ecs.GameObject;
import engine.physics.Collision;
import engine.world.board.Board;
import engine.world.spatial.GridPartition;

import java.util.ArrayList;
import java.util.List;

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

        gridPartition.clear();
        gridPartition.add(gameObjects);
    }
}