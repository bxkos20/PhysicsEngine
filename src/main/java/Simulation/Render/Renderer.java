package Simulation.Render;

import GameObject.Components.ComponentRegistry;
import GameObject.Components.Core.PhysicsComponent;
import GameObject.Components.Core.RendererComponent;
import GameObject.Components.Core.TransformComponent;
import GameObject.GameObject;
import World.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class Renderer {
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    private static final int RENDERER_ID = ComponentRegistry.getId(RendererComponent.class);

    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    CameraController cameraController;
    private float lastExecutionTimeMs; // Para el profiling

    public Renderer(int width, int height){
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(width / 2f, height / 2f, 0); // Centrar en el mundo
        camera.update();

        cameraController = new CameraController(camera);
        Gdx.input.setInputProcessor(cameraController); // Activar inputs
    }

    public void tick(World world){
        long start = java.lang.System.nanoTime();
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Dibujar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (GameObject gameObject : world.getGameObjects()){
            if (!gameObject.checkSignature(ComponentRegistry.idToBit(RENDERER_ID) |
                    ComponentRegistry.idToBit(TRANSFORM_ID))) continue;

            RendererComponent rendererComponent = gameObject.getComponent(RENDERER_ID);
            TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);

            shapeRenderer.setColor(rendererComponent.getColor());
            shapeRenderer.circle(transform.getPosition().x, transform.getPosition().y, rendererComponent.getRadius());
        }

        shapeRenderer.end();
        this.lastExecutionTimeMs = (java.lang.System.nanoTime() - start) / 1_000_000f;
    }

    public String getProfilingInfo() {
        return String.format("Render: %.2fms",
                lastExecutionTimeMs);
    }
}
