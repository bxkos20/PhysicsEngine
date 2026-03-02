package render.shapes.primitives;

import render.shapes.Shape;
import render.shapes.rawDataMesh.RawDataMesh;
import render.shapes.rawDataMesh.RawDataMeshFactory;

import java.util.Objects;

public class Circle extends Shape {
    private float radius;
    private int quality;

    public Circle(float radius, int quality) {
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
        return Float.compare(radius, circle.radius) == 0 && quality == circle.quality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius, quality);
    }

    @Override
    public RawDataMesh createRawDataMesh() {
        return RawDataMeshFactory.createCircle(radius, quality);
    }
}