package demos.dots;

import demos.dots.settings.DotSettings;
import engine.app.EngineLauncher;
import engine.app.backend.LauncherType;
import engine.config.settings.Settings;
import engine.config.settings.implementations.ScreenSettings;
import engine.config.settings.implementations.SimulationSettings;
import engine.config.settings.implementations.WorldSettings;

public class Launcher {
    public static void main(String[] args) {
        Settings settings = Settings.createDefault();

        WorldSettings worldSettings = new WorldSettings(2500, 2500, WorldSettings.BoardType.TOROIDAL, WorldSettings.CollisionType.ELASTIC ,5);
        DotSettings dotSettings = new DotSettings(2.5f, 5000, 10, 1, 10, 50);
        ScreenSettings screenSettings = new ScreenSettings(1920, 1080);
        SimulationSettings simulationSettings = new SimulationSettings(1/10f, 1/ 30f, 0.25f, 1.0f, 2);

        settings.add(worldSettings)
                .add(dotSettings)
                .add(screenSettings)
                .add(simulationSettings);

        DotSimulationLogic simulationLogic = new DotSimulationLogic();

        EngineLauncher.launch(LauncherType.LIBGDX, simulationLogic, settings);
    }
}
