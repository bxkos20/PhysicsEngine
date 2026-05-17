package engine.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for Vector3.
 * Tests all constructors and method paths.
 */
class Vector3Test {

    @Test
    void constructorWithValues() {
        Vector3 v = new Vector3(1, 2, 3);
        assertEquals(1f, v.x, 0.001f);
        assertEquals(2f, v.y, 0.001f);
        assertEquals(3f, v.z, 0.001f);
    }

    @Test
    void defaultConstructorCreatesZero() {
        Vector3 v = new Vector3();
        assertEquals(0f, v.x, 0.001f);
        assertEquals(0f, v.y, 0.001f);
        assertEquals(0f, v.z, 0.001f);
    }

    @Test
    void setFloats() {
        Vector3 v = new Vector3();
        Vector3 returned = v.set(5, 6, 7);
        assertEquals(5f, v.x, 0.001f);
        assertEquals(6f, v.y, 0.001f);
        assertEquals(7f, v.z, 0.001f);
        assertSame(v, returned);
    }

    @Test
    void setFromOtherVector() {
        Vector3 v = new Vector3();
        Vector3 other = new Vector3(10, 20, 30);
        Vector3 returned = v.set(other);
        assertEquals(10f, v.x, 0.001f);
        assertEquals(20f, v.y, 0.001f);
        assertEquals(30f, v.z, 0.001f);
        assertSame(v, returned);
    }

    @Test
    void setNegativeValues() {
        Vector3 v = new Vector3();
        v.set(-1, -2, -3);
        assertEquals(-1f, v.x, 0.001f);
        assertEquals(-2f, v.y, 0.001f);
        assertEquals(-3f, v.z, 0.001f);
    }
}
