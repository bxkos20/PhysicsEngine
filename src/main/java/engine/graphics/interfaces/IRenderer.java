package engine.graphics.interfaces;

import engine.graphics.Color;
import engine.world.World;

public interface IRenderer {
    void begin();
    void end();
    void draw(IShape shape, float x, float y, Color color);
    void setCamera(ICamera camera);
    void tick(World world);
}