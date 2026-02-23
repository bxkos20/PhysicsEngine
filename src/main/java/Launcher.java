import Simulation.SimulationController;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Launcher {
    final static int SCREEN_WIDTH = 1280;
    final static int SCREEN_HEIGHT = 720;
    final static int WORLD_WIDTH = 2500;
    final static int WORLD_HEIGHT = 2500;

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // Configuración de la ventana
        config.setTitle("PlaceHolder");
        config.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);
        config.setForegroundFPS(60); // Bloquear a 60 FPS para no quemar la GPU
        config.setResizable(false);

        // Arrancar
        new Lwjgl3Application(new SimulationController(WORLD_WIDTH, WORLD_HEIGHT), config);
    }
}