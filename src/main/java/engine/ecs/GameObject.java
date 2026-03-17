package engine.ecs;

public class GameObject {
    private static int nextId = 0;
    private final int id;
    private long signature = 0L;

    // Máximo 32 componentes por defecto (el tamaño de los bits de un 'int')
    private final Object[] components = new Object[32];

    public GameObject(){
        id = nextId++;
    }

    public <T> void addComponent(T component){
        int compId = ComponentRegistry.getId(component.getClass());
        if (components[compId] != null) return; // Ya tiene este componente

        components[compId] = component;
        signature |= (1 << compId); // Actualizamos la firma
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(int componentId) {
        return (T) components[componentId];
    }

    public boolean hasComponent(int componentId) {
        return components[componentId] != null;
    }

    public long getSignature() { return signature; }

    public boolean checkSignature(long otherSignature){
        return (signature & otherSignature) == otherSignature;
    }

    public int getId() { return id; }
}