package engine.math;

/**
 * 3D vector class for rendering and transformation calculations.
 * 
 * <p>Provides basic vector operations for 3D coordinates.
 * Used primarily for rendering vertex positions.</p>
 */
public class Vector3 {
    /** X component */
    public float x;
    
    /** Y component */
    public float y;
    
    /** Z component */
    public float z;
    
    /**
     * Creates a vector with specified components.
     * 
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Creates a zero vector.
     */
    public Vector3() {
        this(0, 0, 0);
    }
    
    /**
     * Sets the vector components.
     * 
     * @param x New X component
     * @param y New Y component
     * @param z New Z component
     * @return This vector for chaining
     */
    public Vector3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    
    /**
     * Copies values from another vector.
     * 
     * @param other Source vector
     * @return This vector for chaining
     */
    public Vector3 set(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }
}
