package backend.libgdx.render.rawDataMesh;

import engine.graphics.shapes.Circle;
import engine.graphics.shapes.CircleFilled;
import engine.graphics.shapes.Rect;
import engine.graphics.shapes.RectFilled;
import engine.graphics.Shape;
import com.badlogic.gdx.math.MathUtils;

/**
 * Factory for creating raw mesh data for primitive shapes.
 *
 * <p>Generates vertex and index arrays for GPU rendering without
 * allocating mesh objects. Used by {@link Shape} implementations to
 * define their geometry.</p>
 *
 * <h3>Coordinate System:</h3>
 * <ul>
 * <li>Origin at center (0,0)</li>
 * <li>X increases right</li>
 * <li>Y increases up</li>
 * </ul>
 */
public class RawDataMeshFactory {

    public static RawDataMesh create(Shape shape) {
        if (shape instanceof RectFilled) return create((RectFilled) shape);
        if (shape instanceof Rect) return create((Rect) shape);
        if (shape instanceof CircleFilled) return create((CircleFilled) shape);
        if (shape instanceof Circle) return create((Circle) shape);
        throw new IllegalArgumentException("Unknown shape type: " + shape.getClass());
    }

    /**
     * Creates a filled rect mesh centered at (0,0).
     *
     * @param rectFilled Rectangle to create mesh from
     * @return RawMesh with 4 vertices and 6 indices
     */
    public static RawDataMesh create(RectFilled rectFilled) {
        float hw = rectFilled.width / 2f;
        float hh = rectFilled.height / 2f;

        float[] vertexes = new float[]{
                -hw, -hh,  // Bottom-Left
                hw, -hh,  // Bottom-Right
                hw,  hh,  // Top-Right
                -hw,  hh   // Top-Left
        };

        short[] indexes = {0, 1, 3, 1, 2, 3};

        return new RawDataMesh(vertexes, indexes);
    }

    /**
     * Creates a hollow rect mesh centered at (0,0).
     *
     * @param rect Rectangle to create mesh from
     * @return RawMesh with 8 vertices and 24 indices
     */
    public static RawDataMesh create(Rect rect) {
        float hw = rect.width / 2f;
        float hh = rect.height / 2f;
        float size = rect.size; // Line thickness

        float[] vertexes = new float[]{
                // Outer rectangle
                -hw, -hh,  // 0: Outer Bottom-Left
                hw, -hh,  // 1: Outer Bottom-Right
                hw,  hh,  // 2: Outer Top-Right
                -hw,  hh,  // 3: Outer Top-Left

                // Inner rectangle
                -hw + size, -hh + size, // 4: Inner Bottom-Left
                hw - size, -hh + size, // 5: Inner Bottom-Right
                hw - size,  hh - size, // 6: Inner Top-Right
                -hw + size,  hh - size  // 7: Inner Top-Left
        };

        short[] indexes = new short[]{
                0, 4, 1,
                1, 4, 5, // Down wall
                1, 5, 2,
                2, 5, 6, // Right wall
                2, 6, 3,
                3, 6, 7, // Up wall
                3, 7, 0,
                0, 7, 4  // Left wall
        };

        return new RawDataMesh(vertexes, indexes);
    }

    /**
     * Creates a filled circle mesh using triangle fan centered at (0,0).
     *
     * @param circleFilled Circle to create mesh from
     * @return RawMesh with (quality + 1) vertices and (quality * 3) indices
     */
    public static RawDataMesh create(CircleFilled circleFilled) {
        float radius = circleFilled.radius;
        int quality = circleFilled.quality;

        float[] vertexes = new float[2 * (quality + 1)]; // 2 floats per vertex
        short[] indexes = new short[3 * quality];

        int vIndex = 0;
        int iIndex = 0;

        // The center (0,0)
        vertexes[vIndex++] = 0;
        vertexes[vIndex++] = 0;

        double angleIncrement = Math.PI * 2 / quality;
        for (int i = 0; i < quality; i++) {
            float angle = (float) (angleIncrement * i);
            float x = (float) (Math.cos(angle) * radius);
            float y = (float) (Math.sin(angle) * radius);
            vertexes[vIndex++] = x;
            vertexes[vIndex++] = y;
        }

        for (int i = 0; i < quality; i++) {
            int next = (i + 1) % quality;
            indexes[iIndex++] = 0;
            indexes[iIndex++] = (short) (i + 1);
            indexes[iIndex++] = (short) (next + 1);
        }

        return new RawDataMesh(vertexes, indexes);
    }

    /**
     * Creates a hollow circle mesh centered at (0,0).
     *
     * @param circle Circle to create mesh from
     * @return RawMesh with (quality * 2) vertices and (quality * 6) indices
     */
    public static RawDataMesh create(Circle circle) {
        float radius = circle.radius;
        int quality = circle.quality;
        float size = circle.size; // Line thickness

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