package demos.dots.types;

import engine.util.ThreadLocalRandom;

/**
 * Manages the interaction rules for a specific Dot simulation instance.
 * Each simulation will have its own instance of this manager, allowing for
 * different interaction matrices per simulation.
 */
public class DotInteractionManager {

    private final float[][] interactionMatrix;
    private final int numDotTypes;

    /**
     * Creates an interaction manager based on the provided settings.
     *
     * @param numDotTypes The settings for this specific dot simulation.
     */
    public DotInteractionManager(int numDotTypes) {
        this.numDotTypes = numDotTypes;
        this.interactionMatrix = new float[this.numDotTypes][this.numDotTypes];
        randomizeInteractions();
    }

    private void randomizeInteractions() {
        for (int i = 0; i < numDotTypes; i++) {
            for (int j = 0; j < numDotTypes; j++) {
                interactionMatrix[i][j] = ThreadLocalRandom.nextFloat() * 2 - 1;
            }
        }
    }

    /**
     * Gets the interaction force between two dot types.
     * @param type1 The first dot type.
     * @param type2 The second dot type.
     * @return The interaction multiplier.
     */
    public float getInteraction(DotType type1, DotType type2) {
        // Usamos ordinal() como antes, pero ahora sobre nuestra matriz de instancia
        if (type1.ordinal() >= numDotTypes || type2.ordinal() >= numDotTypes) {
            // Manejar el caso en que se usa un tipo de punto no configurado para esta simulación
            return 0;
        }
        return interactionMatrix[type1.ordinal()][type2.ordinal()];
    }
}