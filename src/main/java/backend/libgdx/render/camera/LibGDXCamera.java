package backend.libgdx.render.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import engine.graphics.interfaces.ICamera;
import engine.math.Vector2;

// backend/libgdx/render/camera/LibGDXCamera.java
public class LibGDXCamera implements ICamera {
    private final OrthographicCamera orthoCamera;

    public LibGDXCamera(int width, int height) {
        orthoCamera = new OrthographicCamera();
        // Set viewport to match screen dimensions
        orthoCamera.viewportWidth = width;
        orthoCamera.viewportHeight = height;
        orthoCamera.setToOrtho(false, width, height);
        orthoCamera.update();
    }

    @Override
    public void update() { orthoCamera.update(); }

    @Override
    public void setPosition(float x, float y) {
        orthoCamera.position.set(x, y, 0);
    }

    @Override
    public void setPosition(Vector2 position) {
        orthoCamera.position.set(position.x, position.y, 0);
    }

    @Override
    public void setZoom(float zoom) { orthoCamera.zoom = zoom; }

    @Override
    public float getX() { return orthoCamera.position.x; }

    @Override
    public float getY() { return orthoCamera.position.y; }

    @Override
    public float getZoom() { return orthoCamera.zoom; }

    @Override
    public Vector2 getPosition() {
        return new Vector2(orthoCamera.position.x, orthoCamera.position.y);
    }

    @Override
    public engine.math.Matrix4 getCombinedMatrix() {
        return new engine.math.Matrix4(orthoCamera.combined);
    }

    public OrthographicCamera getOrthographicCamera() {
        return orthoCamera;
    }
}