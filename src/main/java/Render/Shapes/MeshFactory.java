package Render.Shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.MathUtils;

public class MeshFactory {

    public static Mesh createRect(float width, float height, Color color) {
        int vertexCount = 4;
        int indexCount = 6; // 2 triangles * 3 vertex each

        // Arrays para la GPU
        float[] vertexes = new float[vertexCount * 3]; // 3 floats por vértice
        short[] indexes = {0, 1, 3, 1, 2, 3};

        int vIndex = 0;
        int iIndex = 0;

        float colorBits = color.toFloatBits();

        //Add the vertexes
        //0
        vertexes[vIndex++] = 0;
        vertexes[vIndex++] = 0;
        vertexes[vIndex++] = colorBits;

        //1
        vertexes[vIndex++] = width;
        vertexes[vIndex++] = 0;
        vertexes[vIndex++] = colorBits;

        //2
        vertexes[vIndex++] = width;
        vertexes[vIndex++] = height;
        vertexes[vIndex++] = colorBits;

        //3
        vertexes[vIndex++] = 0;
        vertexes[vIndex++] = height;
        vertexes[vIndex++] = colorBits;

        Mesh mesh = new Mesh(true, vertexCount, indexCount,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));

        mesh.setVertices(vertexes);
        mesh.setIndices(indexes);

        return mesh;
    }

    public static Mesh createCircle(float radius, int quality, Color color) {
        int vertexCount = quality + 1; // q outlane + 1 center
        int indexCount = quality * 3; // q triangles * 3 vertex each

        // Arrays para la GPU
        float[] vertexes = new float[vertexCount * 3]; // 3 floats por vértice
        short[] indexes = new short[indexCount];

        int vIndex = 0;
        int iIndex = 0;

        float colorBits = color.toFloatBits();
        //The center
        vertexes[vIndex++] = 0;
        vertexes[vIndex++] = 0;
        vertexes[vIndex++] = colorBits;

        double angleIncrement = Math.PI * 2 / quality;
        for (int i = 0; i < quality; i++) {
            float angle = (float) (angleIncrement * i);
            vertexes[vIndex++] = MathUtils.cos(angle) * radius;
            vertexes[vIndex++] = MathUtils.sin(angle) * radius;
            vertexes[vIndex++] = colorBits;
        }

        for (short i = 1; i <= quality; i++) {
            indexes[iIndex++] = 0;
            indexes[iIndex++] = i;
            indexes[iIndex++] = (i == quality)? 1: (short) (i + 1);
        }


        Mesh mesh = new Mesh(true, vertexCount, indexCount,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));

        mesh.setVertices(vertexes);
        mesh.setIndices(indexes);

        return mesh;
    }
}