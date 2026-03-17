package backend.libgdx.render.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import backend.libgdx.rawDataMesh.RawDataMesh;

public class MeshBatch {
    private final Mesh mesh;
    private final float[] vertices;
    private final short[] indices;
    private int vertexCount;
    private int indexCount;
    private int vertexOffset; // Offset for indices (number of vertices, not floats)

    public MeshBatch(int maxVertices, int maxIndices) {
        // Create a dynamic mesh with Position and Color attributes
        this.mesh = new Mesh(false, maxVertices, maxIndices,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));

        this.vertices = new float[maxVertices * 3]; // 3 floats per vertex (x, y, color)
        this.indices = new short[maxIndices];
    }

    public void begin() {
        vertexCount = 0;
        indexCount = 0;
        vertexOffset = 0;
    }

    /**
     * Checks if adding the given raw mesh would overflow the batch buffers.
     */
    public boolean isFull(RawDataMesh rawDataMesh) {
        // Check if we have enough space for vertices (each vertex has 3 floats)
        // rawDataMesh.vertexes has 2 floats per vertex (x,y). So we need rawDataMesh.vertexes.length / 2 * 3 space.
        int floatsNeeded = (rawDataMesh.vertexes.length / 2) * 3;
        return vertexCount + floatsNeeded > vertices.length ||
               indexCount + rawDataMesh.indexes.length > indices.length;
    }

    public void draw(RawDataMesh rawDataMesh, float x, float y, Color color) {
        float[] rawVertices = rawDataMesh.vertexes;
        short[] rawIndices = rawDataMesh.indexes;

        float colorBits = color.toFloatBits();

        // Copy indices with offset adjustment
        for (int i = 0; i < rawIndices.length; i++) {
            indices[indexCount++] = (short) (rawIndices[i] + vertexOffset);
        }

        // Copy vertices with translation and color
        for (int i = 0; i < rawVertices.length; i += 2) {
            vertices[vertexCount++] = rawVertices[i] + x;
            vertices[vertexCount++] = rawVertices[i + 1] + y;
            vertices[vertexCount++] = colorBits;
        }

        // Update the vertex offset for the next shape
        // rawVertices.length is the number of floats. Each vertex has 2 floats (x,y) in raw data.
        vertexOffset += rawVertices.length / 2;
    }

    /**
     * Uploads the data to the GPU and returns the Mesh ready for rendering.
     * Call mesh.core.render() after this.
     */
    public Mesh end() {
        if (vertexCount > 0) {
            mesh.setVertices(vertices, 0, vertexCount);
            mesh.setIndices(indices, 0, indexCount);
        }
        return mesh;
    }
    
    public int getIndexCount() {
        return indexCount;
    }
}