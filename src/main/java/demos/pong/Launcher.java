package demos.pong;

import demos.pong.settings.PongSettings;
import engine.app.EngineLauncher;
import engine.app.backend.LauncherType;
import engine.config.settings.Settings;
import engine.config.settings.implementations.PerformanceSettings;
import engine.config.settings.implementations.WorldSettings;

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