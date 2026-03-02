package Render.Shapes;

import Render.Shapes.Shapes.Circle;
import Render.Shapes.Shapes.Rect;
import Render.Shapes.Shapes.Shape;
import com.badlogic.gdx.graphics.Mesh;

import java.util.HashMap;
import java.util.Map;

public class ShapeRegistry {
    private static int nextId = 0;
    private static final Map<Shape, Mesh> idMap = new HashMap<>();

    // 1. Obtener el ID secuencial (0, 1, 2, 3...) para el índice del Array
    public static Mesh getMesh(Shape shape) {
        if (!idMap.containsKey(shape)) {
            idMap.put(shape, createMesh(shape));
            nextId++;
        }
        return idMap.get(shape);
    }

    private static Mesh createMesh(Shape shape){ // Could be change by a shape.createMesh()
        if (shape instanceof Circle circle){
            return MeshFactory.createCircle(circle.getRadius(), circle.getQuality(), circle.getColor());
        } else if (shape instanceof Rect rect) {
            return MeshFactory.createRect(rect.getWidth(), rect.getHeight(), rect.getColor());
        }
        return null;
    }

}
