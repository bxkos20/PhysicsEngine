package render.shapes.primitives;

import render.shapes.rawDataMesh.RawDataMesh;
import render.shapes.Shape;
import render.shapes.rawDataMesh.RawDataMeshFactory;

public class FilledRect extends Shape {
    private float width;
    private float height;

    public FilledRect(float width, int height) {
        this.width = width;
        this.height = height;
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
            return RawDataMeshFactory.createRect(width, height);
    }


}