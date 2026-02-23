package Simulation.Render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class CameraController extends InputAdapter {
    private final OrthographicCamera camera;
    private final Vector3 lastTouch = new Vector3();

    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Zoom in/out
        camera.zoom += amountY * camera.zoom * 0.1f;

        // Limitar el zoom para que no se invierta ni se acerque demasiado
        if (camera.zoom < 0.1f) camera.zoom = 0.1f;
        if (camera.zoom > 10f) camera.zoom = 10f;

        camera.update();
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Guardamos la posición inicial al hacer clic derecho
        if (button == Input.Buttons.RIGHT) {
            // Unproject convierte coordenadas de pantalla a coordenadas del mundo
            camera.unproject(lastTouch.set(screenX, screenY, 0));
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Solo movemos si el clic derecho está presionado
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            // Calculamos la nueva posición del ratón en el mundo
            Vector3 newTouch = new Vector3(screenX, screenY, 0);
            camera.unproject(newTouch);

            // La diferencia es cuánto debemos mover la cámara
            // Nota: Restamos (last - new) para mover la cámara en dirección opuesta al arrastre
            camera.position.add(lastTouch.x - newTouch.x, lastTouch.y - newTouch.y, 0);

            camera.update();
            return true;
        }
        return false;
    }
}