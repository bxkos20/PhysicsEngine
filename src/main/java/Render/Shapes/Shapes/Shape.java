package Render.Shapes.Shapes;

import com.badlogic.gdx.graphics.Color;
import java.util.Objects;

public abstract class Shape {
    protected Color color;

    public Shape(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}