package engine.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for Vector2.
 * Tests all internal code paths: constructors, arithmetic, edge cases.
 */
class Vector2Test {

    private Vector2 v;

    @BeforeEach
    void setUp() {
        v = new Vector2(3, 4);
    }

    // --- Constructor tests ---

    @Test
    void constructorWithValues() {
        assertEquals(3f, v.x, 0.001f);
        assertEquals(4f, v.y, 0.001f);
    }

    @Test
    void defaultConstructorCreatesZero() {
        Vector2 zero = new Vector2();
        assertEquals(0f, zero.x, 0.001f);
        assertEquals(0f, zero.y, 0.001f);
    }

    // --- set() paths ---

    @Test
    void setFloats() {
        v.set(10, 20);
        assertEquals(10f, v.x, 0.001f);
        assertEquals(20f, v.y, 0.001f);
    }

    @Test
    void setReturnsThis() {
        Vector2 returned = v.set(1, 2);
        assertSame(v, returned);
    }

    @Test
    void setFromOtherVector() {
        Vector2 other = new Vector2(7, 8);
        v.set(other);
        assertEquals(7f, v.x, 0.001f);
        assertEquals(8f, v.y, 0.001f);
    }

    // --- add() paths ---

    @Test
    void addVector() {
        v.add(new Vector2(1, 2));
        assertEquals(4f, v.x, 0.001f);
        assertEquals(6f, v.y, 0.001f);
    }

    @Test
    void addVectorReturnsThis() {
        Vector2 returned = v.add(new Vector2(0, 0));
        assertSame(v, returned);
    }

    @Test
    void addScalars() {
        v.add(10, 20);
        assertEquals(13f, v.x, 0.001f);
        assertEquals(24f, v.y, 0.001f);
    }

    @Test
    void addNegativeValues() {
        v.add(-3, -4);
        assertEquals(0f, v.x, 0.001f);
        assertEquals(0f, v.y, 0.001f);
    }

    // --- mulAdd() path ---

    @Test
    void mulAdd() {
        v.mulAdd(new Vector2(2, 3), 5);
        assertEquals(3 + 2 * 5, v.x, 0.001f);
        assertEquals(4 + 3 * 5, v.y, 0.001f);
    }

    @Test
    void mulAddWithZeroScalar() {
        v.mulAdd(new Vector2(100, 100), 0);
        assertEquals(3f, v.x, 0.001f);
        assertEquals(4f, v.y, 0.001f);
    }

    @Test
    void mulAddWithNegativeScalar() {
        v.mulAdd(new Vector2(1, 1), -1);
        assertEquals(2f, v.x, 0.001f);
        assertEquals(3f, v.y, 0.001f);
    }

    // --- sub() path ---

    @Test
    void subVector() {
        v.sub(new Vector2(1, 1));
        assertEquals(2f, v.x, 0.001f);
        assertEquals(3f, v.y, 0.001f);
    }

    @Test
    void subReturnsThis() {
        assertSame(v, v.sub(new Vector2(0, 0)));
    }

    // --- len() and len2() paths ---

    @Test
    void lenReturnsMagnitude() {
        // 3-4-5 triangle
        assertEquals(5f, v.len(), 0.001f);
    }

    @Test
    void len2ReturnsSquaredMagnitude() {
        assertEquals(25f, v.len2(), 0.001f);
    }

    @Test
    void lenOfZeroVector() {
        Vector2 zero = new Vector2();
        assertEquals(0f, zero.len(), 0.001f);
    }

    // --- nor() paths ---

    @Test
    void norNormalizesVector() {
        v.nor();
        assertEquals(0.6f, v.x, 0.001f);
        assertEquals(0.8f, v.y, 0.001f);
        assertEquals(1f, v.len(), 0.001f);
    }

    @Test
    void norDoesNothingOnZeroVector() {
        Vector2 zero = new Vector2();
        zero.nor();
        assertEquals(0f, zero.x, 0.001f);
        assertEquals(0f, zero.y, 0.001f);
    }

    @Test
    void norReturnsThis() {
        assertSame(v, v.nor());
    }

    // --- dot() path ---

    @Test
    void dotProduct() {
        Vector2 other = new Vector2(1, 0);
        assertEquals(3f, v.dot(other), 0.001f);
    }

    @Test
    void dotProductPerpendicular() {
        Vector2 other = new Vector2(-4, 3); // perpendicular to (3,4)
        assertEquals(0f, v.dot(other), 0.001f);
    }

    @Test
    void dotProductWithZero() {
        assertEquals(0f, v.dot(new Vector2()), 0.001f);
    }

    // --- scl() path ---

    @Test
    void sclPositive() {
        v.scl(2);
        assertEquals(6f, v.x, 0.001f);
        assertEquals(8f, v.y, 0.001f);
    }

    @Test
    void sclNegative() {
        v.scl(-1);
        assertEquals(-3f, v.x, 0.001f);
        assertEquals(-4f, v.y, 0.001f);
    }

    @Test
    void sclZero() {
        v.scl(0);
        assertEquals(0f, v.x, 0.001f);
        assertEquals(0f, v.y, 0.001f);
    }

    @Test
    void sclReturnsThis() {
        assertSame(v, v.scl(1));
    }

    // --- Chaining test (white-box: verify all methods return this) ---

    @Test
    void methodChaining() {
        Vector2 result = new Vector2().set(1, 1).add(2, 2).scl(3).nor();
        assertEquals(1f, result.len(), 0.001f);
    }
}
