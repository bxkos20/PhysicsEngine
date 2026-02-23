package GameObject;

import World.PhysicsWorld;
import com.badlogic.gdx.graphics.Color;
import GameObject.Elements.*;

public abstract class GameObject {
    protected Color color = Color.BLACK;
    public Transform transform;
    public PhysicsComponent physics;   // Puede ser null
    public ColliderComponent collider; // Puede ser null

    public GameObject(float x, float y) {
        this.transform = new Transform(x, y);
    }

    public GameObject withPhysics() {
        this.physics = new PhysicsComponent();
        return this;
    }

    public GameObject withCollider(float radius) {
        this.collider = new ColliderComponent(radius);
        return this;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Actualiza la lógica del objeto (IA, decisiones, cambios de estado).
     * @param dt Delta time (tiempo transcurrido)
     * @param world Referencia al mundo para consultar vecinos, mapa, etc.
     */
    public abstract void update(float dt, PhysicsWorld world);
}
