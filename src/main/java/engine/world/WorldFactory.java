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
        Board board = null;
        Collision collision = null;
        GridPartition gridPartition = null;

        if (settings.gridPartitionSize <= 0){
            switch (settings.boardType){
                case EDGED -> {
                    board = new EdgedBoard(settings.width, settings.height);
                    gridPartition = new ToroidalGridPartition(settings.width, settings.height, settings.gridPartitionSize);
                    //TODO EdgedGridPartition
                }
                case TOROIDAL -> {
                    board = new ToroidalBoard(settings.width, settings.height);
                    gridPartition = new ToroidalGridPartition(settings.width, settings.height, settings.gridPartitionSize);
                }
            }
        }
        else {
            //TODO NullGridPartition
            gridPartition = new ToroidalGridPartition(settings.width, settings.height, settings.gridPartitionSize);
            switch (settings.boardType){
                case EDGED -> board = new EdgedBoard(settings.width, settings.height);
                case TOROIDAL -> board = new ToroidalBoard(settings.width, settings.height);
            }
        }

        switch (settings.collisionType){
            case ELASTIC -> collision = new ElasticCollision();
        }

        return new World(board, collision, gridPartition);
    }
}
