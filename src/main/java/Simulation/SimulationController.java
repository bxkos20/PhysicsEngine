package Simulation;

import GameObject.Components.Core.ColliderComponent;
import GameObject.Components.Core.PhysicsComponent;
import GameObject.Components.Core.TransformComponent;
import GameObject.Components.Dot.DotComponent;
import GameObject.Components.Dot.DotType;
import GameObject.GameObject;
import Simulation.Render.Renderer;
import World.Board.Grid.ToroidalGridPartition;
import World.Collision.ElasticCollision;
import World.Board.ToroidalBoard;
import World.World;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.Random;

public class SimulationController extends ApplicationAdapter {
    Renderer renderer;
    World world;
    private final int width;
    private final int height;

    // Velocidad de la simulación (1.0f = tiempo real, 2.0f = doble, etc.)
    public float timeScale = 1.0f;

    public boolean render = true;

    // Paso de física fijo (60 veces por segundo). Cuanto más pequeño, más precisa la física.
    private final float FIXED_TIME_STEP = 1 / 60f;

    // Acumulador para guardar el tiempo "sobrante" entre frames
    private float accumulator = 0f;

    final int totalDots = 500;

    public SimulationController(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void create() {
        ToroidalBoard board = new ToroidalBoard(width, height);
        ElasticCollision collision = new ElasticCollision();
        ToroidalGridPartition gridPartition = new ToroidalGridPartition(width, height, 25);
        world = new World(board, collision, gridPartition);
        renderer = new Renderer(width, height);
        
        Random random = new Random();

        DotType.randomizeInteraction();
        System.out.println(Arrays.deepToString(DotType.INTERACTION));

        int dotPerType = totalDots / DotType.values().length;

        for (int i = 0; i < DotType.values().length; i++) {
            for (int j = 0; j < dotPerType; j++) {
                float x = random.nextFloat() * width;
                float y = random.nextFloat() * height;

                GameObject dot = new GameObject();
                dot.addComponent(new ColliderComponent(5));
                dot.addComponent(new PhysicsComponent(1, 1f, 0.01f));
                dot.addComponent(new TransformComponent(x,y));
                dot.addComponent(new DotComponent(DotType.values()[i]));

                world.addObject(dot);
            }
        }
    }

    @Override
    public void render() {
        // 1. Calculamos cuánto tiempo "simulado" ha pasado en este frame real
        float frameTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f); // Limitamos para evitar espiral de la muerte si se cuelga
        accumulator += frameTime * timeScale;

        // 2. Ejecutamos la física en pasos fijos hasta consumir el tiempo acumulado
        while (accumulator >= FIXED_TIME_STEP) {
            world.update(FIXED_TIME_STEP);
            accumulator -= FIXED_TIME_STEP;
        }

        if (render)
            renderer.tick(world);

        // Mostrar FPS en el título
        Gdx.graphics.setTitle("Simulation.Simulation - FPS: " + Gdx.graphics.getFramesPerSecond() + " - TimeScale: " + timeScale +
                " | " + world.getProfilingInfo());

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) timeScale *= 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) timeScale /= 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) timeScale += 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) timeScale -= 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) render = !render;
    }
}
