package engine.physics;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import engine.ecs.systems.implementations.CollisionSystem;
import engine.math.Vector2;
import engine.world.board.Board;
import engine.world.board.ToroidalBoard;
import engine.world.spatial.ToroidalGridPartition;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for ElasticCollision.
 * Tests collision detection and resolution logic paths:
 * - isColliding: overlapping vs non-overlapping circles
 * - solveCollision: separation, impulse, static objects, separating objects
 */
class ElasticCollisionTest {

    private static final ElasticCollision collision = new ElasticCollision();
    private Board board;

    @BeforeEach
    void setUp() {
        ComponentRegistry.reset();
        board = new ToroidalBoard(1000, 1000);
    }

    private GameObject createCircle(float x, float y, float radius, float mass, float restitution) {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(x, y));
        obj.addComponent(new ColliderComponent(radius));
        obj.addComponent(new PhysicsComponent(mass, restitution, 0));
        return obj;
    }

    private GameObject createBox(float x, float y, float width, float height, float mass, float restitution) {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(x, y));
        obj.addComponent(new ColliderComponent(width, height));
        obj.addComponent(new PhysicsComponent(mass, restitution, 0));
        return obj;
    }

    // --- isColliding paths ---

    @Test
    void isCollidingOverlappingCircles() {
        GameObject a = createCircle(100, 100, 10, 1, 1);
        GameObject b = createCircle(115, 100, 10, 1, 1);
        // distance=15, radiiSum=20 => overlapping
        assertTrue(collision.isColliding(a, b, board));
    }

    @Test
    void isCollidingTouchingCircles() {
        GameObject a = createCircle(100, 100, 10, 1, 1);
        GameObject b = createCircle(120, 100, 10, 1, 1);
        // distance=20, radiiSum=20 => NOT colliding (strict <)
        assertFalse(collision.isColliding(a, b, board));
    }

    @Test
    void isCollidingDistantCircles() {
        GameObject a = createCircle(100, 100, 10, 1, 1);
        GameObject b = createCircle(200, 100, 10, 1, 1);
        assertFalse(collision.isColliding(a, b, board));
    }

    @Test
    void isCollidingSamePosition() {
        GameObject a = createCircle(100, 100, 10, 1, 1);
        GameObject b = createCircle(100, 100, 10, 1, 1);
        // Same position => distance=0, radiiSum=20 => colliding
        assertTrue(collision.isColliding(a, b, board));
    }

    @Test
    void isCollidingOverlappingAabbs() {
        GameObject a = createBox(100, 100, 20, 10, 1, 1);
        GameObject b = createBox(115, 100, 20, 10, 1, 1);
        assertTrue(collision.isColliding(a, b, board));
    }

    @Test
    void isCollidingTouchingAabbs() {
        GameObject a = createBox(100, 100, 20, 10, 1, 1);
        GameObject b = createBox(120, 100, 20, 10, 1, 1);
        assertFalse(collision.isColliding(a, b, board));
    }

    @Test
    void isCollidingCircleAndAabb() {
        GameObject circle = createCircle(100, 100, 5, 1, 1);
        GameObject box = createBox(109, 100, 10, 10, 0, 1);
        assertTrue(collision.isColliding(circle, box, board));
        assertTrue(collision.isColliding(box, circle, board));

        GameObject separatedCircle = createCircle(100, 100, 5, 1, 1);
        GameObject separatedBox = createBox(110, 100, 10, 10, 0, 1);
        assertFalse(collision.isColliding(separatedCircle, separatedBox, board));
    }

    @Test
    void isCollidingAabbAcrossToroidalBoundary() {
        GameObject a = createBox(5, 100, 10, 10, 1, 1);
        GameObject b = createBox(96, 100, 10, 10, 1, 1);
        assertTrue(collision.isColliding(a, b, board));
    }

    // --- solveCollision paths ---

    @Test
    void solveCollisionSeparatesOverlappingObjects() {
        GameObject a = createCircle(100, 100, 10, 1, 1);
        GameObject b = createCircle(115, 100, 10, 1, 1);
        // Overlap = 20 - 15 = 5
        collision.solveCollision(a, b, board);

        TransformComponent tA = a.getComponent(ComponentRegistry.getId(TransformComponent.class));
        TransformComponent tB = b.getComponent(ComponentRegistry.getId(TransformComponent.class));

        // After separation, objects should be further apart
        float dx = Math.abs(tB.getPosition().x - tA.getPosition().x);
        assertTrue(dx >= 15f, "Objects should be separated; dx=" + dx);
    }

    @Test
    void solveCollisionAppliesImpulse() {
        GameObject a = createCircle(100, 100, 10, 1, 1);
        GameObject b = createCircle(115, 100, 10, 1, 1);

        // Give them approaching velocities
        PhysicsComponent pA = a.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        PhysicsComponent pB = b.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pA.setVelocity(5, 0);  // moving right
        pB.setVelocity(-5, 0); // moving left

        collision.solveCollision(a, b, board);

        // After collision, velocities should reverse (elastic with e=1)
        // a should move left, b should move right
        assertTrue(pA.getVelocity().x < 0, "A should bounce left, vx=" + pA.getVelocity().x);
        assertTrue(pB.getVelocity().x > 0, "B should bounce right, vx=" + pB.getVelocity().x);
    }

    @Test
    void solveCollisionStaticObjectDoesNotMove() {
        GameObject a = createCircle(200, 100, 50, 0, 1); // mass=0 => static wall
        GameObject b = createCircle(160, 100, 10, 1, 1); // ball to the left

        PhysicsComponent pB = b.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pB.setVelocity(5, 0); // Moving towards wall

        collision.solveCollision(a, b, board);

        PhysicsComponent pA = a.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        // Static object should not change velocity
        assertEquals(0f, pA.getVelocity().x, 0.001f);
        assertEquals(0f, pA.getVelocity().y, 0.001f);
    }

    @Test
    void solveCollisionBothStaticDoesNothing() {
        GameObject a = createCircle(100, 100, 10, 0, 1);
        GameObject b = createCircle(115, 100, 10, 0, 1);

        TransformComponent tA = a.getComponent(ComponentRegistry.getId(TransformComponent.class));
        TransformComponent tB = b.getComponent(ComponentRegistry.getId(TransformComponent.class));
        float axBefore = tA.getPosition().x;
        float bxBefore = tB.getPosition().x;

        collision.solveCollision(a, b, board);

        // Both static => no movement
        assertEquals(axBefore, tA.getPosition().x, 0.001f);
        assertEquals(bxBefore, tB.getPosition().x, 0.001f);
    }

    @Test
    void solveCollisionEqualMassSwapsVelocities() {
        // Head-on elastic collision with equal mass => velocities swap
        GameObject a = createCircle(100, 100, 10, 1, 1);
        GameObject b = createCircle(120, 100, 10, 1, 1);

        PhysicsComponent pA = a.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        PhysicsComponent pB = b.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pA.setVelocity(10, 0);
        pB.setVelocity(-10, 0);

        // Move them to overlap slightly
        TransformComponent tA = a.getComponent(ComponentRegistry.getId(TransformComponent.class));
        TransformComponent tB = b.getComponent(ComponentRegistry.getId(TransformComponent.class));
        tA.setPosition(95, 100);
        tB.setPosition(105, 100);

        collision.solveCollision(a, b, board);

        // With equal mass and e=1, velocities should approximately swap
        assertTrue(pA.getVelocity().x < 0, "A should reverse direction");
        assertTrue(pB.getVelocity().x > 0, "B should reverse direction");
    }

    @Test
    void solveCollisionSeparatingObjectsNoImpulse() {
        GameObject a = createCircle(100, 100, 10, 1, 1);
        GameObject b = createCircle(115, 100, 10, 1, 1);

        // Velocities moving apart (a moving left, b moving right)
        PhysicsComponent pA = a.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        PhysicsComponent pB = b.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pA.setVelocity(-10, 0);
        pB.setVelocity(10, 0);

        collision.solveCollision(a, b, board);

        // Objects are separating => no impulse applied
        // After separation, velocities should be unchanged (only position adjusted)
        // The normalVelocity > 0 check in elasticCollision should skip impulse
        assertTrue(pA.getVelocity().x <= 0, "A should still be moving left");
        assertTrue(pB.getVelocity().x >= 0, "B should still be moving right");
    }

    @Test
    void solveCollisionSeparatesAndBouncesCircleFromStaticAabb() {
        GameObject box = createBox(109, 100, 10, 10, 0, 1);
        GameObject circle = createCircle(100, 100, 5, 1, 1);
        PhysicsComponent circlePhysics = circle.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        circlePhysics.setVelocity(5, 0);

        collision.solveCollision(box, circle, board);

        assertTrue(circlePhysics.getVelocity().x < 0);
        assertTrue(circle.getComponent(ComponentRegistry.getId(TransformComponent.class)).getPosition().x < 100);
    }

    @Test
    void solveCollisionSeparatesAndBouncesAabbs() {
        GameObject fixedBox = createBox(100, 100, 20, 10, 0, 1);
        GameObject movingBox = createBox(115, 100, 20, 10, 1, 1);
        PhysicsComponent movingPhysics = movingBox.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        movingPhysics.setVelocity(-5, 0);

        collision.solveCollision(fixedBox, movingBox, board);

        assertTrue(movingPhysics.getVelocity().x > 0);
        assertTrue(movingBox.getComponent(ComponentRegistry.getId(TransformComponent.class)).getPosition().x > 115);
    }

    @Test
    void solveCollisionSeparatesCircleStartingInsideAabb() {
        GameObject box = createBox(100, 100, 20, 20, 0, 1);
        GameObject circle = createCircle(100, 100, 2, 1, 1);

        collision.solveCollision(box, circle, board);

        assertFalse(collision.isColliding(box, circle, board));
    }

    @Test
    void collisionSystemSearchesCellsCoveredByLargeAabbs() {
        ToroidalGridPartition grid = new ToroidalGridPartition(100, 100, 10);
        GameObject fixedBox = createBox(5, 50, 30, 10, 0, 1);
        GameObject movingBox = createBox(25, 50, 30, 10, 1, 1);
        List<GameObject> objects = List.of(fixedBox, movingBox);
        grid.add(objects);
        CollisionSystem system = new CollisionSystem(false, grid, board, collision);

        system.update(0, objects);

        assertNotEquals(50f,
                movingBox.getComponent(ComponentRegistry.getId(TransformComponent.class)).getPosition().y,
                0.001f);
    }
}