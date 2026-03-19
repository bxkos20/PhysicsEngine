package engine.math;

/**
 * 2D vector class for physics and rendering calculations.
 * 
 * <p>Provides common vector operations like addition, subtraction,
 * scaling, normalization, and dot product. All operations modify
 * the vector in-place and return `this` for method chaining.</p>
 * 
 * <p>Compatible with LibGDX Vector2 API subset.</p>
 */
public class Vector2 {
    /** X component */
    public float x;
    
    /** Y component */
    public float y;

    /**
     * Creates a vector with specified components.
     * 
     * @param x X component
     * @param y Y component
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a zero vector.
     */
    public Vector2() {
        this(0, 0);
    }

    /**
     * Sets the vector components.
     * 
     * @param x New X component
     * @param y New Y component
     * @return This vector for chaining
     */
    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Adds another vector to this one.
     * 
     * @param other Vector to add
     * @return This vector for chaining
     */
    public Vector2 add(Vector2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    /**
     * Adds scalar values to components.
     * 
     * @param x X offset
     * @param y Y offset
     * @return This vector for chaining
     */
    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /**
     * Adds a scaled vector: this += vec * scalar.
     * Useful for velocity updates: position += velocity * dt.
     * 
     * @param vec    Vector to scale and add
     * @param scalar Scale factor
     * @return This vector for chaining
     */
    public Vector2 mulAdd(Vector2 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        return this;
    }

    /**
     * Subtracts another vector from this one.
     * 
     * @param other Vector to subtract
     * @return This vector for chaining
     */
    public Vector2 sub(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    /**
     * Returns the vector length (magnitude).
     * 
     * @return sqrt(x^2 + y^2)
     */
    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Returns the squared length.
     * Faster than len() as it avoids sqrt.
     * 
     * @return x^2 + y^2
     */
    public float len2() {
        return x * x + y * y;
    }

    /**
     * Normalizes this vector to unit length.
     * Does nothing if length is zero.
     * 
     * @return This vector for chaining
     */
    public Vector2 nor() {
        float len = len();
        if (len != 0) {
            this.x /= len;
            this.y /= len;
        }
        return this;
    }

    /**
     * Computes the dot product with another vector.
     * 
     * @param other Other vector
     * @return x1*x2 + y1*y2
     */
    public float dot(Vector2 other) {
        return x * other.x + y * other.y;
    }

    /**
     * Scales this vector by a scalar.
     * 
     * @param f Scale factor
     * @return This vector for chaining
     */
    public Vector2 scl(float f) {
        this.x *= f;
        this.y *= f;
        return this;
    }

    /**
     * Copies values from another vector.
     * 
     * @param vector Source vector
     * @return This vector for chaining
     */
    public Vector2 set(Vector2 vector) {
        this.x = vector.x;
        this.y = vector.y;
        return this;
    }
}
