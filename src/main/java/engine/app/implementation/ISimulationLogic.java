package engine.app.implementation;

import engine.ecs.GameObject;
import engine.world.World;
import java.util.List;

/**
 * Interface for simulation logic implementations.
 * 
 * <p>Allows different simulation behaviors to be plugged into the engine
 * without the engine knowing about specific component types or logic.</p>
 * 
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>Initialize simulation entities and systems</li>
 *   <li>Provide custom systems for entity processing</li>
 *   <li>Handle simulation-specific logic</li>
 * </ul>
 */
public interface ISimulationLogic {
    
    /**
     * Called once during simulation initialization.
     * Should create all necessary entities and systems.
     */
    void start(World world);

    /**
     * Called every update cycle during simulation runtime.
     * Handles simulation-specific logic.
     */
    void update(float deltaTime, List<GameObject> gameObjects);

    /**
     * Returns profiling information for the last render call.
     *
     * @return Formatted string with render time in milliseconds
     */
    String getProfilingInfo();
}
