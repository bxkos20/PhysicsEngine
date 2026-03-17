package backend.libgdx.primitives;

import backend.libgdx.render.shapes.Shape;
import backend.libgdx.rawDataMesh.RawDataMesh;
import backend.libgdx.rawDataMesh.RawDataMeshFactory;

public class FilledCircle extends Shape {
    private float radius;
    private int quality;

    public FilledCircle(float radius, int quality) {
        this.radius = radius;
        this.quality = quality;
        this.inicialiceKey();
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
}