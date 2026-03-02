package render.shapes;

import com.badlogic.gdx.graphics.Mesh;

import java.util.HashMap;
import java.util.Map;

public class ShapeRegistry {
    private static final Map<Shape, Mesh> shapeMap = new HashMap<>();

    // 1. Obtener el ID secuencial (0, 1, 2, 3...) para el índice del Array
    public static Mesh getMesh(Shape shape) {
        if (!shapeMap.containsKey(shape)) {
            shapeMap.put(shape, createMesh(shape));
        }
        return shapeMap.get(shape);
    }
}
