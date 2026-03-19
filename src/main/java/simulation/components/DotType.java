package simulation.components;

import engine.config.SimulationConfig;
import engine.graphics.Color;
import engine.util.ThreadLocalRandom;

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

    /** Number of distinct particle types */
    public static final int DOT_TYPES = SimulationConfig.Simulation.DOT_TYPES;

    /** Interaction matrix: [this.ordinal()][other.ordinal()] = force multiplier */
    public static final float[][] INTERACTION = new float[DOT_TYPES][DOT_TYPES];

    /** Display color for this particle type */
    public final Color COLOR;

    /**
     * Creates a particle type with the specified color.
     * Uses default min/max distances.
     * 
     * @param color Display color
     */
    DotType(Color color) {
        COLOR = color;
    }

    /**
     * Randomizes all interaction values between particle types.
     * Should be called once at simulation initialization.
     */
    public static void randomizeInteraction() {
        for (int i = 0; i < DOT_TYPES; i++) {
            for (int j = 0; j < DOT_TYPES; j++) {
                INTERACTION[i][j] = ThreadLocalRandom.nextFloat() * 2 - 1;
            }
        }
        printInteraction();
    }

    /**
     * Prints the interaction matrix to the console.
     */
    public static void printInteraction(){
        System.out.println("\nInteraction Matrix: ");
        for (int i = 0; i < DOT_TYPES; i++) {
            System.out.print(DotType.values()[i].name().substring(0,3) + ": ");
            for (int j = 0; j < DOT_TYPES; j++) {
                System.out.printf((INTERACTION[i][j] > 0)?
                                "[" + DotType.values()[j].name().charAt(0) + "  %4.2f ]" :
                                "[" + DotType.values()[j].name().charAt(0) + " %4.2f ]",
                        INTERACTION[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Returns the interaction force multiplier with another particle type.
     * 
     * @param other The other particle type
     * @return Force multiplier [-1, 1]: negative = repulsion, positive = attraction
     */
    public float getInteraction(DotType other) {
        return INTERACTION[this.ordinal()][other.ordinal()];
    }
}
