package Render.Shapes.Shapes;

import com.badlogic.gdx.graphics.Color;
import java.util.Objects;

public class Circle extends Shape {
    private float radius;
    private int quality;

    public Circle(Color color, float radius, int quality) {
        super(color);
        this.radius = radius;
        this.quality = quality;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Circle circle = (Circle) o;
        return Float.compare(radius, circle.radius) == 0 && quality == circle.quality && color.equals(circle.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, radius, quality);
    }
}