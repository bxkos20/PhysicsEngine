package backend.libgdx.factories;

import backend.libgdx.render.primitives.Circle;
import backend.libgdx.render.primitives.FilledCircle;
import backend.libgdx.render.primitives.FilledRect;
import backend.libgdx.render.primitives.Rect;
import engine.factories.ShapeFactory;
import engine.graphics.interfaces.IShape;

public class LibGDXShapeFactory implements ShapeFactory {
    @Override
    public IShape createCircle(float radius, int segments, float lineWidth) {
        return new Circle(radius, segments, lineWidth);
    }

    @Override
    public IShape createRect(float width, float height, float lineWidth) {
        return new Rect(width, height, lineWidth);
    }

    @Override
    public IShape createFilledCircle(float radius, int segments) {
        return new FilledCircle(radius, segments);
    }

    @Override
    public IShape createFilledRect(float width, float height) {
        return new FilledRect(width, height);
    }
}
