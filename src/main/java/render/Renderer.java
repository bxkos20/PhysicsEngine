package render;

import gameObject.components.ComponentRegistry;
import gameObject.components.core.RendererComponent;
import gameObject.components.core.TransformComponent;
import gameObject.GameObject;
import render.camera.CameraController;
import render.shapes.ShapeRegistry;
import world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
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
    private final Matrix4 transformMatrix = new Matrix4();

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

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        List<GameObject> gameObjects = world.getGameObjects();

        // REVERTIDO: Bucle secuencial en el hilo principal (OpenGL Thread)
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                processGameObject(gameObject);
            }
        }

        this.lastExecutionTimeMs = (java.lang.System.nanoTime() - start) / 1_000_000f;
    }

    private void processGameObject(GameObject gameObject) {
        RendererComponent rendererComponent = gameObject.getComponent(RENDERER_ID);
        TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);

        // 1. Obtener o Crear la Malla (Caching con LongMap - Zero Garbage)
        Mesh mesh = ShapeRegistry.getMesh(rendererComponent.getShape());

        // 2. Calcular Matriz de Transformación (Mover el círculo a su sitio)
        // Matriz = Proyección (Cámara) * Traslación (Objeto)
        transformMatrix.set(camera.combined);
        transformMatrix.translate(transform.getPosition().x, transform.getPosition().y, 0);

        // 3. Enviar al Shader y Dibujar
        shader.setUniformMatrix("u_projTrans", transformMatrix);
        mesh.render(shader, GL20.GL_TRIANGLES);
    }

    public String getProfilingInfo() {
        return String.format("Render: %.2fms",
                lastExecutionTimeMs);
    }
}