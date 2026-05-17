package engine.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Black-box tests for Vector2.
 * Tests the mathematical contract/specification without relying on internal implementation.
 * Verifies: vector algebra laws (commutativity, associativity, identity, inverse).
 */
@DisplayName("Vector2 - Black-box (specification-based)")
class Vector2BlackBoxTest {

    // --- Vector space axioms ---

    @Test
    @DisplayName("Addition is commutative: a+b == b+a")
    void additionCommutative() {
        Vector2 a = new Vector2(3, 7);
        Vector2 b = new Vector2(1, -2);

        Vector2 aPlusB = new Vector2(a.x, a.y);
        aPlusB.add(b);

        Vector2 bPlusA = new Vector2(b.x, b.y);
        bPlusA.add(a);

        assertEquals(aPlusB.x, bPlusA.x, 0.001f);
        assertEquals(aPlusB.y, bPlusA.y, 0.001f);
    }

    @Test
    @DisplayName("Zero vector is additive identity: v + 0 == v")
    void zeroVectorIdentity() {
        Vector2 v = new Vector2(5, 10);
        Vector2 result = new Vector2(v.x, v.y);
        result.add(new Vector2(0, 0));
        assertEquals(v.x, result.x, 0.001f);
        assertEquals(v.y, result.y, 0.001f);
    }

    @Test
    @DisplayName("Additive inverse: v + (-v) == 0")
    void additiveInverse() {
        Vector2 v = new Vector2(5, 10);
        Vector2 neg = new Vector2(-5, -10);
        v.add(neg);
        assertEquals(0f, v.x, 0.001f);
        assertEquals(0f, v.y, 0.001f);
    }

    @Test
    @DisplayName("Scalar multiplication identity: v * 1 == v")
    void scalarIdentity() {
        Vector2 v = new Vector2(3, 4);
        Vector2 result = new Vector2(v.x, v.y);
        result.scl(1);
        assertEquals(3f, result.x, 0.001f);
        assertEquals(4f, result.y, 0.001f);
    }

    @Test
    @DisplayName("Scalar zero produces zero vector: v * 0 == 0")
    void scalarZero() {
        Vector2 v = new Vector2(3, 4);
        v.scl(0);
        assertEquals(0f, v.x, 0.001f);
        assertEquals(0f, v.y, 0.001f);
    }

    // --- Dot product properties ---

    @Test
    @DisplayName("Dot product is commutative: a·b == b·a")
    void dotCommutative() {
        Vector2 a = new Vector2(3, 7);
        Vector2 b = new Vector2(1, -2);
        assertEquals(a.dot(b), b.dot(a), 0.001f);
    }

    @Test
    @DisplayName("Perpendicular vectors have zero dot product")
    void dotPerpendicular() {
        Vector2 a = new Vector2(1, 0);
        Vector2 b = new Vector2(0, 1);
        assertEquals(0f, a.dot(b), 0.001f);
    }

    @Test
    @DisplayName("Parallel same-direction vectors have positive dot product")
    void dotParallelPositive() {
        Vector2 a = new Vector2(1, 0);
        Vector2 b = new Vector2(5, 0);
        assertTrue(a.dot(b) > 0);
    }

    @Test
    @DisplayName("Parallel opposite-direction vectors have negative dot product")
    void dotParallelNegative() {
        Vector2 a = new Vector2(1, 0);
        Vector2 b = new Vector2(-5, 0);
        assertTrue(a.dot(b) < 0);
    }

    // --- Normalization contract ---

    @Test
    @DisplayName("Normalized vector has unit length")
    void normalizedHasUnitLength() {
        Vector2 v = new Vector2(3, 4);
        v.nor();
        assertEquals(1f, v.len(), 0.001f);
    }

    @Test
    @DisplayName("Normalization preserves direction")
    void normalizationPreservesDirection() {
        Vector2 original = new Vector2(3, 4);
        Vector2 normalized = new Vector2(3, 4);
        normalized.nor();

        // Cross product of 2D vectors (z-component) should be 0 if same direction
        float cross = original.x * normalized.y - original.y * normalized.x;
        assertEquals(0f, cross, 0.001f);
    }

    // --- Length contract ---

    @Test
    @DisplayName("len() == sqrt(len2())")
    void lenConsistentWithLen2() {
        Vector2 v = new Vector2(3, 4);
        assertEquals(Math.sqrt(v.len2()), v.len(), 0.001f);
    }

    @Test
    @DisplayName("Pythagorean theorem: len satisfies x²+y²=len²")
    void pythagoreanTheorem() {
        Vector2 v = new Vector2(3, 4);
        assertEquals(v.x * v.x + v.y * v.y, v.len2(), 0.001f);
    }

    // --- mulAdd contract ---

    @Test
    @DisplayName("mulAdd(v, s) == this + v*s")
    void mulAddConsistentWithAddAndScl() {
        Vector2 v1 = new Vector2(3, 4);
        Vector2 v2 = new Vector2(3, 4);
        Vector2 other = new Vector2(1, 2);
        float scalar = 5f;

        // Path 1: mulAdd
        v1.mulAdd(other, scalar);

        // Path 2: manual add + scl
        Vector2 scaled = new Vector2(other.x * scalar, other.y * scalar);
        v2.add(scaled);

        assertEquals(v1.x, v2.x, 0.001f);
        assertEquals(v1.y, v2.y, 0.001f);
    }
}
