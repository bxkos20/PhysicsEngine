package engine.ecs.components;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for ColliderComponent.
 * Tests radius get/set paths.
 */
class ColliderComponentTest {

    @Test
    void constructorSetsRadius() {
        ColliderComponent c = new ColliderComponent(5f);
        assertEquals(5f, c.getRadius(), 0.001f);
    }

    @Test
    void setRadius() {
        ColliderComponent c = new ColliderComponent(5f);
        c.setRadius(10f);
        assertEquals(10f, c.getRadius(), 0.001f);
    }

    @Test
    void zeroRadius() {
        ColliderComponent c = new ColliderComponent(0f);
        assertEquals(0f, c.getRadius(), 0.001f);
    }

    @Test
    void negativeRadiusAllowed() {
        // White-box: no validation on radius - test the actual behavior
        ColliderComponent c = new ColliderComponent(-1f);
        assertEquals(-1f, c.getRadius(), 0.001f);
    }
}
