package engine.app;

import engine.app.backend.IBackendLauncher;
import engine.app.backend.LauncherType;
import engine.app.implementation.ISimulationLogic;
import engine.config.Settings;

public abstract class EngineLauncher {

    public static void launch(LauncherType launcher, ISimulationLogic simulationLogic, Settings settings) {
        IBackendLauncher backendLauncher = launcher.getLauncher();
        backendLauncher.launch(simulationLogic, settings);
    }
}