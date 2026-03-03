package render.shapes;

import render.shapes.rawDataMesh.RawDataMesh;
import java.util.HashMap;
import java.util.Map;

/**
 * Caches the raw data of shapes (vertices and indices) so they are not
 * recalculated every frame. This is a CPU and memory optimization.
 */
public class ShapeRegistry {
    private static final Map<Shape, RawDataMesh> shapeCache = new HashMap<>();

    public static RawDataMesh getRawDataMesh(Shape shape) {
        if (!shapeCache.containsKey(shape)) {
            // If the shape's raw data is not in the cache, create it and store it.
            shapeCache.put(shape, shape.createRawDataMesh());
        }
        return shapeCache.get(shape);
    }
}