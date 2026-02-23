package World.Board.Grid;

import GameObject.Components.Core.TransformComponent;
import GameObject.GameObject;

import java.util.ArrayList;
import java.util.List;

public abstract class GridPartition {
    protected final int rows;
    protected final int cols;
    protected final float cellSize;
    protected final float width;
    protected final float height;

    public GridPartition(float width, float height, float cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.cols = (int) Math.ceil(width / cellSize);
        this.rows = (int) Math.ceil(height / cellSize);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public float getCellSize() {
        return cellSize;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public abstract void clear();

    public abstract void add(GameObject gameObject);

    public abstract void add(List<GameObject> gameObjects);

    public abstract ArrayList<GameObject> getNearby(TransformComponent transform, int distance);

    protected abstract int getCol(float x);

    protected abstract int getRow(float y);

}
