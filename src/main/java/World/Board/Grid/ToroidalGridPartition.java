package World.Board.Grid;

import GameObject.GameObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ToroidalGridPartition extends GridPartition{
    private ArrayList<GameObject>[] cells;


    public ToroidalGridPartition(float width, float height, float cellSize) {
        super(width, height, cellSize);
        cells = new ArrayList[rows * cols];
        for(int i=0; i < cells.length; i++) cells[i] = new ArrayList<>();
    }

    @Override
    public void clear() {
        for (int i = 0; i < cells.length; i++) cells[i].clear();
    }

    @Override
    protected int getCol(float x) {
        int col = (int) Math.floor(x / cellSize);
        if (col < 0) col += cols;
        else if (col >= cols) col -= cols;
        return col;
    }

    @Override
    protected int getRow(float y) {
        int row = (int) Math.floor(y / cellSize);
        if (row < 0) row += rows;
        else if (row >= rows) row -= rows;
        return row;
    }

    @Override
    public ArrayList<GameObject> getNearby(GameObject obj, int distance) {
        ArrayList<GameObject> nearby = new ArrayList<>();
        int centerCol = getCol(obj.transform.position.x);
        int centerRow = getRow(obj.transform.position.y);

        for (int i = -distance; i <= distance; i++){
            for (int j = -distance; j <= distance; j++) {
                int neighborCol = centerCol + i;
                int neighborRow = centerRow + j;

                // 2. Aplicar Wrap Toroidal (Lógica específica de esta clase)
                if (neighborCol < 0) neighborCol += cols;
                else if (neighborCol >= cols) neighborCol -= cols;

                if (neighborRow < 0) neighborRow += rows;
                else if (neighborRow >= rows) neighborRow -= rows;

                // 3. Calcular ID y añadir
                int id = neighborCol + neighborRow * cols;

                nearby.addAll(cells[id]);
            }
        }
        return nearby;
    }

    @Override
    public void add(GameObject obj) {
        int id = getCol(obj.transform.position.x)+ getRow(obj.transform.position.y) * cols;
        cells[id].add(obj);
    }
}
