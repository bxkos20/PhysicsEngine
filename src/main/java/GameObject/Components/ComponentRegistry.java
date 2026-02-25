package GameObject.Components;

import java.util.HashMap;
import java.util.Map;

public class ComponentRegistry {
    private static int nextId = 0;
    private static final Map<Class<?>, Integer> idMap = new HashMap<>();

    // 1. Obtener el ID secuencial (0, 1, 2, 3...) para el índice del Array
    public static int getId(Class<?> componentClass) {
        if (!idMap.containsKey(componentClass)) {
            idMap.put(componentClass, nextId);
            nextId++;
        }
        return idMap.get(componentClass);
    }

    // 2. Obtener la máscara de bits (1, 2, 4, 8...) para la signature
    public static long getBit(Class<?> componentClass) {
        return 1L << getId(componentClass);
    }

    public static long idToBit(int id){
        return 1L << id;
    }
}