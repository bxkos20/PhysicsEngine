package Render.Shapes.primitives;

import Render.Shapes.rawDataMesh.RawDataMesh;
import Render.Shapes.Shape;
import Render.Shapes.rawDataMesh.RawDataMeshFactory;
import com.badlogic.gdx.graphics.Color;
import java.util.Objects;

public class Rect extends Shape {
    private float width;
    private float height;

    public Rect(Color color, float width, int height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rect rect = (Rect) o;
        return Float.compare(width, rect.width) == 0 && Float.compare(height, rect.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public RawDataMesh createRawDataMesh() {
        return RawDataMeshFactory.createRect(width, height);
    }


}