package gameObject.components.core;

import com.badlogic.gdx.graphics.Color;
import render.shapes.Shape;

public class RendererComponent {
    private Color color;
    private Shape shape;

    public RendererComponent(Color color, Shape shape) {
        this.color = color;
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
