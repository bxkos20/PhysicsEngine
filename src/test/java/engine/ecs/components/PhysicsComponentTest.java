package engine.ecs.components;

import engine.math.Vector2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for PhysicsComponent.
 * Tests all getters/setters, force accumulation, and edge cases.
 */
class PhysicsComponentTest {

    private PhysicsComponent physics;

    @BeforeEach
    void setUp() {
        physics = new PhysicsComponent(10f, 0.8f, 0.2f);
    }

    @Test
    void constructorSetsProperties() {
        assertEquals(10f, physics.getMass(), 0.001f);
        assertEquals(0.8f, physics.getRestitution(), 0.001f);
        assertEquals(0.2f, physics.getFriction(), 0.001f);
    }

    @Test
    void velocityStartsAtZero() {
        assertEquals(0f, physics.getVelocity().x, 0.001f);
        assertEquals(0f, physics.getVelocity().y, 0.001f);
    }

    @Test
    void sumForcesStartsAtZero() {
        assertEquals(0f, physics.getSumForces().x, 0.001f);
        assertEquals(0f, physics.getSumForces().y, 0.001f);
    }

    @Test
    void addForceVector() {
        physics.addForce(new Vector2(5, 10));
        assertEquals(5f, physics.getSumForces().x, 0.001f);
        assertEquals(10f, physics.getSumForces().y, 0.001f);
    }

    @Test
    void addForceScalars() {
        physics.addForce(3f, 7f);
        assertEquals(3f, physics.getSumForces().x, 0.001f);
        assertEquals(7f, physics.getSumForces().y, 0.001f);
    }

    @Test
    void addForceAccumulates() {
        physics.addForce(new Vector2(1, 2));
        physics.addForce(new Vector2(3, 4));
        assertEquals(4f, physics.getSumForces().x, 0.001f);
        assertEquals(6f, physics.getSumForces().y, 0.001f);
    }

    @Test
    void setVelocityVector() {
        Vector2 vel = new Vector2(5, 6);
        physics.setVelocity(vel);
        assertEquals(5f, physics.getVelocity().x, 0.001f);
        assertEquals(6f, physics.getVelocity().y, 0.001f);
    }

    @Test
    void setVelocityScalars() {
        physics.setVelocity(7, 8);
        assertEquals(7f, physics.getVelocity().x, 0.001f);
        assertEquals(8f, physics.getVelocity().y, 0.001f);
    }

    @Test
    void setMass() {
        physics.setMass(20f);
        assertEquals(20f, physics.getMass(), 0.001f);
    }

    @Test
    void setMassToZero() {
        physics.setMass(0f);
        assertEquals(0f, physics.getMass(), 0.001f);
    }

    @Test
    void setRestitution() {
        physics.setRestitution(0.5f);
        assertEquals(0.5f, physics.getRestitution(), 0.001f);
    }

    @Test
    void setFriction() {
        physics.setFriction(0.9f);
        assertEquals(0.9f, physics.getFriction(), 0.001f);
    }

    @Test
    void setSumForces() {
        physics.setSumForces(new Vector2(10, 20));
        assertEquals(10f, physics.getSumForces().x, 0.001f);
        assertEquals(20f, physics.getSumForces().y, 0.001f);
    }
}
