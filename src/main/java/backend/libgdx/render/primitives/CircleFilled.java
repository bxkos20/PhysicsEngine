package backend.libgdx.render.primitives;

import backend.libgdx.render.rawDataMesh.RawDataMesh;
import backend.libgdx.render.rawDataMesh.RawDataMeshFactory;
import backend.libgdx.render.shapes.Shape;

/**
 * Filled circle shape primitive.
 * Renders as a solid circle using triangle fan geometry.
 * 
 * @see Circle
 * @see RawDataMeshFactory#createCircle(float, int)
 */
public class CircleFilled extends Shape {
    /** Radius of the circle in world units */
    private float radius;
    
    /** Number of triangles to approximate the circle */
    private int quality;

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

    /**
     * Returns the circle's radius.
     * 
     * @return Radius in world units
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets the circle's radius.
     * 
     * @param radius New radius in world units
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * Returns the circle's triangle count.
     * 
     * @return Number of triangles
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Sets the circle's triangle count.
     * 
     * @param quality New triangle count
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    /**
     * Creates the raw mesh data for this filled circle.
     * 
     * @return RawDataMesh containing vertices and indices
     */
    @Override
    public RawDataMesh createRawDataMesh() {
        return RawDataMeshFactory.createCircle(radius, quality);
    }
    
    /**
     * Returns the shape parameters for serialization.
     * 
     * @return Array [radius, quality]
     */
    @Override
    public float[] getParameters() {
        return new float[]{radius, quality};
    }
}