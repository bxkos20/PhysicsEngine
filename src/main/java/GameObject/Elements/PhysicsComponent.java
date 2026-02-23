package GameObject.Elements;

import com.badlogic.gdx.math.Vector2;

// 2. La Física (Movimiento)
public class PhysicsComponent {
    public Vector2 velocity = new Vector2();
    public Vector2 sumForces = new Vector2();
    public float mass = 1f;
    public float restitution = 0f; // "Rebote" (1 = pelota goma, 0 = piedra)
    public float friction = 0.01f;

    public void addForce(Vector2 force) {
        sumForces.add(force);
    }

    public void update(Transform transform, float dt) {
        // Integración de Euler semi-implicita
        velocity.mulAdd(velocity, friction * -1);
        velocity.mulAdd(sumForces, dt / mass);

        transform.position.add(velocity.x * dt, velocity.y * dt);

        sumForces.set(0, 0);
    }
}
