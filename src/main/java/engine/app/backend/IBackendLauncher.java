package engine.app.backend;

import engine.app.implementation.ISimulationLogic;
import engine.config.Settings;
import engine.world.World;

public interface IBackendLauncher {
    void launch(ISimulationLogic simulationLogic, World world, Settings settings);
}
