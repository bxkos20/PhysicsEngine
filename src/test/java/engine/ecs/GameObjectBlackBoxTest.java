package engine.ecs;

import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Black-box tests for GameObject.
 * Tests the ECS entity contract: component attachment, retrieval, signature matching.
 * Based on the specification: "A GameObject is an ID with a component bitmask and storage."
 */
@DisplayName("GameObject - Black-box (specification-based)")
class GameObjectBlackBoxTest {

    @Test
    @DisplayName("Each entity has a unique ID")
    void uniqueIds() {
        GameObject a = new GameObject();
        GameObject b = new GameObject();
        assertNotEquals(a.getId(), b.getId(), "Entity IDs must be unique");
    }

    @Test
    @DisplayName("Entity with no components has empty signature")
    void emptySignature() {
        GameObject obj = new GameObject();
        assertEquals(0L, obj.getSignature(), "Empty entity should have zero signature");
    }

    @Test
    @DisplayName("Adding a component makes it retrievable")
    void addAndGetComponent() {
        GameObject obj = new GameObject();
        TransformComponent t = new TransformComponent(1, 2);
        obj.addComponent(t);

        TransformComponent retrieved = obj.getComponent(ComponentRegistry.getId(TransformComponent.class));
        assertNotNull(retrieved, "Component should be retrievable after adding");
        assertEquals(1f, retrieved.getPosition().x, 0.001f);
    }

    @Test
    @DisplayName("hasComponent returns true only for added components")
    void hasComponentConsistent() {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(0, 0));

        assertTrue(obj.hasComponent(ComponentRegistry.getId(TransformComponent.class)));
        assertFalse(obj.hasComponent(ComponentRegistry.getId(PhysicsComponent.class)));
    }

    @Test
    @DisplayName("checkSignature matches only when all required components are present")
    void checkSignatureRequiresAllComponents() {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(0, 0));

        long transformBit = ComponentRegistry.getBit(TransformComponent.class);
        long physicsBit = ComponentRegistry.getBit(PhysicsComponent.class);

        assertTrue(obj.checkSignature(transformBit), "Should match single component");
        assertFalse(obj.checkSignature(physicsBit), "Should not match missing component");
        assertFalse(obj.checkSignature(transformBit | physicsBit), "Should not match partial signature");
    }

    @Test
    @DisplayName("Adding multiple different components works correctly")
    void multipleComponents() {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(0, 0));
        obj.addComponent(new PhysicsComponent(1, 0.5f, 0.1f));
        obj.addComponent(new ColliderComponent(5f));

        long allBits = ComponentRegistry.getBit(TransformComponent.class)
                | ComponentRegistry.getBit(PhysicsComponent.class)
                | ComponentRegistry.getBit(ColliderComponent.class);

        assertTrue(obj.checkSignature(allBits), "Should match all three components");
    }

    @Test
    @DisplayName("getComponent returns null for non-existent component")
    void getNonExistentComponentReturnsNull() {
        GameObject obj = new GameObject();
        assertNull(obj.getComponent(ComponentRegistry.getId(TransformComponent.class)));
    }
}
