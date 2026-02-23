package World.Systems;

import GameObject.GameObject;
import java.util.List;

public abstract class System {
    protected final int REQUIRED_SIGNATURE;

    public System(int requiredSignature) {
        this.REQUIRED_SIGNATURE = requiredSignature;
    }

    public void update(float dt, List<GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                processGameObject(dt, gameObject);
            }
        }
    }

    protected abstract void processGameObject(float dt, GameObject gameObject);
}