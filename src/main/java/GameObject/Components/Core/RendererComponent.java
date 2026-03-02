package GameObject.Components.Core;

import Render.Shapes.Shapes.Shape;

public class RendererComponent {
    private Shape shape;

    public RendererComponent(Shape shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
}
