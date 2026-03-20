package engine.graphics.shapes;

import backend.libgdx.render.rawDataMesh.RawDataMeshFactory;
import engine.graphics.Shape;

/**
 * Filled rectangle shape primitive.
 * Renders as a solid quad using two triangles.
 * 
 * @see Rect
 * @see RawDataMeshFactory#createRect(float, float)
 */
public class RectFilled extends Shape {
    /** Width of the rectangle in world units */
    public float width;
    
    /** Height of the rectangle in world units */
    public float height;

    /**
     * Creates a filled rectangle shape.
     * 
     * @param width  Width of the rectangle in world units
     * @param height Height of the rectangle in world units
     */
    public RectFilled(float width, float height) {
        this.width = width;
        this.height = height;
        this.inicializeKey();
    }
}