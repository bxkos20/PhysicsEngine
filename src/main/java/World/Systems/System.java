package World.Systems;

import GameObject.GameObject;
import java.util.List;

public abstract class System {
    protected final int REQUIRED_SIGNATURE;
    private float lastExecutionTimeMs; // Para el profiling

    public System(int requiredSignature) {
        this.REQUIRED_SIGNATURE = requiredSignature;
    }

    public void update(float dt, List<GameObject> gameObjects) {
        long start = java.lang.System.nanoTime();
        for (GameObject gameObject : gameObjects) {
            if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                processGameObject(dt, gameObject);
            }
        }
        this.lastExecutionTimeMs = (java.lang.System.nanoTime() - start) / 1_000_000f;
    }

    protected abstract void processGameObject(float dt, GameObject gameObject);

    public float getLastExecutionTimeMs() { return lastExecutionTimeMs; }
}