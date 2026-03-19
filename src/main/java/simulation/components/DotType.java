package simulation.components;

import engine.graphics.Color;
import engine.util.ThreadLocalRandom;

public enum DotType {
    RED(new Color(1,0,0,1)),
    GREEN(new Color(0,1,0,1)),
    BLUE(new Color(0,0,1,1)),
    YELLOW(new Color(1,1,0,1)),
    PURPLE(new Color(1,0,1,1)),
    CYAN(new Color(0,1,1,1)),
    ORANGE(new Color(1,0.5f,0,1)),
    MAGENTA(new Color(1,0,1,0.5f)),
    LIME(new Color(0,1,0,0.5f)),
    PINK(new Color(1,0.5f,0.5f,1));


    public static final float[][] INTERACTION = new float[DotType.values().length][DotType.values().length];
    public final Color COLOR;
    public final float MIN_DISTANCE;
    public final float MAX_DISTANCE;


    DotType(Color color) {
        COLOR = color;
        MIN_DISTANCE = 25;
        MAX_DISTANCE = 75;
    }

    public static void randomizeInteraction() {
        for (int i = 0; i < INTERACTION.length; i++) {
            for (int j = 0; j < INTERACTION[i].length; j++) {
                INTERACTION[i][j] = ThreadLocalRandom.nextFloat() * 2 - 1;
            }
        }
    }

    public float getInteraction(DotType other) {
        return INTERACTION[this.ordinal()][other.ordinal()];
    }
}
