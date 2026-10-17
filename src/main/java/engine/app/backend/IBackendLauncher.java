package engine.app.backend;

import engine.app.implementation.ISimulationLogic;
import engine.config.settings.Settings;

public interface IBackendLauncher {
    void launch(ISimulationLogic simulationLogic, Settings settings);
}