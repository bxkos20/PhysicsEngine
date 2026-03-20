package engine.graphics;

import backend.libgdx.render.shapes.ShapeRegistry;

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
 */
public abstract class Shape{
    /** Unique cache key for this shape configuration */
    protected String key;

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
}