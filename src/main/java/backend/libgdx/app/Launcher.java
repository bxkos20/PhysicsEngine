package backend.libgdx.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import engine.config.SimulationConfig;

public class Launcher {
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