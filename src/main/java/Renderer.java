import GameObject.GameObject;
import World.Board.Grid.ToroidalGridPartition;
import World.Board.ToroidalBoard;
import World.Collision.ElasticCollision;
import World.PhysicsWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Arrays;
import java.util.Random;

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

    public void tick(PhysicsWorld world){
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Dibujar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (GameObject obj : world.getObjects()) {
            if (obj.collider != null) {
                // Color basado en velocidad (Visualización chula)
                float speed = obj.physics != null ? obj.physics.velocity.len() : 0;
                shapeRenderer.setColor(obj.getColor());

                shapeRenderer.circle(obj.transform.position.x, obj.transform.position.y, obj.collider.radius);
            }
        }
        shapeRenderer.end();
    }
}
