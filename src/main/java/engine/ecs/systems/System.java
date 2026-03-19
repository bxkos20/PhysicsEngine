package engine.ecs.systems;

import engine.ecs.GameObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Abstract base class for ECS systems.
 * 
 * <p>Systems process entities that match a required component signature.
 * Supports both sequential and parallel processing modes.</p>
 * 
 * <h3>Processing Modes:</h3>
 * <ul>
 *   <li>Sequential: Simple for-loop iteration (threading=false)</li>
 *   <li>Parallel: Uses parallelStream() for multi-threaded processing (threading=true)</li>
 * </ul>
 * 
 * @see GameObject#checkSignature(long)
 */
public abstract class System {
    /** Bitmask of required components for entities processed by this system */
    protected final long REQUIRED_SIGNATURE;
    
    /** Whether to use parallel processing */
    protected final boolean THREADING;
    
    /** Last execution time in milliseconds (for profiling) */
    protected float lastExecutionTimeMs;

    /**
     * Creates a system with the specified component signature.
     * 
     * @param requiredSignature Bitmask of required components
     * @param threading          Enable parallel processing
     */
    public System(long requiredSignature, boolean threading) {
        this.REQUIRED_SIGNATURE = requiredSignature;
        this.THREADING = threading;
    }

    /**
     * Updates all entities matching the system's signature.
     * 
     * <p>If threading is enabled, uses parallel stream processing.
     * Otherwise, processes entities sequentially.</p>
     * 
     * @param dt          Delta time in seconds
     * @param gameObjects List of all game objects to process
     */
    public void update(float dt, List<GameObject> gameObjects) {
        long start = java.lang.System.nanoTime();
        if (THREADING){
            gameObjects.parallelStream().forEach(gameObject -> {
                if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                    processGameObject(dt, gameObject);
                }
            });
        }else {
            for (int i = 0; i < gameObjects.size(); i++) {
                GameObject gameObject = gameObjects.get(i);
                if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                    processGameObject(dt, gameObject);
                }
            }
        }
        this.lastExecutionTimeMs = (java.lang.System.nanoTime() - start) / 1_000_000f;
    }

    /**
     * Processes a single game object. Called for each entity matching the signature.
     * 
     * @param dt         Delta time in seconds
     * @param gameObject The entity to process
     */
    protected abstract void processGameObject(float dt, GameObject gameObject);

    /**
     * Returns the last execution time for profiling.
     * 
     * @return Execution time in milliseconds
     */
    private final int EXECUTION_AVERAGING_SIZE = 60;
    private final Queue<Float> executionTimes = new LinkedList<>();

    public float getLastExecutionTimeMs() {
        executionTimes.add(lastExecutionTimeMs);
        if (executionTimes.size() > EXECUTION_AVERAGING_SIZE) {
            executionTimes.poll();
        }
        float sum = 0;
        for (float time : executionTimes) {
            sum += time;
        }
        return sum / executionTimes.size();
    }
}