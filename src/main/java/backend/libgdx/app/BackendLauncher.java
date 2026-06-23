package backend.libgdx.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import engine.app.backend.IBackendLauncher;
import engine.app.implementation.ISimulationLogic;
import engine.config.Settings;
import engine.config.implementations.ScreenSettings;
import engine.world.World;

/**
 * Application entry point for the LibGDX-based physics simulation.
 * Configures the LWJGL3 window and launches the {@link BackendSimulation}.
 * 
 * <p>This class serves as the thin adapter between the LibGDX backend
 * and the engine-agnostic simulation logic.</p>
 * 
 * @see BackendSimulation
 */
public class BackendLauncher implements IBackendLauncher {
    /**
     * Application entry point.
     * Configures the LWJGL3 window and starts the simulation.
     */
    public void launch(ISimulationLogic simulationLogic, World world, Settings settings) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        ScreenSettings screenSettings = settings.get(ScreenSettings.class);

        // Use configuration constants
        config.setTitle("PlaceHolder");
        config.setWindowedMode(
                screenSettings.width,
                screenSettings.height
        );
        config.setForegroundFPS(60); // Lock to 60 FPS
        config.useVsync(false);
        config.setResizable(false);

        // Start simulation with pre-created simulation core
        new Lwjgl3Application(
                new BackendSimulation(simulationLogic, world, settings),
                config
        );
    }
}