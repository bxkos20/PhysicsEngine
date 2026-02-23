import GameObject.GameObject;
import World.Board.Grid.ToroidalGridPartition;
import World.Collision.ElasticCollision;
import World.PhysicsWorld;
import World.Board.ToroidalBoard;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Arrays;
import java.util.Random;

public class Simulation extends ApplicationAdapter {
    Renderer renderer;
    PhysicsWorld world;
    private final int width;
    private final int height;

    // Velocidad de la simulación (1.0f = tiempo real, 2.0f = doble, etc.)
    public float timeScale = 1.0f;

    // Paso de física fijo (60 veces por segundo). Cuanto más pequeño, más precisa la física.
    private final float FIXED_TIME_STEP = 1 / 60f;

    // Acumulador para guardar el tiempo "sobrante" entre frames
    private float accumulator = 0f;

    final int totalDots = 500;

    public Simulation(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void create() {
        ToroidalBoard board = new ToroidalBoard(width, height);
        ElasticCollision collision = new ElasticCollision();
        ToroidalGridPartition gridPartition = new ToroidalGridPartition(width, height, 50);
        world = new PhysicsWorld(board, collision, gridPartition);
        renderer = new Renderer(width, height);
        
        Random random = new Random();

        DotType.randomizeInteraction();
        System.out.println(Arrays.deepToString(DotType.INTERACTION));

        int dotPerType = totalDots / DotType.values().length;

        for (int i = 0; i < DotType.values().length; i++) {
            for (int j = 0; j < dotPerType; j++) {
                float x = random.nextFloat() * width;
                float y = random.nextFloat() * height;

                GameObject bola = new Dot(x, y, DotType.values()[i]);

                world.addObject(bola);
            }
        }
    }

    @Override
    public void render() {
        // Limpiar pantalla
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        // 1. Calculamos cuánto tiempo "simulado" ha pasado en este frame real
        float frameTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f); // Limitamos para evitar espiral de la muerte si se cuelga
        accumulator += frameTime * timeScale;

        // 2. Ejecutamos la física en pasos fijos hasta consumir el tiempo acumulado
        while (accumulator >= FIXED_TIME_STEP) {
            world.update(FIXED_TIME_STEP);
            accumulator -= FIXED_TIME_STEP;
        }

        renderer.tick(world);

        // Mostrar FPS en el título
        Gdx.graphics.setTitle("Simulation - FPS: " + Gdx.graphics.getFramesPerSecond() + " - TimeScale: " + timeScale);

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) timeScale *= 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) timeScale /= 2;
    }
}
