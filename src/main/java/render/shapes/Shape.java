package render.shapes;

import render.shapes.rawDataMesh.RawDataMesh;

public abstract class Shape {
    public abstract RawDataMesh createRawDataMesh();

    public abstract String getKey();
}