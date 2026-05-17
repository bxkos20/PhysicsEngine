package engine.world.spatial;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.TransformComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Black-box tests for ToroidalGridPartition.
 * Tests spatial partitioning contract: entities in the same or nearby cells
 * are found by processNearby, toroidal wrapping works correctly.
 */
@DisplayName("ToroidalGridPartition - Black-box (functional)")
class ToroidalGridPartitionBlackBoxTest {

    private ToroidalGridPartition grid;
    private static final float WIDTH = 1000f;
    private static final float HEIGHT = 1000f;
    private static final float CELL_SIZE = 200f;

    @BeforeEach
    void setUp() {
        grid = new ToroidalGridPartition(WIDTH, HEIGHT, CELL_SIZE);
    }

    private GameObject createEntity(float x, float y) {
        GameObject obj = new GameObject();
        obj.addComponent(new TransformComponent(x, y));
        return obj;
    }

    @Test
    @DisplayName("Nearby entities in same cell are found")
    void nearbyEntitiesInSameCell() {
        GameObject a = createEntity(50, 50);
        GameObject b = createEntity(60, 60);

        List<GameObject> all = new ArrayList<>();
        all.add(a);
        all.add(b);
        grid.add(all);

        List<GameObject> found = new ArrayList<>();
        int count = grid.processNearby(a.getComponent(ComponentRegistry.getId(TransformComponent.class)), 1, found::add);

        assertTrue(count >= 1, "Should find at least the other entity in same cell");
        assertTrue(found.contains(b), "Should find entity b");
    }

    @Test
    @DisplayName("Distant entities in far cells are not found with distance=0")
    void distantEntitiesNotFound() {
        // Place entities 3 cells apart (600 units) - not reachable with distance=0 (same cell only)
        GameObject a = createEntity(50, 50);    // cell (0, 0)
        GameObject b = createEntity(650, 50);   // cell (3, 0) - 3 cells away

        List<GameObject> all = new ArrayList<>();
        all.add(a);
        all.add(b);
        grid.add(all);

        List<GameObject> found = new ArrayList<>();
        grid.processNearby(a.getComponent(ComponentRegistry.getId(TransformComponent.class)), 0, found::add);

        assertFalse(found.contains(b), "Entity 3 cells away should not be found with distance=0");
    }

    @Test
    @DisplayName("Clear removes all entities from grid")
    void clearRemovesEntities() {
        GameObject a = createEntity(50, 50);
        List<GameObject> all = new ArrayList<>();
        all.add(a);
        grid.add(all);

        grid.clear();

        List<GameObject> found = new ArrayList<>();
        grid.processNearby(a.getComponent(ComponentRegistry.getId(TransformComponent.class)), 1, found::add);
        assertFalse(found.contains(a), "After clear, entity should not be found");
    }

    @Test
    @DisplayName("Entities near world edge wrap to opposite side (toroidal)")
    void toroidalWrappingNearEdge() {
        // Entity at x=990 should be in the same cell as entity at x=10
        // because the grid wraps around (cell at col 4 wraps to col 0)
        GameObject nearRightEdge = createEntity(990, 50);
        GameObject nearLeftEdge = createEntity(10, 50);

        List<GameObject> all = new ArrayList<>();
        all.add(nearRightEdge);
        all.add(nearLeftEdge);
        grid.add(all);

        List<GameObject> found = new ArrayList<>();
        grid.processNearby(
                nearRightEdge.getComponent(ComponentRegistry.getId(TransformComponent.class)),
                2, // distance=2 to cover wrap-around cells
                found::add
        );

        // With distance=2, the toroidal grid should find the entity near the left edge
        assertTrue(found.contains(nearLeftEdge), "Toroidal wrapping should find entity on opposite edge");
    }

    @Test
    @DisplayName("Multiple entities in same cell are all found")
    void multipleEntitiesInSameCell() {
        List<GameObject> entities = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entities.add(createEntity(50 + i, 50 + i));
        }
        grid.add(entities);

        TransformComponent center = entities.get(0).getComponent(ComponentRegistry.getId(TransformComponent.class));
        List<GameObject> found = new ArrayList<>();
        grid.processNearby(center, 1, found::add);

        assertEquals(5, found.size(), "All 5 entities in same cell should be found");
    }

    @Test
    @DisplayName("Entity without TransformComponent is not added")
    void entityWithoutTransformNotAdded() {
        GameObject obj = new GameObject();
        // No TransformComponent added
        List<GameObject> all = new ArrayList<>();
        all.add(obj);
        grid.add(all); // Should not crash

        // Grid should still work
        GameObject withTransform = createEntity(50, 50);
        List<GameObject> all2 = new ArrayList<>();
        all2.add(withTransform);
        grid.add(all2);

        List<GameObject> found = new ArrayList<>();
        grid.processNearby(withTransform.getComponent(ComponentRegistry.getId(TransformComponent.class)), 1, found::add);
        // Should find at least itself
        assertTrue(found.size() >= 1);
    }

    @Test
    @DisplayName("Grid dimensions match world dimensions")
    void gridDimensions() {
        assertEquals(WIDTH, grid.getWidth(), 0.001f);
        assertEquals(HEIGHT, grid.getHeight(), 0.001f);
        assertEquals(CELL_SIZE, grid.getCellSize(), 0.001f);
    }
}
