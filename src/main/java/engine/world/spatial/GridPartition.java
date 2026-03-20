package engine.world.spatial;

import engine.ecs.GameObject;
import engine.ecs.components.TransformComponent;

import java.util.List;
import java.util.function.Consumer;

/**
 * Abstract base class for spatial partitioning grids.
 * 
 * <p>Divides the world into cells for efficient neighbor queries.
 * Used by collision detection to avoid O(n^2) pairwise checks.</p>
 * 
 * <h3>Performance:</h3>
 * <p>Reduces collision detection from O(n^2) to approximately O(n)
 * when entities are uniformly distributed.</p>
 * 
 * @see ToroidalGridPartition
 * @see engine.ecs.systems.CollisionSystem
 */
public abstract class GridPartition {
    /** Number of rows in the grid */
    protected final int rows;
    
    /** Number of columns in the grid */
    protected final int cols;
    
    /** Size of each cell in world units */
    protected final float cellSize;
    
    /** Total world width */
    protected final float width;
    
    /** Total world height */
    protected final float height;

    /**
     * Creates a grid partition with specified dimensions.
     * 
     * @param width    Total world width
     * @param height   Total world height
     * @param cellSize Size of each cell
     */
    public GridPartition(float width, float height, float cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.cols = (int) Math.ceil(width / cellSize);
        this.rows = (int) Math.ceil(height / cellSize);
    }

    /** @return Cell size in world units */
    public float getCellSize() {
        return cellSize;
    }

    /** @return Total world width */
    public float getWidth() {
        return width;
    }

    /** @return Total world height */
    public float getHeight() {
        return height;
    }

    /**
     * Converts world X coordinate to column index.
     * 
     * @param x World X coordinate
     * @return Column index
     */
    protected abstract int getCol(float x);

    /**
     * Converts world Y coordinate to row index.
     * 
     * @param y World Y coordinate
     * @return Row index
     */
    protected abstract int getRow(float y);

    /** Clears all entities from the grid */
    public abstract void clear();

    /**
     * Processes entities near the given transform with a callback.
     * 
     * @param transform  Transform to search around
     * @param distance   Cell distance to search
     * @param processor  Callback for each nearby entity
     * @return Number of entities processed
     */
    public abstract int processNearby(TransformComponent transform, int distance, Consumer<GameObject> processor);

    /**
     * Adds a single entity to the grid.
     * 
     * @param gameObject Entity to add
     */
    public abstract void add(GameObject gameObject);

    /**
     * Adds multiple entities to the grid.
     * 
     * @param gameObjects Entities to add
     */
    public abstract void add(List<GameObject> gameObjects);

}
