package backend.libgdx.render.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import engine.graphics.interfaces.ICamera;
import engine.math.Vector2;
import engine.math.Vector3;

public class CameraController extends InputAdapter {
    private final ICamera camera;
    private final Vector3 lastTouch = new Vector3();

    public CameraController(ICamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Zoom in/out
        float zoom = camera.getZoom() + amountY * camera.getZoom() * 0.1f;

        // Limitar el zoom para que no se invierta ni se acerque demasiado
        if (zoom < 0.1f) zoom = 0.1f;
        if (zoom > 10f) zoom = 10f;

        camera.setZoom(zoom);

        camera.update();
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            // Store the initial position
            lastTouch.set(screenX, screenY, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            // Calculate the difference in screen coordinates
            float deltaX = screenX - lastTouch.x;
            float deltaY = screenY - lastTouch.y;
            
            // Get current camera position
            Vector2 currentPos = camera.getPosition();
            
            // Move camera in opposite direction of mouse drag
            // For natural drag: when mouse moves right, camera moves left
            // When mouse moves up, camera moves down
            float newX = currentPos.x - deltaX * camera.getZoom();
            float newY = currentPos.y + deltaY * camera.getZoom(); // Note: + for Y because screen Y is inverted
            
            camera.setPosition(newX, newY);
            
            // Update last touch position
            lastTouch.set(screenX, screenY, 0);
            
            camera.update();
            return true;
        }
        return false;
    }
}