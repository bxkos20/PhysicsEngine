package engine.graphics.interfaces;

/**
 * Interface for renderable shape abstraction.
 * 
 * <p>Provides type identification and parameter serialization for shapes.
 * Implementations store shape-specific geometry parameters.</p>
 */
public interface IShape {
    /**
     * Returns the shape type identifier.
     * Used by rendering systems to recreate shapes from serialized data.
     * 
     * @return Shape type string (e.g., "circle", "rect")
     */
    String getType();
    
    /**
     * Returns shape parameters for serialization.
     * Interpretation depends on shape type.
     * 
     * @return Array of float parameters
     */
    float[] getParameters();
}