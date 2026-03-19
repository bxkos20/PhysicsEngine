package backend.libgdx.render.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import engine.graphics.interfaces.ICamera;
import engine.math.Vector2;

/**
 * LibGDX implementation of the {@link ICamera} interface.
 * Wraps an {@link OrthographicCamera} for 2D rendering.
 * 
 * <p>Provides viewport management, position/zoom control, and projection
 * matrix generation for the rendering system.</p>
 * 
 * @see ICamera
 * @see OrthographicCamera
 */
public class LibGDXCamera implements ICamera {
    /** The underlying LibGDX orthographic camera */
    private final OrthographicCamera orthoCamera;

    /**
     * Creates an orthographic camera with the specified viewport dimensions.
     * 
     * @param width  Viewport width in pixels
     * @param height Viewport height in pixels
     */
    public LibGDXCamera(int width, int height) {
        orthoCamera = new OrthographicCamera();
        // Set viewport to match screen dimensions
        orthoCamera.viewportWidth = width;
        orthoCamera.viewportHeight = height;
        orthoCamera.setToOrtho(false, width, height);
        orthoCamera.update();
    }

    /**
     * Updates the camera's projection matrix.
     * Must be called after changing position or zoom.
     */
    @Override
    public void update() { orthoCamera.update(); }

    /**
     * Sets the camera's center position.
     * 
     * @param x X coordinate in world units
     * @param y Y coordinate in world units
     */
    @Override
    public void setPosition(float x, float y) {
        orthoCamera.position.set(x, y, 0);
    }

    /**
     * Sets the camera's center position from a vector.
     * 
     * @param position Position vector
     */
    @Override
    public void setPosition(Vector2 position) {
        orthoCamera.position.set(position.x, position.y, 0);
    }

    /**
     * Sets the camera's zoom level.
     * 
     * @param zoom Zoom multiplier (1.0 = normal, 2.0 = 2x zoomed out)
     */
    @Override
    public void setZoom(float zoom) { orthoCamera.zoom = zoom; }

    /**
     * Returns the camera's X position.
     * 
     * @return X coordinate in world units
     */
    @Override
    public float getX() { return orthoCamera.position.x; }

    /**
     * Returns the camera's Y position.
     * 
     * @return Y coordinate in world units
     */
    @Override
    public float getY() { return orthoCamera.position.y; }

    /**
     * Returns the camera's zoom level.
     * 
     * @return Zoom multiplier
     */
    @Override
    public float getZoom() { return orthoCamera.zoom; }

    /**
     * Returns the camera's position as a vector.
     * 
     * @return New Vector2 with camera position
     */
    @Override
    public Vector2 getPosition(Vector2 position) {
        return position.set(orthoCamera.position.x, orthoCamera.position.y);
    }

    /**
     * Returns the combined projection-view matrix for shader use.
     * 
     * @return A wrapped {@link engine.math.Matrix4} containing the native LibGDX matrix
     */
    @Override
    public engine.math.Matrix4 getCombinedMatrix() {
        return new engine.math.Matrix4(orthoCamera.combined);
    }

    /**
     * Returns the underlying LibGDX camera for direct manipulation.
     * 
     * @return The native OrthographicCamera instance
     */
    public OrthographicCamera getOrthographicCamera() {
        return orthoCamera;
    }
}