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
 *   <li>Origin at bottom-left</li>
 *   <li>X increases right</li>
 *   <li>Y increases up</li>
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
     * Creates a filled rect mesh using triangle fan from center.
     *
     * @param rectFilled Circle to create mesh from
     * @return RawMesh with (quality + 1) vertices and (quality * 3) indices
     */
    public static RawDataMesh create(RectFilled rectFilled) {
        float width = rectFilled.width;
        float height = rectFilled.height;

        float[] vertexes = new float[]{
                0, 0,
                width, 0,
                width, height,
                0, height
        };

        short[] indexes = {0, 1, 3, 1, 2, 3};

        return new RawDataMesh(vertexes, indexes);
    }

    /**
     * Creates a filled circle mesh using triangle fan from center.
     *
     * @param rect Circle to create mesh from
     * @return RawMesh with (quality + 1) vertices and (quality * 3) indices
     */
    public static RawDataMesh create(Rect rect) {

        float deltaX = rect.width / 2;
        float deltaY = rect.height / 2;
        float size = rect.size;

        float[] vertexes = new float[]{
                - deltaX, - deltaY,
                deltaX, - deltaY,
                deltaX, deltaY,
                - deltaX, deltaY,
                size - deltaX, size - deltaY,
                deltaX - size, size - deltaY,
                deltaX - size, deltaY - size,
                size - deltaX, deltaY - size
        };

        short[] indexes = new short[]{
                0, 4, 1,
                1, 4, 5, // Down wall
                1, 5, 2,
                2, 5, 6, // Right wall
                2, 6, 3,
                3, 6, 7, // Up wall
                3, 7, 0,
                0, 7, 4 //Left wall
        };

        return new RawDataMesh(vertexes, indexes);
    }

    /**
     * Creates a filled circle mesh using triangle fan from center.
     *
     * @param circleFilled Circle to create mesh from
     * @return RawMesh with (quality + 1) vertices and (quality * 3) indices
     */
    public static RawDataMesh create(CircleFilled circleFilled)
    {
        float radius = circleFilled.radius;
        int quality = circleFilled.quality;

        float[] vertexes = new float[2 * (quality + 1)]; // 2 floats per vertex
        short[] indexes = new short[3 * quality];

        int vIndex = 0;
        int iIndex = 0;

        // The center
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
     * Creates a filled circle mesh using triangle fan from center.
     *
     * @param circle Circle to create mesh from
     * @return RawMesh with (quality + 1) vertices and (quality * 3) indices
     */
    public static RawDataMesh create(Circle circle) {
        float radius = circle.radius;
        int quality = circle.quality;
        float size = circle.size;
        float delta = radius / 2;

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
            vertexes[vIndex++] = cos * radius - delta;
            vertexes[vIndex++] = sin * radius - delta;

            // Inner ring vertex
            vertexes[vIndex++] = cos * innerRadius - delta;
            vertexes[vIndex++] = sin * innerRadius - delta;
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