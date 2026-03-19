package backend.libgdx.render.primitives;

import backend.libgdx.render.rawDataMesh.RawDataMesh;
import backend.libgdx.render.rawDataMesh.RawDataMeshFactory;
import backend.libgdx.render.shapes.Shape;

/**
 * Filled rectangle shape primitive.
 * Renders as a solid quad using two triangles.
 * 
 * @see Rect
 * @see RawDataMeshFactory#createRect(float, float)
 */
public class RectFilled extends Shape {
    /** Width of the rectangle in world units */
    private float width;
    
    /** Height of the rectangle in world units */
    private float height;

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

    /**
     * Returns the rectangle's width.
     * 
     * @return Width in world units
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the rectangle's width.
     * 
     * @param width New width in world units
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Returns the rectangle's height.
     * 
     * @return Height in world units
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the rectangle's height.
     * 
     * @param height New height in world units
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Creates the raw mesh data for this filled rectangle.
     * 
     * @return RawDataMesh containing vertices and indices
     */
    @Override
    public RawDataMesh createRawDataMesh() {
            return RawDataMeshFactory.createRect(width, height);
    }
    
    /**
     * Returns the shape parameters for serialization.
     * 
     * @return Array [width, height]
     */
    @Override
    public float[] getParameters() {
        return new float[]{width, height};
    }


}