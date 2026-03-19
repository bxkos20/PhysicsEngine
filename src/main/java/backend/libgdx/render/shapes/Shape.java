package backend.libgdx.render.shapes;

import backend.libgdx.render.rawDataMesh.RawDataMesh;
import engine.graphics.interfaces.IShape;

import java.lang.reflect.Field;

/**
 * Abstract base class for all renderable shapes.
 * 
 * <p>Provides caching via unique keys based on shape parameters.
 * Subclasses implement {@link #createRawDataMesh()} to define geometry.</p>
 * 
 * <h3>Caching Strategy:</h3>
 * <p>Shapes with identical parameters share the same mesh data via
 * {@link ShapeRegistry}, avoiding redundant geometry calculations.</p>
 * 
 * @see ShapeRegistry
 * @see IShape
 */
public abstract class Shape implements IShape {
    /** Unique cache key for this shape configuration */
    protected String key;

    /**
     * Creates the raw mesh data for this shape.
     * Called once per unique shape configuration.
     * 
     * @return RawMesh containing vertices and indices
     */
    public abstract RawDataMesh createRawDataMesh();

    /**
     * Returns the unique cache key for this shape.
     * Format: "CLASSNAME[param1:(value),param2:(value),...]"
     * 
     * @return Cache key string
     */
    public String getKey(){
        return key;
    }

    /**
     * Generates and sets the cache key using reflection.
     * Should be called in subclass constructor.
     */
    public void inicializeKey(){
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName().toUpperCase());
        sb.append("[");
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getName().equals("key")) continue;
            try {
                field.setAccessible(true);
                sb.append(field.getName())
                        .append(":(")
                        .append(field.get(this))
                        .append("),");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        this.key = sb.toString();
    }

    /**
     * Returns the shape type name (simple class name).
     * 
     * @return Shape type identifier
     */
    public String getType(){
        return this.getClass().getSimpleName();
    }

    /**
     * Returns shape parameters as float array.
     * Override in subclasses for serialization.
     * 
     * @return Array of shape parameters
     */
    public float[] getParameters(){
        return new float[0]; // Default empty implementation
    }
    
    /**
     * Utility method to safely get field values using reflection.
     * 
     * @return Array of float values from all non-key fields
     */
    protected float[] getShapeParameters() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            java.util.List<Float> params = new java.util.ArrayList<>();
            
            for (Field field : fields) {
                if (field.getName().equals("key")) continue;
                field.setAccessible(true);
                Object value = field.get(this);
                if (value instanceof Float) {
                    params.add((Float) value);
                } else if (value instanceof Integer) {
                    params.add(((Integer) value).floatValue());
                }
            }
            
            float[] result = new float[params.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = params.get(i);
            }
            return result;
        } catch (IllegalAccessException e) {
            // Log error but don't crash
            System.err.println("Error accessing shape parameters: " + e.getMessage());
            return new float[0];
        }
    }
}