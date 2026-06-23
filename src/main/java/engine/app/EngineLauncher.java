package engine.app;

import engine.app.backend.IBackendLauncher;
import engine.app.backend.LauncherType;
import engine.app.implementation.ISimulationLogic;
import engine.config.Settings;
import engine.world.World;

public abstract class EngineLauncher {

    public static void launch(LauncherType launcher, ISimulationLogic simulationLogic, World world, Settings settings) { //TODO
        IBackendLauncher backendLauncher = launcher.getLauncher();
        backendLauncher.launch(simulationLogic, world, settings);
    }
}
