package intial;

import engine.app.EngineLauncher;
import engine.app.backend.LauncherType;

public class Launcher {
    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        EngineLauncher.launch(LauncherType.LIBGDX, simulation);
    }
}
