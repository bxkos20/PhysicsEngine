package engine.app;

import engine.app.backend.IBackendLauncher;
import engine.app.backend.LauncherType;
import engine.app.implementation.ISimulationLogic;

public abstract class EngineLauncher {

    public static void launch(LauncherType launcher, ISimulationLogic simulationLogic) {
        IBackendLauncher backendLauncher = launcher.getLauncher();
        backendLauncher.launch(simulationLogic);
    }
}
