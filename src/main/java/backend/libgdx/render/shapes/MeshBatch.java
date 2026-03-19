package backend.libgdx.render.shapes;

import backend.libgdx.render.rawDataMesh.RawDataMesh;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;

/**
 * GPU mesh batcher for efficient shape rendering.
 * 
 * <p>Collects multiple shapes into a single mesh to minimize draw calls.
 * Each shape is translated to its world position and colored before being
 * added to the batch.</p>
 * 
 * <h3>Vertex Format:</h3>
 * <ul>
 *   <li>Position: 2 floats (x, y)</li>
 *   <li>Color: 1 packed float (RGBA)</li>
 * </ul>
 * 
 * @see backend.libgdx.render.Renderer
 */
public class MeshBatch {
    /** The GPU mesh for rendering */
    private final Mesh mesh;
    
    /** Vertex data buffer [x, y, color, x, y, color, ...] */
    private final float[] vertices;
    
    /** Index data buffer */
    private final short[] indices;
    
    /** Current vertex count in the batch */
    private int vertexCount;
    
    /** Current index count in the batch */
    private int indexCount;
    
    /** Offset for indices (number of vertices, not floats) */
    private int vertexOffset;

    /**
     * Creates a mesh batch with specified capacity.
     * 
     * @param maxVertices Maximum number of vertices per batch
     * @param maxIndices  Maximum number of indices per batch
     */
    public MeshBatch(int maxVertices, int maxIndices) {
        // Create a dynamic mesh with Position and Color attributes
        this.mesh = new Mesh(false, maxVertices, maxIndices,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));

        this.vertices = new float[maxVertices * 3]; // 3 floats per vertex (x, y, color)
        this.indices = new short[maxIndices];
    }

    /**
     * Resets batch state for a new frame.
     * Clears vertex and index counters.
     */
    public void begin() {
        vertexCount = 0;
        indexCount = 0;
        vertexOffset = 0;
    }

    /**
     * Checks if adding the given mesh would exceed batch capacity.
     * 
     * @param rawDataMesh Mesh to test
     * @return true if batch would overflow
     */
    public boolean isFull(RawDataMesh rawDataMesh) {
        // Check if we have enough space for vertices (each vertex has 3 floats)
        // rawDataMesh.vertexes has 2 floats per vertex (x,y). So we need rawDataMesh.vertexes.length / 2 * 3 space.
        int floatsNeeded = (rawDataMesh.vertexes.length / 2) * 3;
        return vertexCount + floatsNeeded > vertices.length ||
               indexCount + rawDataMesh.indexes.length > indices.length;
    }

    /**
     * Adds a shape to the batch with translation and color.
     * 
     * @param rawDataMesh Source mesh data
     * @param x           World X position
     * @param y           World Y position  
     * @param color       RGBA color for all vertices
     */
    public void draw(RawDataMesh rawDataMesh, float x, float y, float colorBits) {
        float[] rawVertices = rawDataMesh.vertexes;
        short[] rawIndices = rawDataMesh.indexes;

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
     * Uploads batched data to GPU and returns renderable mesh.
     * 
     * @return Mesh ready for rendering with shader
     */
    public Mesh end() {
        if (vertexCount > 0) {
            mesh.setVertices(vertices, 0, vertexCount);
            mesh.setIndices(indices, 0, indexCount);
        }
        return mesh;
    }
    
    /**
     * Returns the number of indices currently in the batch.
     * 
     * @return Index count for glDrawElements
     */
    public int getIndexCount() {
        return indexCount;
    }
}