package engine.config.implementations;

import engine.config.ISettings;

public class RenderingSettings implements ISettings {
    public int batchVertices;
    public int batchIndices;

    /**
     * Default constructor.
     * Sets large, safe batch sizes suitable for complex simulations.
     */
    public RenderingSettings() {
        this.batchVertices = 50000;
        this.batchIndices = 150000;
    }

    /**
     * Fully parameterized constructor.
     * @param batchVertices The maximum number of vertices in a single batch.
     * @param batchIndices The maximum number of indices in a single batch.
     */
    public RenderingSettings(int batchVertices, int batchIndices) {
        this.batchVertices = batchVertices;
        this.batchIndices = batchIndices;
    }
}