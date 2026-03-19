package backend.libgdx.render.rawDataMesh;

/**
 * Raw mesh data container for GPU rendering.
 * Holds vertex positions and triangle indices before batching.
 * 
 * <p>Immutable once created - used as input for {@link backend.libgdx.render.shapes.MeshBatch}.</p>
 * 
 * @param vertexes Flat array of vertex positions [x0, y0, x1, y1, ...]
 * @param indexes  Triangle indices referencing vertex positions
 */
public class RawDataMesh {
    /** Flat array of vertex positions [x0, y0, x1, y1, ...] */
    public float[] vertexes;
    
    /** Triangle indices referencing vertex positions */
    public short[] indexes;

    /**
     * Creates a raw mesh data container.
     * 
     * @param vertexes Flat array of vertex positions
     * @param indexes  Triangle indices
     */
    public RawDataMesh(float[] vertexes, short[] indexes) {
        this.vertexes = vertexes;
        this.indexes = indexes;
    }
}
