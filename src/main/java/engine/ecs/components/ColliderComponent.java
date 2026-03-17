package engine.ecs.components;

public class ColliderComponent {
    private float radius;

    public ColliderComponent(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
