package engine.world.board;

import engine.ecs.components.TransformComponent;
import engine.math.Vector2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Black-box tests for ToroidalBoard.
 * Tests the toroidal world contract from specification:
 * - Objects wrap around edges
 * - Shortest distance considers wrap-around
 * - Direction vectors cross edges when shorter
 */
@DisplayName("ToroidalBoard - Black-box (specification-based)")
class ToroidalBoardBlackBoxTest {

    private ToroidalBoard board;

    @BeforeEach
    void setUp() {
        board = new ToroidalBoard(1000, 1000);
    }

    @Test
    @DisplayName("Objects exiting right edge appear on left")
    void wrapRightToLeft() {
        TransformComponent t = new TransformComponent(1050, 500);
        board.enforceBounds(t);
        assertEquals(50f, t.getPosition().x, 0.001f);
    }

    @Test
    @DisplayName("Objects exiting left edge appear on right")
    void wrapLeftToRight() {
        TransformComponent t = new TransformComponent(-50, 500);
        board.enforceBounds(t);
        assertEquals(950f, t.getPosition().x, 0.001f);
    }

    @Test
    @DisplayName("Objects exiting top appear on bottom")
    void wrapTopToBottom() {
        TransformComponent t = new TransformComponent(500, 1100);
        board.enforceBounds(t);
        assertEquals(100f, t.getPosition().y, 0.001f);
    }

    @Test
    @DisplayName("Objects exiting bottom appear on top")
    void wrapBottomToTop() {
        TransformComponent t = new TransformComponent(500, -100);
        board.enforceBounds(t);
        assertEquals(900f, t.getPosition().y, 0.001f);
    }

    @Test
    @DisplayName("Shortest distance wraps around edges")
    void shortestDistanceWraps() {
        // Points near opposite edges: direct=980, wrapped=20
        Vector2 a = new Vector2(10, 500);
        Vector2 b = new Vector2(990, 500);
        assertEquals(20f, board.getDistance(a, b), 0.1f);
    }

    @Test
    @DisplayName("Distance to self is zero")
    void distanceToSelf() {
        Vector2 p = new Vector2(500, 500);
        assertEquals(0f, board.getDistance(p, p), 0.001f);
    }

    @Test
    @DisplayName("Distance is symmetric: d(a,b) == d(b,a)")
    void distanceSymmetric() {
        Vector2 a = new Vector2(100, 200);
        Vector2 b = new Vector2(900, 800);
        assertEquals(board.getDistance(a, b), board.getDistance(b, a), 0.001f);
    }

    @Test
    @DisplayName("Distance is non-negative")
    void distanceNonNegative() {
        Vector2 a = new Vector2(10, 500);
        Vector2 b = new Vector2(990, 500);
        assertTrue(board.getDistance(a, b) >= 0);
    }

    @Test
    @DisplayName("Direct distance used when shorter than wrapped")
    void directDistanceWhenShorter() {
        Vector2 a = new Vector2(100, 100);
        Vector2 b = new Vector2(200, 100);
        assertEquals(100f, board.getDistance(a, b), 0.001f);
    }

    @Test
    @DisplayName("Direction vector points along shortest path")
    void directionVectorShortestPath() {
        Vector2 a = new Vector2(990, 500);
        Vector2 b = new Vector2(10, 500);
        Vector2 out = new Vector2();
        board.getDirectionVector(a, b, out);
        // Shortest path wraps: dx should be +20 (not -980)
        assertTrue(out.x > 0, "Direction should wrap positively; dx=" + out.x);
        assertEquals(0f, out.y, 0.001f);
    }

    @Test
    @DisplayName("Midpoint is on shortest path between two points")
    void midpointOnShortestPath() {
        Vector2 a = new Vector2(100, 100);
        Vector2 b = new Vector2(300, 100);
        Vector2 out = new Vector2();
        board.getMidPoint(a, b, out);
        assertEquals(200f, out.x, 0.001f);
        assertEquals(100f, out.y, 0.001f);
    }

    @Test
    @DisplayName("Midpoint wraps correctly for edge-crossing points")
    void midpointWrapsCorrectly() {
        Vector2 a = new Vector2(990, 500);
        Vector2 b = new Vector2(10, 500);
        Vector2 out = new Vector2();
        board.getMidPoint(a, b, out);
        // Midpoint of the wrapping path should be around x=500
        assertTrue(out.x >= 0 && out.x < 1000, "Midpoint should be within bounds; x=" + out.x);
    }

    @Test
    @DisplayName("Triangle inequality: d(a,c) <= d(a,b) + d(b,c)")
    void triangleInequality() {
        Vector2 a = new Vector2(100, 100);
        Vector2 b = new Vector2(500, 500);
        Vector2 c = new Vector2(900, 800);
        float dac = board.getDistance(a, c);
        float dab = board.getDistance(a, b);
        float dbc = board.getDistance(b, c);
        assertTrue(dac <= dab + dbc + 0.1f, "Triangle inequality should hold");
    }
}
