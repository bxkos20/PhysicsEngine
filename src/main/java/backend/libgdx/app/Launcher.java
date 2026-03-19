package backend.libgdx.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import engine.config.SimulationConfig;

/**
 * Application entry point for the LibGDX-based physics simulation.
 * Configures the LWJGL3 window and launches the {@link SimulationController}.
 * 
 * <p>This class serves as the thin adapter between the LibGDX backend
 * and the engine-agnostic simulation logic.</p>
 * 
 * @see SimulationController
 * @see SimulationConfig
 */
public class Launcher {
    /**
     * Application entry point.
     * Configures the LWJGL3 window and starts the simulation.
     * 
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
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

        // Start simulation
        new Lwjgl3Application(
            new SimulationController(
                SimulationConfig.World.WORLD_WIDTH, 
                SimulationConfig.World.WORLD_HEIGHT
            ), 
            config
        );
    }
}