package engine.physics;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import engine.world.board.Board;
import engine.world.board.ToroidalBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Black-box tests for ElasticCollision.
 * Tests the collision contract from a specification perspective:
 * - Conservation of momentum
 * - Conservation of kinetic energy (elastic)
 * - Proper separation of overlapping objects
 * - Static objects act as immovable walls
 */
@DisplayName("ElasticCollision - Black-box (specification-based)")
class ElasticCollisionBlackBoxTest {

    private ElasticCollision collision;
    private Board board;

    @BeforeEach
    void setUp() {
        collision = new ElasticCollision();
        board = new ToroidalBoard(1000, 1000);
    }

    private GameObject createCircle(float x, float y, float radius, float mass, float restitution) {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(x, y));
        obj.addComponent(new ColliderComponent(radius));
        obj.addComponent(new PhysicsComponent(mass, restitution, 0));
        return obj;
    }

    @Test
    @DisplayName("Momentum is conserved in elastic collision")
    void momentumConserved() {
        float massA = 2f, massB = 3f;
        GameObject a = createCircle(100, 100, 15, massA, 1f);
        GameObject b = createCircle(120, 100, 15, massB, 1f);

        PhysicsComponent pA = a.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        PhysicsComponent pB = b.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pA.setVelocity(10, 0);
        pB.setVelocity(-5, 0);

        // Total momentum before
        float pxBefore = massA * pA.getVelocity().x + massB * pB.getVelocity().x;
        float pyBefore = massA * pA.getVelocity().y + massB * pB.getVelocity().y;

        collision.solveCollision(a, b, board);

        // Total momentum after
        float pxAfter = massA * pA.getVelocity().x + massB * pB.getVelocity().x;
        float pyAfter = massA * pA.getVelocity().y + massB * pB.getVelocity().y;

        assertEquals(pxBefore, pxAfter, 1f, "X momentum should be conserved");
        assertEquals(pyBefore, pyAfter, 1f, "Y momentum should be conserved");
    }

    @Test
    @DisplayName("Kinetic energy is conserved in perfectly elastic collision (e=1)")
    void kineticEnergyConservedElastic() {
        float massA = 2f, massB = 2f;
        GameObject a = createCircle(95, 100, 15, massA, 1f);
        GameObject b = createCircle(110, 100, 15, massB, 1f);

        PhysicsComponent pA = a.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        PhysicsComponent pB = b.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pA.setVelocity(10, 0);
        pB.setVelocity(-10, 0);

        float keBefore = 0.5f * massA * pA.getVelocity().len2() + 0.5f * massB * pB.getVelocity().len2();

        collision.solveCollision(a, b, board);

        float keAfter = 0.5f * massA * pA.getVelocity().len2() + 0.5f * massB * pB.getVelocity().len2();

        assertEquals(keBefore, keAfter, 5f, "Kinetic energy should be approximately conserved");
    }

    @Test
    @DisplayName("Non-overlapping circles are not colliding")
    void nonOverlappingNotColliding() {
        GameObject a = createCircle(100, 100, 10, 1, 1);
        GameObject b = createCircle(200, 100, 10, 1, 1);
        assertFalse(collision.isColliding(a, b, board));
    }

    @Test
    @DisplayName("Overlapping circles are colliding")
    void overlappingAreColliding() {
        GameObject a = createCircle(100, 100, 20, 1, 1);
        GameObject b = createCircle(110, 100, 20, 1, 1);
        assertTrue(collision.isColliding(a, b, board));
    }

    @Test
    @DisplayName("Static object does not change velocity after collision")
    void staticObjectUnaffected() {
        GameObject wall = createCircle(500, 100, 50, 0, 1); // mass=0 => static
        GameObject ball = createCircle(460, 100, 10, 1, 1); // Ball to the LEFT of wall

        PhysicsComponent pBall = ball.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pBall.setVelocity(50, 0); // Moving RIGHT towards wall

        PhysicsComponent pWall = wall.getComponent(ComponentRegistry.getId(PhysicsComponent.class));

        collision.solveCollision(wall, ball, board);

        // Wall velocity remains zero
        assertEquals(0f, pWall.getVelocity().x, 0.001f);
        assertEquals(0f, pWall.getVelocity().y, 0.001f);

        // Ball should bounce back (move left)
        assertTrue(pBall.getVelocity().x < 0, "Ball should bounce off wall");
    }

    @Test
    @DisplayName("Equal mass head-on collision swaps velocities (elastic)")
    void equalMassSwapsVelocities() {
        GameObject a = createCircle(95, 100, 15, 1, 1);
        GameObject b = createCircle(110, 100, 15, 1, 1);

        PhysicsComponent pA = a.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        PhysicsComponent pB = b.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pA.setVelocity(10, 0);
        pB.setVelocity(-10, 0);

        collision.solveCollision(a, b, board);

        // For equal mass elastic collision, velocities swap
        assertTrue(pA.getVelocity().x < 0, "A should reverse direction");
        assertTrue(pB.getVelocity().x > 0, "B should reverse direction");
    }

    @Test
    @DisplayName("Collision detection works with toroidal wrapping")
    void collisionDetectionToroidal() {
        // Two objects near opposite edges of toroidal world
        GameObject a = createCircle(5, 500, 20, 1, 1);
        GameObject b = createCircle(995, 500, 20, 1, 1);

        // In toroidal world, distance is 10 (wraps around), radii sum = 40
        // They should be colliding
        assertTrue(collision.isColliding(a, b, board), "Should collide via toroidal wrapping");
    }

    @Test
    @DisplayName("Lower restitution reduces bounce velocity")
    void lowerRestitutionReducesBounce() {
        // e=1 (perfectly elastic) - ball bouncing off static wall
        GameObject wall1 = createCircle(500, 100, 50, 0, 1f);
        GameObject ball1 = createCircle(460, 100, 10, 1, 1f);
        PhysicsComponent pBall1 = ball1.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pBall1.setVelocity(50, 0); // Moving towards wall

        collision.solveCollision(wall1, ball1, board);
        float speedAfterElastic = Math.abs(pBall1.getVelocity().x);

        // e=0.1 (nearly inelastic) - ball bouncing off static wall
        GameObject wall2 = createCircle(500, 100, 50, 0, 0.1f);
        GameObject ball2 = createCircle(460, 100, 10, 1, 0.1f);
        PhysicsComponent pBall2 = ball2.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pBall2.setVelocity(50, 0); // Moving towards wall

        collision.solveCollision(wall2, ball2, board);
        float speedAfterInelastic = Math.abs(pBall2.getVelocity().x);

        // Higher restitution should result in higher bounce speed
        assertTrue(speedAfterElastic > speedAfterInelastic,
                "Elastic collision should produce higher bounce speed: elastic=" + speedAfterElastic + " inelastic=" + speedAfterInelastic);
    }

    @Test
    @DisplayName("Objects are separated after collision resolution")
    void objectsSeparatedAfterCollision() {
        GameObject a = createCircle(100, 100, 10, 1, 1);
        GameObject b = createCircle(108, 100, 10, 1, 1); // Overlapping by 12

        collision.solveCollision(a, b, board);

        TransformComponent tA = a.getComponent(ComponentRegistry.getId(TransformComponent.class));
        TransformComponent tB = b.getComponent(ComponentRegistry.getId(TransformComponent.class));

        float dist = (float) Math.sqrt(
                Math.pow(tB.getPosition().x - tA.getPosition().x, 2) +
                Math.pow(tB.getPosition().y - tA.getPosition().y, 2)
        );

        // After separation, distance should be >= radii sum (20)
        assertTrue(dist >= 18f, "Objects should be separated; dist=" + dist);
    }
}
