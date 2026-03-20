package engine.graphics.shapes;

import backend.libgdx.render.rawDataMesh.RawDataMeshFactory;
import engine.graphics.Shape;

/**
 * Outlined circle (ring) shape primitive.
 * Renders as a hollow circle with configurable line width.
 * 
 * @see CircleFilled
 * @see RawDataMeshFactory#createCircleOutline(float, int, float)
 */
public class Circle extends Shape {
    /**
     * Radius of the circle in world units
     */
    public float radius;

    /**
     * Number of line segments to approximate the circle
     */
    public int quality;

    /**
     * Width of the outline stroke
     */
    public float size;

    /**
     * Creates an outlined circle shape.
     *
     * @param radius  Radius of the circle in world units
     * @param quality Number of line segments (higher = smoother)
     * @param size    Width of the outline stroke
     */
    public Circle(float radius, int quality, float size) {
        this.radius = radius;
        this.quality = quality;
        this.size = size;
        this.inicializeKey();
    }
}