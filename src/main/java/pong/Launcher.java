package pong;

import engine.app.EngineLauncher;
import engine.app.backend.LauncherType;
import engine.config.Settings;
import engine.config.implementations.*;
import engine.physics.ElasticCollision;
import engine.world.World;
import engine.world.board.EdgedBoard;

import engine.world.spatial.ToroidalGridPartition;
import pong.settings.PongSettings;

public class Launcher {
    public static void main(String[] args) {
        Settings settings = Settings.createDefault();
        WorldSettings worldSettings = new WorldSettings(1000, 500);
        PerformanceSettings performanceSettings = new PerformanceSettings(true, 100);
        PongSettings pongSettings = new PongSettings();

        settings.add(worldSettings)
                .add(performanceSettings)
                .add(pongSettings);


        Simulation simulation = new Simulation();
        World world = new World(
                new EdgedBoard(worldSettings.width, worldSettings.height),
                new ElasticCollision(),
                new ToroidalGridPartition(worldSettings.width, worldSettings.height, performanceSettings.gridCellSize),
                performanceSettings.enableMultithreading
        );

        EngineLauncher.launch(LauncherType.LIBGDX, simulation, world, settings);
    }
}
