package engine.graphics.shapes;

import backend.libgdx.render.rawDataMesh.RawDataMeshFactory;
import engine.graphics.Shape;

/**
 * Filled circle shape primitive.
 * Renders as a solid circle using triangle fan geometry.
 * 
 * @see Circle
 * @see RawDataMeshFactory#createCircle(float, int)
 */
public class CircleFilled extends Shape {
    /** Radius of the circle in world units */
    public float radius;
    
    /** Number of triangles to approximate the circle */
    public int quality;

    /**
     * Creates a filled circle shape.
     * 
     * @param radius  Radius of the circle in world units
     * @param quality Number of triangles (higher = smoother)
     */
    public CircleFilled(float radius, int quality) {
        this.radius = radius;
        this.quality = quality;
        this.inicializeKey();
    }
}