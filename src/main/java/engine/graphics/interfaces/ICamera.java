package engine.graphics.interfaces;

import engine.math.Matrix4;
import engine.math.Vector2;

public interface ICamera {
    void update();
    void setPosition(float x, float y);
    void setPosition(Vector2 position);
    void setZoom(float zoom);
    float getX();
    float getY();
    float getZoom();
    Vector2 getPosition();
    Matrix4 getCombinedMatrix();
}