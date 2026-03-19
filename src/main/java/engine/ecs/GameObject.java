package engine.ecs;

/**
 * Entity container in the Entity-Component-System architecture.
 * 
 * <p>A GameObject is essentially a unique ID with a component bitmask (signature)
 * and an array of attached components. Systems query entities by signature to
 * determine which ones they should process.</p>
 * 
 * <h3>Component Storage:</h3>
 * <p>Components are stored in a fixed-size array (max 32), indexed by component ID.
 * The signature bitmask tracks which components are present.</p>
 * 
 * @see ComponentRegistry
 * @see engine.ecs.systems.System
 */
public class GameObject {
    /** Counter for generating unique entity IDs */
    private static int nextId = 0;
    
    /** Unique identifier for this entity */
    private final int id;
    
    /** Bitmask indicating which components are attached */
    private long signature = 0L;

    /** Component storage array (max 32 components per entity) */
    private final Object[] components = new Object[32];

    /**
     * Creates a new entity with a unique ID.
     */
    public GameObject(){
        id = nextId++;
    }

    /**
     * Attaches a component to this entity.
     * Updates the entity's signature to include the component's bit.
     * 
     * @param component The component instance to attach
     * @param <T> Component type
     */
    public <T> void addComponent(T component){
        int compId = ComponentRegistry.getId(component.getClass());
        if (components[compId] != null) return; // Already has this component

        components[compId] = component;
        signature |= (1 << compId); // Update signature
    }

    /**
     * Retrieves a component by its registered ID.
     * 
     * @param componentId The component's registered ID
     * @param <T> Component type (unchecked cast)
     * @return The component instance, or null if not present
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponent(int componentId) {
        return (T) components[componentId];
    }

    /**
     * Checks if this entity has a specific component.
     * 
     * @param componentId The component's registered ID
     * @return true if component is present
     */
    public boolean hasComponent(int componentId) {
        return components[componentId] != null;
    }

    /**
     * Returns this entity's component signature.
     * Used by systems for entity filtering.
     * 
     * @return Bitmask of attached components
     */
    public long getSignature() { return signature; }

    /**
     * Checks if this entity's signature contains all required components.
     * 
     * @param otherSignature Bitmask of required components
     * @return true if entity has all components in the mask
     */
    public boolean checkSignature(long otherSignature){
        return (signature & otherSignature) == otherSignature;
    }

    /**
     * Returns this entity's unique identifier.
     * 
     * @return Entity ID
     */
    public int getId() { return id; }
}