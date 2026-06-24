package demos.dots.types;

import engine.graphics.Color;

/**
 * Enumeration of particle types in the simulation.
 * 
 * <p>Each type has a distinct color and interaction rules with other types.
 * Interactions are stored in a 2D matrix where positive values indicate
 * attraction and negative values indicate repulsion.</p>
 * 
 * <h3>Interaction System:</h3>
 * <ul>
 *   <li>Each particle type interacts differently with all other types</li>
 *   <li>Interactions are randomized at simulation start</li>
 *   <li>Values range from -1 (strong repulsion) to +1 (strong attraction)</li>
 * </ul>
 */
public enum DotType {
    /** Red particle */
    RED(new Color(1,0,0,1)),
    /** Green particle */
    GREEN(new Color(0,1,0,1)),
    /** Blue particle */
    BLUE(new Color(0,0,1,1)),
    /** Yellow particle */
    YELLOW(new Color(1,1,0,1)),
    /** Violet particle */
    VIOLET(new Color(1,0,1,1)),
    /** Cyan particle */
    CYAN(new Color(0,1,1,1)),
    /** Orange particle */
    ORANGE(new Color(1,0.5f,0,1)),
    /** Magenta particle (semi-transparent) */
    MAGENTA(new Color(1,0,1,0.5f)),
    /** Lime particle (semi-transparent) */
    LIME(new Color(0,1,0,0.5f)),
    /** Pink particle */
    PINK(new Color(1,0.5f,0.5f,1));

    /** Screen color for this particle type */
    public final Color COLOR;

    /**
     * Creates a particle type with the specified color.
     * Uses default min/max distances.
     * 
     * @param color Screen color
     */
    DotType(Color color) {
        COLOR = color;
    }
}
