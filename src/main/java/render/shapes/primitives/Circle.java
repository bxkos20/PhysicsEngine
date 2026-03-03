package render.shapes.primitives;

import render.shapes.Shape;
import render.shapes.rawDataMesh.RawDataMesh;
import render.shapes.rawDataMesh.RawDataMeshFactory;

import java.util.Objects;

public class Circle extends Shape {
    private float radius;
    private int quality;
    private final String key;

    public Circle(float radius, int quality) {
        this.radius = radius;
        this.quality = quality;
        this.key = "C_" + radius + "_" + quality;
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
    public RawDataMesh createRawDataMesh() {
        return RawDataMeshFactory.createCircle(radius, quality);
    }

    @Override
    public String getKey() {
        return key;
    }
}