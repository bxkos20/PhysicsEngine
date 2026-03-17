package engine.ecs.components;

import com.badlogic.gdx.graphics.Color;
import backend.libgdx.render.shapes.Shape;

public class RendererComponent {  //TODO: don't follow ecs principes
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
