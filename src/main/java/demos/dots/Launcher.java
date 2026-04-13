package demos.dots;

import engine.app.EngineLauncher;
import engine.app.backend.LauncherType;

public class Launcher {
    public static void main(String[] args) {
        DotSimulationLogic simulationLogic = new DotSimulationLogic();
        EngineLauncher.launch(LauncherType.LIBGDX, simulationLogic);
    }
}
