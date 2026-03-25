package engine.app.backend;

import engine.app.implementation.ISimulationLogic;

public interface IBackendLauncher {
    void launch(ISimulationLogic simulationLogic);
}
