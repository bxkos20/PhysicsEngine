package engine.config.implementations;

import engine.config.ISettings;

public class WorldSettings implements ISettings {
    public int width;
    public int height;
    public BoardType boardType;
    public CollisionType collisionType;
    public int gridPartitionSize;

    /**
     * Default constructor.
     * Sets a reasonable default world size for a sandbox environment.
     */
    public WorldSettings() {
        this.width = 1000;
        this.height = 1000;
        this.boardType = BoardType.EDGED;
        this.collisionType = CollisionType.ELASTIC;
        this.gridPartitionSize = 25;
    }

    /**
     * Fully parameterized constructor.
     *
     * @param width  The width of the simulation world in simulation units.
     * @param height The height of the simulation world in simulation units.
     */
    public WorldSettings(int width, int height, BoardType boardType, CollisionType collisionType, int gridPartitionSize) {
        this.width = width;
        this.height = height;
        this.boardType = boardType;
        this.collisionType = collisionType;
        this.gridPartitionSize = gridPartitionSize;
    }

    public enum BoardType {
        EDGED, TOROIDAL
    }

    public enum CollisionType {
        ELASTIC
    }
}