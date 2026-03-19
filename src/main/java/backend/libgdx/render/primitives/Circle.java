package backend.libgdx.render.primitives;

import backend.libgdx.render.rawDataMesh.RawDataMesh;
import backend.libgdx.render.rawDataMesh.RawDataMeshFactory;
import backend.libgdx.render.shapes.Shape;

public class Circle extends Shape {
    private float radius;
    private int quality;
    private float size;

    public Circle(float radius, int quality, float size) {
        this.radius = radius;
        this.quality = quality;
        this.size = size;
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
        return RawDataMeshFactory.createCircleOutline(radius, quality, size);
    }
    
    @Override
    public float[] getParameters() {
        return new float[]{radius, quality, size};
    }
}