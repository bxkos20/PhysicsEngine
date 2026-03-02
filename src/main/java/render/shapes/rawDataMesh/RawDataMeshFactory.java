package render.shapes.rawDataMesh;

import com.badlogic.gdx.math.MathUtils;

public class RawDataMeshFactory {

    public static RawDataMesh createRect(float width, float height) {
        int vertexCount = 4;
        int indexCount = 6; // 2 triangles * 3 vertex each

        // Arrays para la GPU
        float[] vertexes = new float[vertexCount * 2]; // 3 floats per vertex
        short[] indexes = {0, 1, 3, 1, 2, 3};

        int vIndex = 0;
        int iIndex = 0;

        //Add the vertexes
        //0
        vertexes[vIndex++] = 0;
        vertexes[vIndex++] = 0;

        //1
        vertexes[vIndex++] = width;
        vertexes[vIndex++] = 0;

        //2
        vertexes[vIndex++] = width;
        vertexes[vIndex++] = height;

        //3
        vertexes[vIndex++] = 0;
        vertexes[vIndex++] = height;

        return new RawDataMesh(vertexes, indexes);
    }

    public static RawDataMesh createCircle(float radius, int quality) {
        int vertexCount = quality + 1; // q outlane + 1 center
        int indexCount = quality * 3; // q triangles * 3 vertex each

        // Arrays para la GPU
        float[] vertexes = new float[vertexCount * 2]; // 3 floats por vértice
        short[] indexes = new short[indexCount];

        int vIndex = 0;
        int iIndex = 0;

        //The center
        vertexes[vIndex++] = 0;
        vertexes[vIndex++] = 0;

        double angleIncrement = Math.PI * 2 / quality;
        for (int i = 0; i < quality; i++) {
            float angle = (float) (angleIncrement * i);
            vertexes[vIndex++] = MathUtils.cos(angle) * radius;
            vertexes[vIndex++] = MathUtils.sin(angle) * radius;
        }

        for (short i = 1; i <= quality; i++) {
            indexes[iIndex++] = 0;
            indexes[iIndex++] = i;
            indexes[iIndex++] = (i == quality) ? 1 : (short) (i + 1);
        }

        return new RawDataMesh(vertexes, indexes);
    }
}