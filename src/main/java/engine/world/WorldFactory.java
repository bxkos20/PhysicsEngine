package engine.world;

import engine.config.implementations.WorldSettings;
import engine.physics.Collision;
import engine.physics.ElasticCollision;
import engine.world.board.Board;
import engine.world.board.EdgedBoard;
import engine.world.board.ToroidalBoard;
import engine.world.spatial.GridPartition;
import engine.world.spatial.ToroidalGridPartition;

public class WorldFactory {

    public static World createWorldWhitSettings(WorldSettings settings){
        Board board;
        Collision collision;
        GridPartition gridPartition;

        float safeGridSize = settings.gridPartitionSize > 0
                ? settings.gridPartitionSize
                : Math.max(10f, Math.max(settings.width, settings.height) / 25f);

        switch (settings.boardType){
            case EDGED -> board = new EdgedBoard(settings.width, settings.height);
            case TOROIDAL -> board = new ToroidalBoard(settings.width, settings.height);
            default -> throw new IllegalArgumentException("Unsupported board type: " + settings.boardType);
        }

        gridPartition = new ToroidalGridPartition(settings.width, settings.height, safeGridSize);

        switch (settings.collisionType){
            case ELASTIC -> collision = new ElasticCollision();
            default -> throw new IllegalArgumentException("Unsupported collision type: " + settings.collisionType);
        }

        return new World(board, collision, gridPartition);
    }
}
