package engine.ecs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for ComponentRegistry.
 * Tests ID assignment, bit mask conversion, and thread-safety logic paths.
 */
class ComponentRegistryTest {

    // Dummy classes for unique component registration
    static class CompA {}
    static class CompB {}
    static class CompC {}

    @Test
    void getIdReturnsConsistentId() {
        int id1 = ComponentRegistry.getId(CompA.class);
        int id2 = ComponentRegistry.getId(CompA.class);
        assertEquals(id1, id2);
    }

    @Test
    void differentClassesGetDifferentIds() {
        int idA = ComponentRegistry.getId(CompA.class);
        int idB = ComponentRegistry.getId(CompB.class);
        assertNotEquals(idA, idB);
    }

    @Test
    void idsAreSequential() {
        int idA = ComponentRegistry.getId(CompA.class);
        int idB = ComponentRegistry.getId(CompB.class);
        int idC = ComponentRegistry.getId(CompC.class);
        // Each new class should get nextId++
        assertEquals(idA + 1, idB);
        assertEquals(idB + 1, idC);
    }

    @Test
    void getBitReturnsPowerOfTwo() {
        int id = ComponentRegistry.getId(CompA.class);
        long bit = ComponentRegistry.getBit(CompA.class);
        assertEquals(1L << id, bit);
    }

    @Test
    void idToBitMatchesGetBit() {
        int id = ComponentRegistry.getId(CompA.class);
        assertEquals(ComponentRegistry.getBit(CompA.class), ComponentRegistry.idToBit(id));
    }

    @Test
    void getAllComponentsReturnsCopy() {
        var map1 = ComponentRegistry.getAllComponents();
        var map2 = ComponentRegistry.getAllComponents();
        // Should be equal content but different object (copy)
        assertEquals(map1, map2);
        assertNotSame(map1, map2);
    }

    @Test
    void getAllComponentsContainsRegistered() {
        ComponentRegistry.getId(CompA.class);
        var map = ComponentRegistry.getAllComponents();
        assertTrue(map.containsKey(CompA.class));
    }
}
