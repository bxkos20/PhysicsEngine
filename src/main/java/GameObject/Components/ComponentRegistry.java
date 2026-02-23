package GameObject.Components;

import java.util.HashMap;
import java.util.Map;

public class ComponentRegistry {
    private static int nextBit = 0;
    private static final Map<Class<?>, Integer> bitMap = new HashMap<>();

    public static int getBit(Class<?> componentClass) {
        if (!bitMap.containsKey(componentClass)) {
            bitMap.put(componentClass, 1 << nextBit);
            nextBit++;
        }
        return bitMap.get(componentClass);
    }
}