package engine.ecs.components;

import engine.math.Vector2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for TransformComponent.
 * Tests position get/set paths.
 */
class TransformComponentTest {

    @Test
    void constructorSetsPosition() {
        TransformComponent t = new TransformComponent(3, 7);
        assertEquals(3f, t.getPosition().x, 0.001f);
        assertEquals(7f, t.getPosition().y, 0.001f);
    }

    @Test
    void setPositionFromVector() {
        TransformComponent t = new TransformComponent(0, 0);
        t.setPosition(new Vector2(10, 20));
        assertEquals(10f, t.getPosition().x, 0.001f);
        assertEquals(20f, t.getPosition().y, 0.001f);
    }

    @Test
    void setPositionFromCoordinates() {
        TransformComponent t = new TransformComponent(0, 0);
        t.setPosition(5, 15);
        assertEquals(5f, t.getPosition().x, 0.001f);
        assertEquals(15f, t.getPosition().y, 0.001f);
    }

    @Test
    void getPositionReturnsMutableReference() {
        TransformComponent t = new TransformComponent(0, 0);
        Vector2 pos = t.getPosition();
        pos.x = 99;
        assertEquals(99f, t.getPosition().x, 0.001f);
    }
}
