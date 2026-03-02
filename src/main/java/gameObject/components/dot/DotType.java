package gameObject.components.dot;

import com.badlogic.gdx.graphics.Color;

import java.util.Random;

public enum DotType {
    RED(Color.RED),
    BlUE(Color.BLUE),
    LIME(Color.LIME),
    PINK(Color.PINK),
    CYAN(Color.CYAN),
    WHITE(Color.WHITE),
    YELLOW(Color.YELLOW),
    BROWN(Color.BROWN),
    SLATE(Color.SLATE),
    FIREBRICK(Color.FIREBRICK);


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
        Random random = new Random();
        for (int i = 0; i < INTERACTION.length; i++) {
            for (int j = 0; j < INTERACTION[i].length; j++) {
                INTERACTION[i][j] = random.nextFloat() * 2 - 1;
            }
        }
    }

    public float getInteraction(DotType other) {
        return INTERACTION[this.ordinal()][other.ordinal()];
    }
}
