package engine.ecs;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for ECS component type IDs and bit masks.
 * 
 * <p>Assigns unique IDs to component classes and provides methods to convert
 * between IDs and bit masks for efficient entity signature operations.</p>
 * 
 * <h3>Usage:</h3>
 * <pre>
 * int id = ComponentRegistry.getId(TransformComponent.class);
 * long bit = ComponentRegistry.getBit(TransformComponent.class);
 * </pre>
 * 
 * @see GameObject
 */
public class ComponentRegistry {
    /** Next available component ID */
    private static int nextId = 0;
    
    /** Map from component class to assigned ID */
    private static final Map<Class<?>, Integer> idMap = new HashMap<>();

    /** Lock for thread-safe ID assignment */
    private static final Object lock = new Object();

    /**
     * Gets or assigns a unique ID for a component class.
     * Thread-safe - uses synchronized access.
     * 
     * @param componentClass The component class to get ID for
     * @return Unique integer ID (0, 1, 2, ...)
     */
    public static synchronized int getId(Class<?> componentClass) {
        if (!idMap.containsKey(componentClass)) {
            idMap.put(componentClass, nextId);
            nextId++;
        }
        return idMap.get(componentClass);
    }

    /**
     * Gets the bit mask for a component class.
     * Equivalent to {@code 1L << getId(componentClass)}.
     * 
     * @param componentClass The component class
     * @return Bit mask with single bit set at component's ID position
     */
    public static long getBit(Class<?> componentClass) {
        return 1L << getId(componentClass);
    }

    /**
     * Converts a component ID to its bit mask.
     * 
     * @param id Component ID from {@link #getId(Class)}
     * @return Bit mask with single bit set at ID position
     */
    public static long idToBit(int id){
        return 1L << id;
    }
    
    /**
     * Returns a copy of all registered component mappings.
     * Useful for debugging and inspection.
     * 
     * @return Map of component class to ID
     */
    public static synchronized Map<Class<?>, Integer> getAllComponents() {
        return new HashMap<>(idMap);
    }
}