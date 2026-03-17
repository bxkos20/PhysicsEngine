package backend.libgdx.primitives;

import backend.libgdx.rawDataMesh.RawDataMesh;
import backend.libgdx.render.shapes.Shape;
import backend.libgdx.rawDataMesh.RawDataMeshFactory;

public class Rect extends Shape {
    private float width;
    private float height;
    private float size;

    public Rect(float width, float height, float size) {
        this.width = width;
        this.height = height;
        this.size = size;
        this.inicialiceKey();
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
        return RawDataMeshFactory.createRectOutline(width, height, size);
    }


}