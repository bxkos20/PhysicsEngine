package Render.Shapes;

import Render.Shapes.rawDataMesh.RawDataMesh;

public abstract class Shape {
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public abstract RawDataMesh createRawDataMesh();
}