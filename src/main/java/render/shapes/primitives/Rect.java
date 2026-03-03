package render.shapes.primitives;

import render.shapes.rawDataMesh.RawDataMesh;
import render.shapes.Shape;
import render.shapes.rawDataMesh.RawDataMeshFactory;
import com.badlogic.gdx.graphics.Color;
import java.util.Objects;

public class Rect extends Shape {
    private float width;
    private float height;
    private final String key;

    public Rect(float width, int height) {
        this.width = width;
        this.height = height;
        this.key = "R_" + width + "_" + height;
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
    public RawDataMesh createRawDataMesh() {
        return RawDataMeshFactory.createRect(width, height);
    }

    @Override
    public String getKey() {
        return key;
    }


}