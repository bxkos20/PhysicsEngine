package pong;

import engine.app.EngineLauncher;
import engine.app.backend.LauncherType;
import engine.config.Settings;
import engine.config.implementations.*;
import engine.physics.ElasticCollision;
import engine.world.World;
import engine.world.board.ToroidalBoard;
import engine.world.spatial.ToroidalGridPartition;

public class Launcher {
    public static void main(String[] args) {
        Settings settings = Settings.createDefault();
        WorldSettings worldSettings = new WorldSettings(1000, 1000);
        settings.add(worldSettings);

        PerformanceSettings performanceSettings = settings.get(PerformanceSettings.class);

        Simulation simulation = new Simulation();
        World world = new World(
                new ToroidalBoard(worldSettings.width, worldSettings.height),
                new ElasticCollision(),
                new ToroidalGridPartition(worldSettings.width, worldSettings.height, performanceSettings.gridCellSize),
                performanceSettings.enableMultithreading
        );

        EngineLauncher.launch(LauncherType.LIBGDX, simulation, world, settings);
    }
}
