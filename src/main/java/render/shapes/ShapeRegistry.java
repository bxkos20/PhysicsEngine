package render.shapes;

import render.shapes.rawDataMesh.RawDataMesh;
import java.util.HashMap;
import java.util.Map;

/**
 * Caches the raw data of shapes (vertices and indices) so they are not
 * recalculated every frame. This is a CPU and memory optimization.
 */
public class ShapeRegistry {
    private static final Map<String, RawDataMesh> shapeCache = new HashMap<>();

    public static RawDataMesh getRawDataMesh(Shape shape) {
        String key = shape.getKey();
        if (!shapeCache.containsKey(key)) {
            // If the shape's raw data is not in the cache, create it and store it.
            shapeCache.put(key, shape.createRawDataMesh());
        }
        return shapeCache.get(key);
    }
}