package demos.dots.components;

/**
 * Component identifying a particle's type in the simulation.
 * 
 * <p>Used by {@link demos.dots.systems.DotSystem} to determine
 * interaction forces between different particle types.</p>
 * 
 * @see DotType
 */
public class DotComponent {
    /** The type/category of this particle */
    private final DotType dotType;

    /**
     * Creates a dot component with the specified type.
     * 
     * @param dotType The particle type
     */
    public DotComponent(DotType dotType) {
        this.dotType = dotType;
    }

    /**
     * Returns the particle type.
     * 
     * @return The DotType of this particle
     */
    public DotType getDotType() {
        return dotType;
    }
}
