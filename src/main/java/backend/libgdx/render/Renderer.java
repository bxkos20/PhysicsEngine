package backend.libgdx.render;

import backend.libgdx.factories.LibGDXShapeFactory;
import backend.libgdx.render.camera.CameraController;
import backend.libgdx.render.rawDataMesh.RawDataMesh;
import backend.libgdx.render.shapes.MeshBatch;
import backend.libgdx.render.shapes.Shape;
import backend.libgdx.render.shapes.ShapeRegistry;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ScreenUtils;
import engine.config.SimulationConfig;
import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.RenderData;
import engine.ecs.components.TransformComponent;
import engine.graphics.interfaces.ICamera;
import engine.graphics.interfaces.IRenderer;
import engine.graphics.interfaces.IShape;
import engine.world.World;

import java.util.List;

public class Renderer implements IRenderer {
    // Vertex Shader: Transforma la posición de los vértices usando una matriz
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

    private final ShaderProgram shader;
    private final MeshBatch meshBatch;

    private static final int RENDER_DATA_ID = ComponentRegistry.getId(RenderData.class);
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    private static final long REQUIRED_SIGNATURE = ComponentRegistry.idToBit(RENDER_DATA_ID) |
            ComponentRegistry.idToBit(TRANSFORM_ID);

    ICamera camera;
    private CameraController cameraController;
    private float lastExecutionTimeMs; // Para el profiling

    public Renderer(int width, int height, ICamera camera) {
        ShaderProgram.pedantic = false; // Permite que no usemos todas las variables
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
        this.camera.setPosition(width / 2f, height / 2f); // Center on world
        this.camera.update();

        // CameraController is handled by SimulationController, not here
    }

    public void tick(World world) {
        long start = java.lang.System.nanoTime();
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        camera.update();
        // Get native matrix from engine wrapper
        com.badlogic.gdx.math.Matrix4 gdxMatrix = 
            (com.badlogic.gdx.math.Matrix4) camera.getCombinedMatrix().getNativeMatrix();
        shader.bind();
        shader.setUniformMatrix("u_projTrans", gdxMatrix);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        List<GameObject> gameObjects = world.getGameObjects();

        meshBatch.begin();
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                
                RenderData renderData = gameObject.getComponent(RENDER_DATA_ID);
                TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);
                
                if (renderData != null && transform != null) {
                    // Recreate shape from data using factory
                    IShape shape = recreateShapeFromData(renderData);
                    
                    RawDataMesh rawDataMesh = ShapeRegistry.getRawDataMesh((Shape) shape);

                    // If the batch is full, flush it and start a new one
                    if (meshBatch.isFull(rawDataMesh)) {
                        flush();
                        meshBatch.begin();
                    }
                
                    meshBatch.draw(rawDataMesh, transform.getPosition().x, transform.getPosition().y, convertColor(renderData.color));
                }
            }
        }
        
        // Flush any remaining data at the end of the frame
        flush();

        this.lastExecutionTimeMs = (java.lang.System.nanoTime() - start) / 1_000_000f;
    }

    private void flush() {
        Mesh meshToRender = meshBatch.end();
        if (meshToRender.getNumIndices() > 0) {
            meshToRender.render(shader, GL20.GL_TRIANGLES, 0, meshBatch.getIndexCount());
        }
    }

    public String getProfilingInfo() {
        return String.format("Render: %.2fms", lastExecutionTimeMs);
    }
    
    private IShape recreateShapeFromData(RenderData renderData) {
        // Use LibGDX shape factory to recreate shape from data
        LibGDXShapeFactory factory = new LibGDXShapeFactory();
        
        switch (renderData.shapeType) {
            case "circle":
                return factory.createCircle(
                    renderData.shapeParams[0], // radius
                    (int)renderData.shapeParams[1], // quality;  
                    renderData.shapeParams[2]   // lineWidth
                );
            case "rect":
                return factory.createRect(
                    renderData.shapeParams[0], // width
                    renderData.shapeParams[1], // height
                    renderData.shapeParams[2]   // lineWidth
                );
            case "filled_circle":
                return factory.createFilledCircle(
                    renderData.shapeParams[0], // radius
                    (int)renderData.shapeParams[1]   // quality
                );
            case "filled_rect":
                return factory.createFilledRect(
                    renderData.shapeParams[0], // width
                    renderData.shapeParams[1]   // height
                );
            default:
                throw new IllegalArgumentException("Unknown shape type: " + renderData.shapeType);
        }
    }

    private Color convertColor(engine.graphics.Color color) {
        return new Color(color.r, color.g, color.b, color.a);
    }

    @Override
    public void begin() {
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

    @Override
    public void end() {
        flush();
    }

    @Override
    public void draw(IShape shape, float x, float y, engine.graphics.Color color) {
        RawDataMesh rawDataMesh = ShapeRegistry.getRawDataMesh((Shape)shape);

        // If the batch is full, flush it and start a new one
        if (meshBatch.isFull(rawDataMesh)) {
            flush();
            meshBatch.begin();
        }

        meshBatch.draw(rawDataMesh, x, y, convertColor(color));
    }

    @Override
    public void setCamera(ICamera camera) {
        this.cameraController = new CameraController(camera);
        Gdx.input.setInputProcessor(cameraController);
    }
}