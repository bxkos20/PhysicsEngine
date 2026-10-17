package engine.world;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import engine.config.settings.implementations.WorldSettings;

class WorldFactoryTest {

    @Test
    void createWorldWhitSettings_shouldUseSafeGridSize_whenGridPartitionSizeIsZero() {
        WorldSettings settings = new WorldSettings(1000, 500, WorldSettings.BoardType.EDGED, WorldSettings.CollisionType.ELASTIC, 0);

        World world = assertDoesNotThrow(() -> WorldFactory.createWorldWhitSettings(settings));

        assertTrue(world.gridPartition.getCellSize() > 0f, "gridPartitionSize should never be zero or negative");
    }
}
