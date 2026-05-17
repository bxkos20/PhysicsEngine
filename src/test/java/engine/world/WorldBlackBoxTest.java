package engine.world;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import engine.physics.ElasticCollision;
import engine.world.board.ToroidalBoard;
import engine.world.spatial.ToroidalGridPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Black-box tests for World.
 * Tests the world update pipeline as a whole: entity management, physics integration,
 * collision resolution — from a functional/specification perspective.
 */
@DisplayName("World - Black-box (integration)")
class WorldBlackBoxTest {

    private World world;

    @BeforeEach
    void setUp() {
        world = new World(
                new ToroidalBoard(1000, 1000),
                new ElasticCollision(),
                new ToroidalGridPartition(1000, 1000, 200)
        );
    }

    private GameObject createPhysicsEntity(float x, float y, float radius, float mass, float restitution) {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(x, y));
        obj.addComponent(new PhysicsComponent(mass, restitution, 0));
        obj.addComponent(new ColliderComponent(radius));
        return obj;
    }

    @Test
    @DisplayName("Entities added via addEntity appear after next update")
    void entityAddedAppearsAfterUpdate() {
        assertTrue(world.getGameObjects().isEmpty());
        GameObject obj = createPhysicsEntity(100, 100, 5, 1, 1);
        world.addEntity(obj);

        // Before update, entity is in pending buffer
        assertTrue(world.getGameObjects().isEmpty());

        world.update(1f / 60f);

        // After update, entity should be in main list
        assertEquals(1, world.getGameObjects().size());
        assertSame(obj, world.getGameObjects().get(0));
    }

    @Test
    @DisplayName("Entity position updates with velocity after update")
    void positionUpdatesWithVelocity() {
        GameObject obj = createPhysicsEntity(100, 100, 5, 1, 1);
        PhysicsComponent physics = obj.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        physics.setVelocity(60, 0); // 60 units/sec in X

        world.addEntity(obj);
        world.update(1f / 60f); // dt = 1/60

        TransformComponent transform = obj.getComponent(ComponentRegistry.getId(TransformComponent.class));
        // Position should have moved by velocity * dt = 60 * (1/60) = 1
        assertEquals(101f, transform.getPosition().x, 0.5f);
    }

    @Test
    @DisplayName("Force application changes velocity according to F=ma")
    void forceChangesVelocity() {
        GameObject obj = createPhysicsEntity(100, 100, 5, 2, 1);
        PhysicsComponent physics = obj.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        physics.addForce(100, 0); // Apply 100N in X

        world.addEntity(obj);
        world.update(1f / 60f);

        // a = F/m = 100/2 = 50; dv = a*dt = 50/60 ≈ 0.833
        // velocity should be ~0.833 in X
        assertTrue(physics.getVelocity().x > 0, "Velocity should increase in X");
    }

    @Test
    @DisplayName("Static objects (mass=0) do not move")
    void staticObjectsDoNotMove() {
        GameObject obj = createPhysicsEntity(100, 100, 5, 0, 1);
        PhysicsComponent physics = obj.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        physics.addForce(1000, 1000);

        world.addEntity(obj);
        world.update(1f / 60f);

        TransformComponent transform = obj.getComponent(ComponentRegistry.getId(TransformComponent.class));
        assertEquals(100f, transform.getPosition().x, 0.001f);
        assertEquals(100f, transform.getPosition().y, 0.001f);
    }

    @Test
    @DisplayName("Entities wrap around toroidal world boundaries")
    void entitiesWrapAround() {
        GameObject obj = createPhysicsEntity(999, 500, 5, 1, 1);
        PhysicsComponent physics = obj.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        physics.setVelocity(120, 0); // Fast enough to cross boundary

        world.addEntity(obj);
        world.update(1f / 60f);

        TransformComponent transform = obj.getComponent(ComponentRegistry.getId(TransformComponent.class));
        // Should wrap: 999 + 120*(1/60) = 999 + 2 = 1001 => wraps to 1
        assertTrue(transform.getPosition().x < 100, "Should wrap around; x=" + transform.getPosition().x);
    }

    @Test
    @DisplayName("Colliding entities are resolved after update")
    void collidingEntitiesResolved() {
        GameObject a = createPhysicsEntity(100, 100, 10, 1, 1);
        GameObject b = createPhysicsEntity(115, 100, 10, 1, 1);

        PhysicsComponent pA = a.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        PhysicsComponent pB = b.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        pA.setVelocity(10, 0);
        pB.setVelocity(-10, 0);

        world.addEntity(a);
        world.addEntity(b);
        world.update(1f / 60f);

        // After collision resolution, objects should be moving apart
        // (at least one should have reversed direction)
        boolean movingApart = pA.getVelocity().x < 0 || pB.getVelocity().x > 0;
        assertTrue(movingApart, "Objects should be moving apart after collision");
    }

    @Test
    @DisplayName("Multiple entities can be added and updated")
    void multipleEntitiesUpdate() {
        for (int i = 0; i < 10; i++) {
            GameObject obj = createPhysicsEntity(100 + i * 50, 100, 5, 1, 1);
            PhysicsComponent physics = obj.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
            physics.setVelocity(0, 10);
            world.addEntity(obj);
        }

        world.update(1f / 60f);

        assertEquals(10, world.getGameObjects().size());
        for (GameObject obj : world.getGameObjects()) {
            TransformComponent t = obj.getComponent(ComponentRegistry.getId(TransformComponent.class));
            assertTrue(t.getPosition().y > 100, "Y should have increased");
        }
    }

    @Test
    @DisplayName("Friction reduces velocity over time")
    void frictionReducesVelocity() {
        GameObject obj = createPhysicsEntity(500, 500, 5, 1, 1);
        PhysicsComponent physics = obj.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        physics.setFriction(5.0f); // High friction
        physics.setVelocity(100, 0);

        world.addEntity(obj);
        world.update(1f / 60f);

        // Friction factor = max(0, 1 - 5 * 1/60) ≈ 0.917
        // velocity should be reduced from 100
        assertTrue(physics.getVelocity().x < 100, "Friction should reduce velocity");
    }

    @Test
    @DisplayName("Forces are cleared after each update")
    void forcesClearedAfterUpdate() {
        GameObject obj = createPhysicsEntity(500, 500, 5, 1, 1);
        PhysicsComponent physics = obj.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        physics.addForce(100, 0);

        world.addEntity(obj);
        world.update(1f / 60f);

        assertEquals(0f, physics.getSumForces().x, 0.001f);
        assertEquals(0f, physics.getSumForces().y, 0.001f);
    }

    @Test
    @DisplayName("Profiling info returns non-null string")
    void profilingInfoNotNull() {
        world.update(1f / 60f);
        assertNotNull(world.getProfilingInfo());
    }
}
