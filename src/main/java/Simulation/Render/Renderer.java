package Simulation.Render;

import GameObject.Components.Core.ColliderComponent;
import GameObject.Components.Core.PhysicsComponent;
import GameObject.Components.Core.TransformComponent;
import GameObject.Components.Dot.DotComponent;
import GameObject.GameObject;
import GameObject.Components.ComponentRegistry;
import World.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class Renderer {
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    CameraController cameraController;



    public Renderer(int width, int height){
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(width / 2f, height / 2f, 0); // Centrar en el mundo
        camera.update();

        cameraController = new CameraController(camera);
        Gdx.input.setInputProcessor(cameraController); // Activar inputs
    }

    public void tick(World world){  //TODO: RenderComponent
        // Limpiar pantalla
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Dibujar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (GameObject gameObject : world.getGameObjects()){
            if (!gameObject.checkSignature(ComponentRegistry.getBit(TransformComponent.class) |
                    ComponentRegistry.getBit(DotComponent.class) |
                    ComponentRegistry.getBit(PhysicsComponent.class))) continue;

            ColliderComponent collider = gameObject.getComponent(ColliderComponent.class);
            TransformComponent transform = gameObject.getComponent(TransformComponent.class);
            DotComponent dot = gameObject.getComponent(DotComponent.class);

            shapeRenderer.setColor(dot.getDotType().COLOR);
            shapeRenderer.circle(transform.getPosition().x, transform.getPosition().y, collider.getRadius());
        }
        shapeRenderer.end();
    }
}
