package pong;

import engine.app.EngineLauncher;
import engine.app.backend.LauncherType;
import engine.config.Settings;
import engine.config.implementations.PerformanceSettings;
import engine.config.implementations.WorldSettings;
import pong.settings.PongSettings;

public class Launcher {
    public static void main(String[] args) {
        Settings settings = Settings.createDefault();
        WorldSettings worldSettings = new WorldSettings(1000, 500, WorldSettings.BoardType.EDGED, WorldSettings.CollisionType.ELASTIC,  0);
        PerformanceSettings performanceSettings = new PerformanceSettings(true);
        PongSettings pongSettings = new PongSettings();

        settings.add(worldSettings)
                .add(performanceSettings)
                .add(pongSettings);


        Simulation simulation = new Simulation();

        EngineLauncher.launch(LauncherType.LIBGDX, simulation, settings);
    }
}