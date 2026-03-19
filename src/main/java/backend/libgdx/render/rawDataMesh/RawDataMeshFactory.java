package backend.libgdx.render.rawDataMesh;

import com.badlogic.gdx.math.MathUtils;

/**
 * Factory for creating raw mesh data for primitive shapes.
 * 
 * <p>Generates vertex and index arrays for GPU rendering without
 * allocating mesh objects. Used by {@link backend.libgdx.render.shapes.Shape} implementations to
 * define their geometry.</p>
 * 
 * <h3>Coordinate System:</h3>
 * <ul>
 *   <li>Origin at bottom-left</li>
 *   <li>X increases right</li>
 *   <li>Y increases up</li>
 * </ul>
 */
public class RawDataMeshFactory {

    /**
     * Creates a filled rectangle mesh (quad).
     * 
     * <p>Vertex layout:</p>
     * <pre>
     *   3---2
     *   |   |
     *   0---1
     * </pre>
     * 
     * @param width  Rectangle width
     * @param height Rectangle height
     * @return RawMesh with 4 vertices and 6 indices (2 triangles)
     */
    public static RawDataMesh createRect(float width, float height) {
        int vertexCount = 4;

        float[] vertexes = new float[vertexCount * 2]; // 2 floats per vertex
        short[] indexes = {0, 1, 3, 1, 2, 3};

        int vIndex = 0;

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

    /**
     * Creates an outlined rectangle mesh (hollow).
     * Uses 8 vertices (4 outer, 4 inner) to create line geometry.
     * 
     * @param width  Outer rectangle width
     * @param height Outer rectangle height
     * @param size   Line thickness (inset from outer edge)
     * @return RawMesh with 8 vertices and 24 indices (8 triangles)
     */
    public static RawDataMesh createRectOutline(float width, float height, float size) {
        int vertexCount = 8;
        int indexCount = 24; // 8 triangles * 3 vertex each

        // Arrays para la GPU
        float[] vertexes = new float[vertexCount * 2]; // 2 floats per vertex
        short[] indexes = new short[indexCount];

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

        //4
        vertexes[vIndex++] = size;
        vertexes[vIndex++] = size;

        //5
        vertexes[vIndex++] = width - size;
        vertexes[vIndex++] = size;

        //6
        vertexes[vIndex++] = width - size;
        vertexes[vIndex++] = height - size;

        //7
        vertexes[vIndex++] = size;
        vertexes[vIndex++] = height - size;

        short l = 4;

        for (short i = 0; i < l; i++) {
            short x = (i + 1 == l) ? 0 : (short) (i + 1);
            //1
            indexes[iIndex++] = i;
            indexes[iIndex++] = (short) (l + i);
            indexes[iIndex++] = x;

            //2
            indexes[iIndex++] = (short) (l + i);
            indexes[iIndex++] = (short) (l + x);
            indexes[iIndex++] = x;
        }

        return new RawDataMesh(vertexes, indexes);
    }

    /**
     * Creates a filled circle mesh using triangle fan from center.
     * 
     * @param radius  Circle radius
     * @param quality Number of segments (triangles) around circumference
     * @return RawMesh with (quality + 1) vertices and (quality * 3) indices
     */
    public static RawDataMesh createCircle(float radius, int quality) {
        int vertexCount = quality + 1; // q outlane + 1 center
        int indexCount = quality * 3; // q triangles * 3 vertex each

        // Arrays para la GPU
        float[] vertexes = new float[vertexCount * 2]; // 2 floats por vértice
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

    /**
     * Creates an outlined circle mesh (ring) using inner/outer rings.
     * 
     * @param radius  Outer circle radius
     * @param quality Number of segments around circumference
     * @param size    Line thickness (radius - innerRadius)
     * @return RawMesh with (quality * 2) vertices and (quality * 6) indices
     */
    public static RawDataMesh createCircleOutline(float radius, int quality, float size) {
        int vertexCount = quality * 2;
        int indexCount = quality * 6; // quality * 2 triangles * 3 vertices

        float[] vertexes = new float[vertexCount * 2];
        short[] indexes = new short[indexCount];

        int vIndex = 0;
        int iIndex = 0;

        float innerRadius = radius - size;
        double angleIncrement = Math.PI * 2 / quality;

        for (int i = 0; i < quality; i++) {
            float angle = (float) (angleIncrement * i);
            float cos = MathUtils.cos(angle);
            float sin = MathUtils.sin(angle);

            // Outer ring vertex
            vertexes[vIndex++] = cos * radius;
            vertexes[vIndex++] = sin * radius;

            // Inner ring vertex
            vertexes[vIndex++] = cos * innerRadius;
            vertexes[vIndex++] = sin * innerRadius;
        }

        for (int i = 0; i < quality; i++) {
            short p0 = (short) (i * 2);         // Outer current
            short p1 = (short) (i * 2 + 1);     // Inner current
            short p2 = (short) ((i * 2 + 2) % (quality * 2)); // Outer next
            short p3 = (short) ((i * 2 + 3) % (quality * 2)); // Inner next

            // Triangle 1
            indexes[iIndex++] = p0;
            indexes[iIndex++] = p1;
            indexes[iIndex++] = p2;

            // Triangle 2
            indexes[iIndex++] = p1;
            indexes[iIndex++] = p3;
            indexes[iIndex++] = p2;
        }

        return new RawDataMesh(vertexes, indexes);
    }
}
