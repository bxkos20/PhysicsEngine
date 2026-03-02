package world.systems;

import gameObject.GameObject;
import java.util.List;

public abstract class System {
    protected final long REQUIRED_SIGNATURE;
    protected final boolean THREADING;
    protected float lastExecutionTimeMs; // Para el profiling

    public System(long requiredSignature, boolean threading) {
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
            for (int i = 0; i < gameObjects.size(); i++) {
                GameObject gameObject = gameObjects.get(i);
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