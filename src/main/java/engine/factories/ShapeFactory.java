package engine.factories;

import engine.graphics.interfaces.IShape;

public interface ShapeFactory {
    IShape createCircle(float radius, int segments, float lineWidth);
    IShape createRect(float width, float height, float lineWidth);
    IShape createFilledCircle(float radius, int segments);
    IShape createFilledRect(float width, float height);
}
