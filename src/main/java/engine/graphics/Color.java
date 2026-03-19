package engine.graphics;

import com.badlogic.gdx.utils.NumberUtils;

/**
 * RGBA color representation for rendering.
 * 
 * <p>Components are stored as floats in range [0, 1].
 * Used for entity coloring and rendering operations.</p>
 */
public class Color {
    /** Red component [0, 1] */
    public float r;
    
    /** Green component [0, 1] */
    public float g;
    
    /** Blue component [0, 1] */
    public float b;
    
    /** Alpha (opacity) component [0, 1] */
    public float a;

    /**
     * Creates a color with specified RGBA values.
     * 
     * @param r Red [0, 1]
     * @param g Green [0, 1]
     * @param b Blue [0, 1]
     * @param a Alpha [0, 1]
     */
    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public float toFloatBits() {
        int color = ((int)(255 * a) << 24) | ((int)(255 * b) << 16) | ((int)(255 * g) << 8) | ((int)(255 * r));
        return Float.intBitsToFloat(color);
    }
}
