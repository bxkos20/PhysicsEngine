package demos.dots;

import demos.dots.settings.DotSettings;
import engine.app.EngineLauncher;
import engine.app.backend.LauncherType;
import engine.config.Settings;
import engine.config.implementations.PerformanceSettings;
import engine.config.implementations.ScreenSettings;
import engine.config.implementations.WorldSettings;

public class Launcher {
    public static void main(String[] args) {
        Settings settings = Settings.createDefault();

        WorldSettings worldSettings = new WorldSettings(2500, 2500, WorldSettings.BoardType.TOROIDAL, WorldSettings.CollisionType.ELASTIC ,5);
        DotSettings dotSettings = new DotSettings(2.5f, 5000, 10, 1, 10, 50);
        ScreenSettings screenSettings = new ScreenSettings(1920, 1080);

        settings.add(worldSettings)
                .add(dotSettings)
                .add(screenSettings);

        DotSimulationLogic simulationLogic = new DotSimulationLogic();

        EngineLauncher.launch(LauncherType.LIBGDX, simulationLogic, settings);
    }
}
