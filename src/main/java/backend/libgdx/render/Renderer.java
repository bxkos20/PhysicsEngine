package backend.libgdx.render;

import backend.libgdx.render.shapes.MeshBatch;
import backend.libgdx.render.shapes.ShapeRegistry;
import engine.ecs.ComponentRegistry;
import engine.ecs.components.RendererComponent;
import engine.ecs.components.TransformComponent;
import engine.ecs.GameObject;
import backend.libgdx.render.camera.CameraController;
import backend.libgdx.rawDataMesh.RawDataMesh;
import engine.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.List;

public class Renderer {
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

    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    private static final int RENDERER_ID = ComponentRegistry.getId(RendererComponent.class);
    private static final long REQUIRED_SIGNATURE = ComponentRegistry.idToBit(RENDERER_ID) |
            ComponentRegistry.idToBit(TRANSFORM_ID);

    OrthographicCamera camera;
    CameraController cameraController;
    private float lastExecutionTimeMs; // Para el profiling

    public Renderer(int width, int height) {
        ShaderProgram.pedantic = false; // Permite que no usemos todas las variables
        shader = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (!shader.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        }
        
        // Initialize the batch with a reasonable size
        meshBatch = new MeshBatch(10000, 20000);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(width / 2f, height / 2f, 0); // Centrar en el mundo
        camera.update();

        cameraController = new CameraController(camera);
        Gdx.input.setInputProcessor(cameraController); // Activar inputs
    }

    public void tick(World world) {
        long start = java.lang.System.nanoTime();
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        camera.update();
        shader.bind();
        shader.setUniformMatrix("u_projTrans", camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        List<GameObject> gameObjects = world.getGameObjects();

        meshBatch.begin();
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                
                RendererComponent rendererComponent = gameObject.getComponent(RENDERER_ID);
                TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);
                
                RawDataMesh rawDataMesh = ShapeRegistry.getRawDataMesh(rendererComponent.getShape());

                // If the batch is full, flush it and start a new one
                if (meshBatch.isFull(rawDataMesh)) {
                    flush();
                    meshBatch.begin();
                }
                
                meshBatch.draw(rawDataMesh, transform.getPosition().x, transform.getPosition().y, rendererComponent.getColor());
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
        return String.format("Render: %.2fms",
                lastExecutionTimeMs);
    }
}