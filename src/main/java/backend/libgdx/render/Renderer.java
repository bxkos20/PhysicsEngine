package backend.libgdx.render;

import backend.libgdx.render.camera.CameraController;
import backend.libgdx.render.rawDataMesh.RawDataMesh;
import backend.libgdx.render.shapes.MeshBatch;
import engine.graphics.Shape;
import backend.libgdx.render.shapes.ShapeRegistry;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ScreenUtils;
import engine.config.SimulationConfig;
import engine.ecs.ComponentRegistry;
import engine.ecs.components.RenderComponent;
import engine.ecs.components.TransformComponent;
import engine.graphics.Color;
import engine.graphics.interfaces.ICamera;
import engine.graphics.interfaces.IRenderer;

import java.util.LinkedList;
import java.util.Queue;

/**
 * LibGDX-based renderer implementing GPU-accelerated shape batching.
 * 
 * <p>Uses custom vertex and fragment shaders for efficient rendering of
 * thousands of shapes with minimal draw calls. Shapes are batched into
 * a single mesh and rendered in one GPU operation when possible.</p>
 * 
 * <h3>Rendering Pipeline:</h3>
 * <ol>
 *   <li>Clear screen and update camera projection</li>
 *   <li>Begin batch collection</li>
 *   <li>Add shapes with position and color to batch</li>
 *   <li>Flush batch to GPU when full or at frame end</li>
 * </ol>
 * 
 * @see MeshBatch
 * @see IRenderer
 */
public class Renderer implements IRenderer {
    /** Vertex shader: transforms vertex positions using projection matrix */
    private static final String VERTEX_SHADER =
            "attribute vec4 a_position;\n" +
                    "attribute vec4 a_color;\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "varying vec4 v_color;\n" +
                    "void main() {\n" +
                    "   v_color = a_color;\n" +
                    "   gl_Position = u_projTrans * a_position;\n" +
                    "}";

    // Fragment Shader: Pinta el píxel con el color interpolado
    private static final String FRAGMENT_SHADER =
            "#ifdef GL_ES\n" +
                    "precision mediump float;\n" +
                    "#endif\n" +
                    "varying vec4 v_color;\n" +
                    "void main() {\n" +
                    "   gl_FragColor = v_color;\n" +
                    "}";

    /** Compiled shader program for GPU rendering */
    private final ShaderProgram shader;
    
    /** Batch collector for efficient mesh rendering */
    private final MeshBatch meshBatch;

    /** Component ID for RenderData, cached for performance */
    private static final int RENDER_DATA_ID = ComponentRegistry.getId(RenderComponent.class);
    
    /** Component ID for TransformComponent, cached for performance */
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    
    /** Signature bitmask for entities with both render and transform components */
    private static final long REQUIRED_SIGNATURE = ComponentRegistry.idToBit(RENDER_DATA_ID) |
            ComponentRegistry.idToBit(TRANSFORM_ID);

    /** Active camera for view projection */
    ICamera camera;
    
    /** Optional camera controller for input handling */
    private CameraController cameraController;

    /** Start time for profiling */
    private long start;

    /** Last frame's render time in milliseconds (for profiling) */
    private float lastExecutionTimeMs;

    /**
     * Creates a renderer with the specified viewport dimensions and camera.
     *
     * @param camera Camera for view projection
     */
    public Renderer(ICamera camera) {
        ShaderProgram.pedantic = false; // Disable pedantic mode to allow unused shader variables
        shader = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (!shader.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        }
        
        // Initialize the batch with configuration size
        meshBatch = new MeshBatch(
            SimulationConfig.Rendering.BATCH_VERTICES, 
            SimulationConfig.Rendering.BATCH_INDICES
        );

        this.camera = camera;
        this.camera.update();

        // CameraController is handled by SimulationController, not here
    }

    /**
     * Flushes the current batch to the GPU and renders it.
     */
    private void flush() {
        Mesh meshToRender = meshBatch.end();
        if (meshToRender.getNumIndices() > 0) {
            meshToRender.render(shader, GL20.GL_TRIANGLES, 0, meshBatch.getIndexCount());
        }
    }

    /**
     * Returns profiling information for the last render call.
     *
     * @return Formatted string with render time in milliseconds
     */
    public String getProfilingInfo() {
        return String.format("Render: %.2fms", getLastExecutionTimeMs());
    }

    /**
     * Returns the last execution time for profiling.
     *
     * @return Execution time in milliseconds
     */
    private final int EXECUTION_AVERAGING_SIZE = 60;
    private final Queue<Float> executionTimes = new LinkedList<>();

    public float getLastExecutionTimeMs() {
        executionTimes.add(lastExecutionTimeMs);
        if (executionTimes.size() > EXECUTION_AVERAGING_SIZE) {
            executionTimes.poll();
        }
        float sum = 0;
        for (float time : executionTimes) {
            sum += time;
        }
        return sum / executionTimes.size();
    }

    /**
     * Begins a new render batch.
     * Clears the screen and prepares the shader for rendering.
     */
    @Override
    public void begin() {
        // Record start time for profiling
        start = java.lang.System.nanoTime();
        // Clear screen
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        
        // Update camera and setup shader
        camera.update();
        com.badlogic.gdx.math.Matrix4 gdxMatrix = 
            (com.badlogic.gdx.math.Matrix4) camera.getCombinedMatrix().getNativeMatrix();
        shader.bind();
        shader.setUniformMatrix("u_projTrans", gdxMatrix);

        // Setup blend mode
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        // Start mesh batch
        meshBatch.begin();
    }

    /**
     * Ends the current batch and flushes it to the GPU.
     */
    @Override
    public void end() {
        // Record end time for profiling
        this.lastExecutionTimeMs = (java.lang.System.nanoTime() - start) / 1_000_000f;

        // Flush batch to GPU
        flush();
    }

    /**
     * Draws a shape at the specified position with the given color.
     * 
     * @param shape The shape to draw
     * @param x     World X position
     * @param y     World Y position
     * @param color Color for the shape
     */
    @Override
    public void draw(Shape shape, float x, float y, Color color) {
        RawDataMesh rawDataMesh = ShapeRegistry.getRawDataMesh(shape);

        // If the batch is full, flush it and start a new one
        if (meshBatch.isFull(rawDataMesh)) {
            flush();
            meshBatch.begin();
        }

        meshBatch.draw(rawDataMesh, x, y, color.toFloatBits());
    }

    /**
     * Sets up camera input handling for the specified camera.
     * 
     * @param camera The camera to control
     */
    @Override
    public void setCamera(ICamera camera) {
        this.cameraController = new CameraController(camera);
        Gdx.input.setInputProcessor(cameraController);
    }
}