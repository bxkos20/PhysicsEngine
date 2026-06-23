package engine.config.implementations;

import engine.config.ISettings;

public class ScreenSettings implements ISettings {
    public int width;
    public int height;

    /**
     * Default constructor.
     * Sets a standard, reasonable window size.
     */
    public ScreenSettings() {
        this.width = 1280;
        this.height = 720;
    }

    /**
     * Fully parameterized constructor.
     * @param width The width of the application window in pixels.
     * @param height The height of the application window in pixels.
     */
    public ScreenSettings(int width, int height) {
        this.width = width;
        this.height = height;
    }
}