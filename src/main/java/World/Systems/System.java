package World.Systems;

import GameObject.GameObject;
import java.util.List;

public abstract class System {
    protected final int REQUIRED_SIGNATURE;
    private final boolean THREADING;
    private float lastExecutionTimeMs; // Para el profiling

    public System(int requiredSignature, boolean threading) {
        this.REQUIRED_SIGNATURE = requiredSignature;
        this.THREADING = threading;
    }

    public void update(float dt, List<GameObject> gameObjects) {
        long start = java.lang.System.nanoTime();
        if (THREADING){
            gameObjects.parallelStream().forEach(gameObject -> {
                if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                    processGameObject(dt, gameObject);
                }
            });
        }else {
            for (GameObject gameObject : gameObjects) {
                if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                    processGameObject(dt, gameObject);
                }
            }
        }
        this.lastExecutionTimeMs = (java.lang.System.nanoTime() - start) / 1_000_000f;
    }

    protected abstract void processGameObject(float dt, GameObject gameObject);

    public float getLastExecutionTimeMs() { return lastExecutionTimeMs; }
}