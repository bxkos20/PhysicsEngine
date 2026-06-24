package engine.config.implementations;

import engine.config.ISettings;

public class PerformanceSettings implements ISettings {
    public boolean enableMultithreading;

    /**
     * Default constructor.
     * Enables multithreading and sets a generic grid cell size.
     */
    public PerformanceSettings() {
        this.enableMultithreading = true;
    }

    /**
     * Fully parameterized constructor.
     * @param enableMultithreading Whether to use multiple threads for system updates.
     */
    public PerformanceSettings(boolean enableMultithreading) {
        this.enableMultithreading = enableMultithreading;
    }
}