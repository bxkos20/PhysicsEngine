package GameObject.Components.Core;

import com.badlogic.gdx.math.Vector2;

public class PhysicsComponent {
    private Vector2 velocity = new Vector2();
    private Vector2 sumForces = new Vector2();
    private float mass;
    private float restitution; // Bounciness (1 = a lot, 0 = nothing)
    private float friction; // (0 = nothing, 1 = static)

    public PhysicsComponent(float mass, float restitution, float friction) {
        this.velocity = new Vector2();
        this.sumForces = new Vector2();
        this.mass = mass;
        this.restitution = restitution;
        this.friction = friction;
    }

    public void addForce(Vector2 force) {
        sumForces.add(force);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public Vector2 getSumForces() {
        return sumForces;
    }

    public void setSumForces(Vector2 sumForces) {
        this.sumForces = sumForces;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }
}
