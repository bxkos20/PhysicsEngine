package GameObject;

import java.util.HashMap;
import java.util.Map;

public class GameObject {
    private static int nextId = 0;
    private final int id;
    private int signature;
    Map<Class<?>, Object> components = new HashMap<>();

    public GameObject(){
        id = nextId;
        nextId++;
    }

    public <T> void addComponent(T component){
        if (components.containsKey(component.getClass()))
            return;
        components.put(component.getClass(), component);
        signature |= ComponentRegistry.getBit(component.getClass());
    }

    public <T> T getComponent (Class<T> type){
        return type.cast(components.get(type));
    }

    public boolean hasComponent (Class<?> type){
        return components.containsKey(type);
    }

    public int getSignature() {
        return signature;
    }
}
