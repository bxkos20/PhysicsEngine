package engine.graphics.shapes;

import backend.libgdx.render.rawDataMesh.RawDataMeshFactory;
import engine.graphics.Shape;

/**
 * Outlined rectangle shape primitive.
 * Renders as a hollow rectangle with configurable line width.
 * 
 * @see RectFilled
 * @see RawDataMeshFactory#createRectOutline(float, float, float)
 */
public class Rect extends Shape {
    /** Width of the rectangle in world units */
    public float width;
    
    /** Height of the rectangle in world units */
    public float height;
    
    /** Width of the outline stroke */
    public float size;

    /**
     * Creates an outlined rectangle shape.
     * 
     * @param width  Width of the rectangle in world units
     * @param height Height of the rectangle in world units
     * @param size   Width of the outline stroke
     */
    public Rect(float width, float height, float size) {
        this.width = width;
        this.height = height;
        this.size = size;
        this.inicializeKey();
    }
}