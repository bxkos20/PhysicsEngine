package engine.ecs;

import java.util.HashMap;
import java.util.Map;

public class ComponentRegistry {
    private static int nextId = 0;
    private static final Map<Class<?>, Integer> idMap = new HashMap<>();

    // Thread-safe component ID assignment
    private static final Object lock = new Object();

    // 1. Get sequential ID for component class
    public static synchronized int getId(Class<?> componentClass) {
        if (!idMap.containsKey(componentClass)) {
            idMap.put(componentClass, nextId);
            nextId++;
        }
        return idMap.get(componentClass);
    }

    // 2. Get bit mask for component signature
    public static long getBit(Class<?> componentClass) {
        return 1L << getId(componentClass);
    }

    // 3. Convert ID to bit mask
    public static long idToBit(int id){
        return 1L << id;
    }
    
    // 4. Get all registered components (for debugging)
    public static synchronized Map<Class<?>, Integer> getAllComponents() {
        return new HashMap<>(idMap);
    }
}