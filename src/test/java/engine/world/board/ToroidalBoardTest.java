package engine.world.board;

import engine.ecs.components.TransformComponent;
import engine.math.Vector2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for ToroidalBoard.
 * Tests toroidal distance calculations, direction vectors, midpoint, and boundary enforcement.
 * Covers wrap-around paths and edge cases.
 */
class ToroidalBoardTest {

    private ToroidalBoard board;
    private static final float WIDTH = 1000f;
    private static final float HEIGHT = 1000f;

    @BeforeEach
    void setUp() {
        board = new ToroidalBoard(WIDTH, HEIGHT);
    }

    // --- enforceBounds paths ---

    @Test
    void enforceBoundsWithinBounds() {
        TransformComponent t = new TransformComponent(500, 500);
        board.enforceBounds(t);
        assertEquals(500f, t.getPosition().x, 0.001f);
        assertEquals(500f, t.getPosition().y, 0.001f);
    }

    @Test
    void enforceBoundsWrapsPositiveX() {
        TransformComponent t = new TransformComponent(1050, 500);
        board.enforceBounds(t);
        assertEquals(50f, t.getPosition().x, 0.001f);
    }

    @Test
    void enforceBoundsWrapsNegativeX() {
        TransformComponent t = new TransformComponent(-50, 500);
        board.enforceBounds(t);
        assertEquals(950f, t.getPosition().x, 0.001f);
    }

    @Test
    void enforceBoundsWrapsPositiveY() {
        TransformComponent t = new TransformComponent(500, 1200);
        board.enforceBounds(t);
        assertEquals(200f, t.getPosition().y, 0.001f);
    }

    @Test
    void enforceBoundsWrapsNegativeY() {
        TransformComponent t = new TransformComponent(500, -100);
        board.enforceBounds(t);
        assertEquals(900f, t.getPosition().y, 0.001f);
    }

    @Test
    void enforceBoundsExactBoundary() {
        TransformComponent t = new TransformComponent(1000, 1000);
        board.enforceBounds(t);
        assertEquals(0f, t.getPosition().x, 0.001f);
        assertEquals(0f, t.getPosition().y, 0.001f);
    }

    // --- getDistance / getDistance2 paths ---

    @Test
    void getDistanceSamePoint() {
        Vector2 p = new Vector2(500, 500);
        assertEquals(0f, board.getDistance(p, p), 0.001f);
    }

    @Test
    void getDistance2SamePoint() {
        Vector2 p = new Vector2(500, 500);
        assertEquals(0f, board.getDistance2(p, p), 0.001f);
    }

    @Test
    void getDistanceDirectPath() {
        Vector2 a = new Vector2(100, 100);
        Vector2 b = new Vector2(200, 100);
        assertEquals(100f, board.getDistance(a, b), 0.001f);
    }

    @Test
    void getDistance2DirectPath() {
        Vector2 a = new Vector2(100, 100);
        Vector2 b = new Vector2(200, 100);
        assertEquals(10000f, board.getDistance2(a, b), 0.001f);
    }

    @Test
    void getDistanceWrapsAroundXEdge() {
        // Shortest path from x=990 to x=10 wraps around (distance=20, not 980)
        Vector2 a = new Vector2(990, 500);
        Vector2 b = new Vector2(10, 500);
        float dist = board.getDistance(a, b);
        assertEquals(20f, dist, 0.1f);
    }

    @Test
    void getDistance2WrapsAroundXEdge() {
        Vector2 a = new Vector2(990, 500);
        Vector2 b = new Vector2(10, 500);
        assertEquals(400f, board.getDistance2(a, b), 1f);
    }

    @Test
    void getDistanceWrapsAroundYEdge() {
        Vector2 a = new Vector2(500, 990);
        Vector2 b = new Vector2(500, 10);
        float dist = board.getDistance(a, b);
        assertEquals(20f, dist, 0.1f);
    }

    @Test
    void getDistanceDiagonal() {
        Vector2 a = new Vector2(0, 0);
        Vector2 b = new Vector2(3, 4);
        assertEquals(5f, board.getDistance(a, b), 0.001f);
    }

    // --- getDirectionVector paths ---

    @Test
    void getDirectionVectorDirect() {
        Vector2 a = new Vector2(100, 100);
        Vector2 b = new Vector2(200, 200);
        Vector2 out = new Vector2();
        board.getDirectionVector(a, b, out);
        assertEquals(100f, out.x, 0.001f);
        assertEquals(100f, out.y, 0.001f);
    }

    @Test
    void getDirectionVectorWrapsX() {
        // From 990 to 10: shortest direction wraps, dx should be 20 (not -980)
        Vector2 a = new Vector2(990, 500);
        Vector2 b = new Vector2(10, 500);
        Vector2 out = new Vector2();
        board.getDirectionVector(a, b, out);
        assertEquals(20f, out.x, 0.1f);
        assertEquals(0f, out.y, 0.001f);
    }

    @Test
    void getDirectionVectorWrapsY() {
        Vector2 a = new Vector2(500, 990);
        Vector2 b = new Vector2(500, 10);
        Vector2 out = new Vector2();
        board.getDirectionVector(a, b, out);
        assertEquals(0f, out.x, 0.001f);
        assertEquals(20f, out.y, 0.1f);
    }

    // --- getMidPoint paths ---

    @Test
    void getMidPointDirect() {
        Vector2 a = new Vector2(100, 100);
        Vector2 b = new Vector2(200, 200);
        Vector2 out = new Vector2();
        board.getMidPoint(a, b, out);
        assertEquals(150f, out.x, 0.001f);
        assertEquals(150f, out.y, 0.001f);
    }

    @Test
    void getMidPointWrapsAround() {
        Vector2 a = new Vector2(990, 500);
        Vector2 b = new Vector2(10, 500);
        Vector2 out = new Vector2();
        board.getMidPoint(a, b, out);
        // Midpoint of wrap path: 990 + 10 = 1000/2 = 500 (wrap midpoint)
        float expectedX = (990f + 20f / 2f) % WIDTH;
        assertEquals(expectedX, out.x, 1f);
    }
}
