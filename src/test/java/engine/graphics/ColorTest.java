package engine.graphics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for Color.
 * Tests RGBA storage and toFloatBits conversion path.
 */
class ColorTest {

    @Test
    void constructorSetsRGBA() {
        Color c = new Color(0.5f, 0.25f, 0.75f, 1.0f);
        assertEquals(0.5f, c.r, 0.001f);
        assertEquals(0.25f, c.g, 0.001f);
        assertEquals(0.75f, c.b, 0.001f);
        assertEquals(1.0f, c.a, 0.001f);
    }

    @Test
    void toFloatBitsWhite() {
        Color white = new Color(1f, 1f, 1f, 1f);
        float bits = white.toFloatBits();
        // All channels max => 0xFFFFFFFF packed
        int unpacked = Float.floatToRawIntBits(bits);
        assertEquals(0xFFFFFFFF, unpacked);
    }

    @Test
    void toFloatBitsBlack() {
        Color black = new Color(0f, 0f, 0f, 1f);
        float bits = black.toFloatBits();
        int unpacked = Float.floatToRawIntBits(bits);
        // Only alpha channel set
        assertEquals(0xFF000000, unpacked);
    }

    @Test
    void toFloatBitsTransparent() {
        Color transparent = new Color(1f, 1f, 1f, 0f);
        float bits = transparent.toFloatBits();
        int unpacked = Float.floatToRawIntBits(bits);
        assertEquals(0x00FFFFFF, unpacked);
    }

    @Test
    void fieldsAreMutable() {
        Color c = new Color(0, 0, 0, 0);
        c.r = 0.5f;
        c.g = 0.5f;
        assertEquals(0.5f, c.r, 0.001f);
        assertEquals(0.5f, c.g, 0.001f);
    }
}
