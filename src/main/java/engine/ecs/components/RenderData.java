package engine.ecs.components;

import engine.graphics.Color;

public class RenderData {
    public Color color;
    public String shapeType; // "circle", "rect", "filled_circle", "filled_rect"
    public float[] shapeParams; // [radius, quality, lineWidth] or [width, height]
    
    public RenderData(Color color, String shapeType, float[] shapeParams) {
        this.color = color;
        this.shapeType = shapeType;
        this.shapeParams = shapeParams;
    }
}
