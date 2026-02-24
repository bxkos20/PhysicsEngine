package GameObject.Components.Core;

import com.badlogic.gdx.graphics.Color;

public class RendererComponent {
    private Color color;
    private float radius;

    public RendererComponent(Color color, float radius) {
        this.color = color;
        this.radius = radius;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
