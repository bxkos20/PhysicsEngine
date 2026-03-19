package backend.libgdx.render.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import engine.graphics.interfaces.ICamera;
import engine.math.Vector2;
import engine.math.Vector3;

/**
 * Input handler for camera pan and zoom controls.
 * 
 * <p>Extends LibGDX's {@link InputAdapter} to process mouse events:</p>
 * <ul>
 *   <li>Mouse wheel - Zoom in/out (0.1x to 10x range)</li>
 *   <li>Right-click drag - Pan camera</li>
 * </ul>
 * 
 * @see ICamera
 */
public class CameraController extends InputAdapter {
    /** The camera being controlled */
    private final ICamera camera;
    
    /** Last mouse position for drag delta calculation */
    private final Vector3 lastTouch = new Vector3();

    private Vector2 tempVector = new Vector2();

    /**
     * Creates a controller for the specified camera.
     * 
     * @param camera The camera to control
     */
    public CameraController(ICamera camera) {
        this.camera = camera;
    }

    /**
     * Handles mouse scroll wheel input for zoom control.
     * Zoom is clamped to range [0.1, 10.0] to prevent inversion or extreme zoom.
     * 
     * @param amountX Horizontal scroll amount (unused)
     * @param amountY Vertical scroll amount (positive = scroll up = zoom out)
     * @return true to indicate event was consumed
     */
    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Zoom in/out
        float zoom = camera.getZoom() + amountY * camera.getZoom() * 0.1f;

        // Clamp zoom to prevent inversion or extreme values
        if (zoom < 0.1f) zoom = 0.1f;
        if (zoom > 10f) zoom = 10f;

        camera.setZoom(zoom);

        camera.update();
        return true;
    }

    /**
     * Records initial mouse position when right-click is pressed.
     * 
     * @param screenX Mouse X position in screen coordinates
     * @param screenY Mouse Y position in screen coordinates
     * @param pointer Pointer index (for multi-touch)
     * @param button  Mouse button that was pressed
     * @return true if right-click, false otherwise
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            // Store the initial position
            lastTouch.set(screenX, screenY, 0);
            return true;
        }
        return false;
    }

    /**
     * Handles camera panning during right-click drag.
     * Camera moves opposite to mouse direction for natural "grab and drag" feel.
     * 
     * @param screenX Current mouse X position
     * @param screenY Current mouse Y position
     * @param pointer Pointer index
     * @return true if right-dragging, false otherwise
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            // Calculate the difference in screen coordinates
            float deltaX = screenX - lastTouch.x;
            float deltaY = screenY - lastTouch.y;
            
            // Get current camera position
            camera.getPosition(tempVector);
            
            // Move camera in opposite direction of mouse drag
            // For natural drag: when mouse moves right, camera moves left
            // When mouse moves up, camera moves down
            tempVector.add(- deltaX * camera.getZoom(), + deltaY * camera.getZoom());

            camera.setPosition(tempVector);
            
            // Update last touch position
            lastTouch.set(screenX, screenY, 0);
            
            camera.update();
            return true;
        }
        return false;
    }
}