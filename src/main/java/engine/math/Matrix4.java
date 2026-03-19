package engine.math;

/**
 * Wrapper for 4x4 transformation matrices.
 * 
 * <p>This is a placeholder implementation that wraps a native matrix object
 * (typically LibGDX's Matrix4). Provides type abstraction for the engine
 * without direct backend dependency.</p>
 * 
 * <p>Future implementation could include matrix operations like
 * multiplication, translation, rotation, and projection.</p>
 */
public class Matrix4 {
    /** The underlying native matrix (e.g., LibGDX Matrix4) */
    private Object nativeMatrix;
    
    /**
     * Creates a matrix wrapper around a native matrix object.
     * 
     * @param nativeMatrix Backend-specific matrix object
     */
    public Matrix4(Object nativeMatrix) {
        this.nativeMatrix = nativeMatrix;
    }
    
    /**
     * Returns the underlying native matrix for backend operations.
     * 
     * @return The wrapped matrix object
     */
    public Object getNativeMatrix() {
        return nativeMatrix;
    }
}
