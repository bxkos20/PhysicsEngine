package engine.math;

public class Matrix4 {
    // This is a placeholder - in real implementation you'd have matrix operations
    // For now, we'll use this as a wrapper type
    
    private Object nativeMatrix; // Will hold LibGDX Matrix4 in implementation
    
    public Matrix4(Object nativeMatrix) {
        this.nativeMatrix = nativeMatrix;
    }
    
    public Object getNativeMatrix() {
        return nativeMatrix;
    }
}
