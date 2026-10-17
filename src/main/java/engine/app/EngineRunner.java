package engine.app;

import engine.config.parameters.Parameters;
import engine.config.parameters.implementations.SimulationParameters;

public class EngineRunner {

    private final EngineSimulation engineSimulation;
    private final SimulationParameters simulationParameters;

    private float simulationAccumulator = 0f;
    private float renderAccumulator = 0f;
    

    public EngineRunner(EngineSimulation engineSimulation, Parameters parameters){
        this.simulationParameters = parameters.get(SimulationParameters.class);
        this.engineSimulation = engineSimulation;
    }


    public void run(float dt){
        float frameTime = Math.min(dt, simulationParameters.maxFrameTime);

        if (simulationParameters.render) {
            renderAccumulator += frameTime;
            while (renderAccumulator >= simulationParameters.fixedTimestepRendering) {
                engineSimulation.render(simulationParameters.fixedTimestepRendering);
                renderAccumulator -= simulationParameters.fixedTimestepRendering;
            }
        }

        if (!simulationParameters.pause){
            simulationAccumulator += frameTime * simulationParameters.timescale;
            while (simulationAccumulator >= simulationParameters.fixedTimestepSimulation) {
                engineSimulation.update(simulationParameters.fixedTimestepSimulation);
                simulationAccumulator -= simulationParameters.fixedTimestepSimulation;
            }
        }
    }
    
}
