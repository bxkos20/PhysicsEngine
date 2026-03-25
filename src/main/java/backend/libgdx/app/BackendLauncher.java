package backend.libgdx.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import engine.app.backend.IBackendLauncher;
import engine.app.implementation.ISimulationLogic;
import engine.config.SimulationConfig;

/**
 * Application entry point for the LibGDX-based physics simulation.
 * Configures the LWJGL3 window and launches the {@link BackendSimulation}.
 * 
 * <p>This class serves as the thin adapter between the LibGDX backend
 * and the engine-agnostic simulation logic.</p>
 * 
 * @see BackendSimulation
 * @see SimulationConfig
 */
public class BackendLauncher implements IBackendLauncher {
    /**
     * Application entry point.
     * Configures the LWJGL3 window and starts the simulation.
     */
    public void launch(ISimulationLogic simulationLogic) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // Use configuration constants
        config.setTitle("PlaceHolder");
        config.setWindowedMode(
                SimulationConfig.Display.SCREEN_WIDTH,
                SimulationConfig.Display.SCREEN_HEIGHT
        );
        config.setForegroundFPS(60); // Lock to 60 FPS
        config.useVsync(false);
        config.setResizable(false);

        // Start simulation with pre-created simulation core
        new Lwjgl3Application(
                new BackendSimulation(simulationLogic),
                config
        );
    }
}