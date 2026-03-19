package engine.ecs.components;

import engine.graphics.Color;
import engine.graphics.interfaces.IShape;

public class RendererComponent {  //TODO: don't follow ecs principes
    private Color color;
    private IShape shape;

    public RendererComponent(Color color, IShape shape) {
        this.color = color;
        this.shape = shape;
    }

    public IShape getShape() {
        return shape;
    }

    public void setShape(IShape shape) {
        this.shape = shape;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
