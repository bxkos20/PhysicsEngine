package engine.ecs;

import engine.ecs.components.TransformComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.ColliderComponent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for GameObject.
 * Tests ID generation, component add/get/remove, signature bitmask logic.
 */
class GameObjectTest {

    @Test
    void uniqueIds() {
        GameObject a = new GameObject();
        GameObject b = new GameObject();
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    void idsAreSequential() {
        GameObject a = new GameObject();
        GameObject b = new GameObject();
        assertEquals(a.getId() + 1, b.getId());
    }

    @Test
    void addComponentUpdatesSignature() {
        GameObject obj = new GameObject();
        long sigBefore = obj.getSignature();
        obj.addComponent(new TransformComponent(0, 0));
        long sigAfter = obj.getSignature();
        assertNotEquals(sigBefore, sigAfter);
        assertTrue(sigAfter > sigBefore);
    }

    @Test
    void addSameComponentTwiceDoesNotDuplicate() {
        GameObject obj = new GameObject();
        TransformComponent t1 = new TransformComponent(1, 2);
        TransformComponent t2 = new TransformComponent(3, 4);
        obj.addComponent(t1);
        long sig1 = obj.getSignature();
        obj.addComponent(t2); // Should be ignored (same component type already present)
        long sig2 = obj.getSignature();
        assertEquals(sig1, sig2);
        // Original component should remain
        TransformComponent retrieved = obj.getComponent(ComponentRegistry.getId(TransformComponent.class));
        assertEquals(1f, retrieved.getPosition().x, 0.001f);
    }

    @Test
    void getComponentReturnsCorrectInstance() {
        GameObject obj = new GameObject();
        TransformComponent t = new TransformComponent(5, 10);
        obj.addComponent(t);
        int compId = ComponentRegistry.getId(TransformComponent.class);
        TransformComponent retrieved = obj.getComponent(compId);
        assertSame(t, retrieved);
    }

    @Test
    void getComponentReturnsNullIfNotPresent() {
        GameObject obj = new GameObject();
        int compId = ComponentRegistry.getId(TransformComponent.class);
        assertNull(obj.getComponent(compId));
    }

    @Test
    void hasComponentReturnsTrueWhenPresent() {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(0, 0));
        int compId = ComponentRegistry.getId(TransformComponent.class);
        assertTrue(obj.hasComponent(compId));
    }

    @Test
    void hasComponentReturnsFalseWhenNotPresent() {
        GameObject obj = new GameObject();
        int compId = ComponentRegistry.getId(TransformComponent.class);
        assertFalse(obj.hasComponent(compId));
    }

    @Test
    void checkSignatureWithSingleComponent() {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(0, 0));
        long transformBit = ComponentRegistry.getBit(TransformComponent.class);
        assertTrue(obj.checkSignature(transformBit));
    }

    @Test
    void checkSignatureWithMultipleComponents() {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(0, 0));
        obj.addComponent(new PhysicsComponent(1, 0.5f, 0.1f));

        long bothBits = ComponentRegistry.getBit(TransformComponent.class) | ComponentRegistry.getBit(PhysicsComponent.class);
        assertTrue(obj.checkSignature(bothBits));

        // Missing ColliderComponent
        long allThree = bothBits | ComponentRegistry.getBit(ColliderComponent.class);
        assertFalse(obj.checkSignature(allThree));
    }

    @Test
    void checkSignatureWithZeroMask() {
        GameObject obj = new GameObject();
        // Empty mask should always match
        assertTrue(obj.checkSignature(0L));
    }

    @Test
    void signatureStartsAtZero() {
        GameObject obj = new GameObject();
        assertEquals(0L, obj.getSignature());
    }
}
