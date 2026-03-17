package engine.ecs.components;

import engine.math.Vector2;

public class TransformComponent {
    private Vector2 position;

    public TransformComponent(float x, float y) {
        this.position = new Vector2(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPosition(int x, int y) {
        this.position.x = x;
        this.position.y = y;
    }
}