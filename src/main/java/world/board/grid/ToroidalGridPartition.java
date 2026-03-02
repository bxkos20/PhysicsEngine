package world.board.grid;

import gameObject.components.core.TransformComponent;
import gameObject.components.ComponentRegistry;
import gameObject.GameObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ToroidalGridPartition extends GridPartition {
    private ArrayList<GameObject>[] cells;
    private ThreadLocal<ArrayList<GameObject>> nearbyThread = ThreadLocal.withInitial(ArrayList::new);


    public ToroidalGridPartition(float width, float height, float cellSize) {
        super(width, height, cellSize);
        cells = new ArrayList[rows * cols];
        for (int i = 0; i < cells.length; i++) cells[i] = new ArrayList<GameObject>();
    }

    @Override
    public void clear() {
        for (int i = 0; i < cells.length; i++) cells[i].clear();
    }

    @Override
    protected int getCol(float x) {
        int col = (int) Math.floor(x / cellSize);;
        col = (col % cols + cols) % cols;
        return col;
    }

    @Override
    protected int getRow(float y) {
        int row = (int) Math.floor(y / cellSize);
        row = (row % rows + rows) % rows;
        return row;
    }

    @Override
    public List<GameObject> getNearby(TransformComponent transform, int distance) {
        ArrayList<GameObject> nearby = nearbyThread.get();
        nearby.clear();

        int centerCol = getCol(transform.getPosition().x);
        int centerRow = getRow(transform.getPosition().y);

        for (int i = -distance; i <= distance; i++) {
            for (int j = -distance; j <= distance; j++) {
                int neighborCol = centerCol + i;
                int neighborRow = centerRow + j;

                // 2. Aplicar Wrap Toroidal (Lógica específica de esta clase)
                neighborCol = (neighborCol % cols + cols) % cols;
                neighborRow = (neighborRow % rows + rows) % rows;

                // 3. Calcular ID y añadir
                int id = neighborCol + neighborRow * cols;
                nearby.addAll(cells[id]);
            }
        }
        return nearby;
    }

    @Override
    public void processNearby(TransformComponent transform, int distance, Consumer<GameObject> processor) {
        int centerCol = getCol(transform.getPosition().x);
        int centerRow = getRow(transform.getPosition().y);

        for (int i = -distance; i <= distance; i++) {
            for (int j = -distance; j <= distance; j++) {
                int neighborCol = centerCol + i;
                int neighborRow = centerRow + j;

                // 2. Aplicar Wrap Toroidal (Lógica específica de esta clase)
                neighborCol = (neighborCol % cols + cols) % cols;
                neighborRow = (neighborRow % rows + rows) % rows;

                // 3. Calcular ID y añadir
                int id = neighborCol + neighborRow * cols;
                ArrayList<GameObject> cell = cells[id];

                for (int k = 0; k < cell.size(); k++) {
                    processor.accept(cell.get(k));
                }
            }
        }
    }

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
