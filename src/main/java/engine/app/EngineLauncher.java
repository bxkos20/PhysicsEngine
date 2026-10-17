package engine.app;

import engine.app.backend.IBackendLauncher;
import engine.app.backend.LauncherType;
import engine.app.implementation.ISimulationLogic;
import engine.config.parameters.Parameters;
import engine.config.parameters.implementations.SimulationParameters;
import engine.config.settings.implementations.SimulationSettings;
import engine.config.settings.Settings;

public abstract class EngineLauncher {

    public static void launch(LauncherType launcher, ISimulationLogic simulationLogic, Settings settings) {
        IBackendLauncher backendLauncher = launcher.getLauncher();
        Parameters parameters = createParameters(settings);

        backendLauncher.launch(simulationLogic, settings, parameters);
    }

    private static Parameters createParameters(Settings settings) {
        SimulationSettings simulationSettings = settings.get(SimulationSettings.class);
        if (simulationSettings == null) {
            throw new IllegalArgumentException("Settings must include SimulationSettings");
        }

        return new Parameters().add(new SimulationParameters(simulationSettings));
    }
}