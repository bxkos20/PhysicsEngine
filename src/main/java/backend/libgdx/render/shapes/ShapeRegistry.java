package backend.libgdx.render.shapes;

import backend.libgdx.render.rawDataMesh.RawDataMesh;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache for shape mesh data to avoid redundant geometry calculations.
 * 
 * <p>Uses shape parameter keys to store and retrieve {@link RawDataMesh}
 * instances. When a shape is first rendered, its mesh is created and cached.
 * Subsequent renders of identical shapes reuse the cached mesh.</p>
 * 
 * <p>This optimization significantly reduces CPU overhead when rendering
 * thousands of identical shapes (e.g., particles).</p>
 */
public class ShapeRegistry {
    /** Cache storage: key -> RawDataMesh */
    private static final Map<String, RawDataMesh> shapeCache = new HashMap<>();

    /**
     * Retrieves or creates mesh data for a shape.
     * If the shape is not cached, creates and stores its mesh data.
     * 
     * @param shape The shape to get mesh data for
     * @return Cached or newly created RawDataMesh
     */
    public static RawDataMesh getRawDataMesh(Shape shape) {
        String key = shape.getKey();
        if (!shapeCache.containsKey(key)) {
            // If the shape's raw data is not in the cache, create it and store it.
            shapeCache.put(key, shape.createRawDataMesh());
        }
        return shapeCache.get(key);
    }
}