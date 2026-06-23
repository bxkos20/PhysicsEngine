package engine.config.implementations;

import engine.config.ISettings;

public class PerformanceSettings implements ISettings {
    public boolean enableMultithreading;
    public int gridCellSize;

    /**
     * Default constructor.
     * Enables multithreading and sets a generic grid cell size.
     */
    public PerformanceSettings() {
        this.enableMultithreading = true;
        this.gridCellSize = 25;
    }

    /**
     * Fully parameterized constructor.
     * @param enableMultithreading Whether to use multiple threads for system updates.
     * @param gridCellSize The size of the cells in the spatial partitioning grid.
     */
    public PerformanceSettings(boolean enableMultithreading, int gridCellSize) {
        this.enableMultithreading = enableMultithreading;
        this.gridCellSize = gridCellSize;
    }
}