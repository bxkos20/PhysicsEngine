package engine.world.spatial;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.TransformComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Spatial grid partition with toroidal (wrap-around) topology.
 * 
 * <p>Entities near world edges can interact with entities on the opposite side,
 * creating a seamless wrap-around world. Used for simulations where particles
 * should not bounce off walls but instead wrap around.</p>
 * 
 * <h3>Thread Safety:</h3>
 * <p>Uses ThreadLocal for neighbor lists to support parallel collision detection.</p>
 * 
 * @see GridPartition
 */
public class ToroidalGridPartition extends GridPartition {
    /** Cell storage array - each cell contains a list of game objects */
    private final List<GameObject>[] cells;
    
    /** ThreadLocal neighbor list for parallel processing */
    private final ThreadLocal<ArrayList<GameObject>> nearbyThread = ThreadLocal.withInitial(ArrayList::new);


    /**
     * Creates a toroidal grid partition.
     * 
     * @param width    World width
     * @param height   World height
     * @param cellSize Cell size
     */
    public ToroidalGridPartition(float width, float height, float cellSize) {
        super(width, height, cellSize);
        @SuppressWarnings("unchecked")
        List<GameObject>[] cells = new List[rows * cols];
        this.cells = cells;
        for (int i = 0; i < cells.length; i++) cells[i] = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * Clears all cells without reallocating.
     */
    @Override
    public void clear() {
        for (int i = 0; i < cells.length; i++) cells[i].clear();
    }

    /**
     * {@inheritDoc}
     * Applies toroidal wrap: col = (col % cols + cols) % cols
     */
    @Override
    protected int getCol(float x) {
        int col = (int) Math.floor(x / cellSize);
        col = (col % cols + cols) % cols;
        return col;
    }

    /**
     * {@inheritDoc}
     * Applies toroidal wrap: row = (row % rows + rows) % rows
     */
    @Override
    protected int getRow(float y) {
        int row = (int) Math.floor(y / cellSize);
        row = (row % rows + rows) % rows;
        return row;
    }

    /**
     * {@inheritDoc}
     * Processes neighbors from all cells within distance, wrapping at edges.
     */
    @Override
    public int processNearby(TransformComponent transform, int distance, Consumer<GameObject> processor) {
        int centerCol = getCol(transform.getPosition().x);
        int centerRow = getRow(transform.getPosition().y);
        int cellsProcessed = 0;

        for (int i = -distance; i <= distance; i++) {
            for (int j = -distance; j <= distance; j++) {
                int neighborCol = centerCol + i;
                int neighborRow = centerRow + j;

                // 2. Aplicar Wrap Toroidal (Lógica específica de esta clase)
                neighborCol = (neighborCol % cols + cols) % cols;
                neighborRow = (neighborRow % rows + rows) % rows;

                // 3. Calcular ID y añadir
                int id = neighborCol + neighborRow * cols;
                List<GameObject> cell = cells[id];

                for (int k = 0; k < cell.size(); k++) {
                    processor.accept(cell.get(k));
                    cellsProcessed++;
                }
            }
        }
        return cellsProcessed;
    }

    /**
     * {@inheritDoc}
     * Adds entity to the cell corresponding to its position.
     */
    @Override
    public void add(GameObject gameObject) {
        final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);

        if (gameObject.checkSignature(ComponentRegistry.idToBit(TRANSFORM_ID))) {

            TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);
            int id = getCol(transform.getPosition().x) + getRow(transform.getPosition().y) * cols;
            cells[id].add(gameObject);
        }
    }

    @Override
    public void add(List<GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {
            add(gameObject);
        }
    }
}
