package engine.graphics.interfaces;

import engine.math.Matrix4;
import engine.math.Vector2;

/**
 * Interface for camera abstraction in 2D rendering.
 * 
 * <p>Provides position, zoom, and projection matrix for view transformation.
 * Implementations wrap backend-specific camera objects (e.g., LibGDX OrthographicCamera).</p>
 */
public interface ICamera {
    /** Updates the camera's projection matrix after position/zoom changes */
    void update();
    
    /** Sets camera position from coordinates */
    void setPosition(float x, float y);
    
    /** Sets camera position from vector */
    void setPosition(Vector2 position);
    
    /** Sets zoom level (1.0 = normal, >1 = zoomed out) */
    void setZoom(float zoom);
    
    /** @return Camera X position */
    float getX();
    
    /** @return Camera Y position */
    float getY();
    
    /** @return Current zoom level */
    float getZoom();
    
    /** @return Camera position as vector
     * @param position Output vector to store position
     * @return Camera position vector
     */
    Vector2 getPosition(Vector2 position);
    
    /** @return Combined projection-view matrix for shaders */
    Matrix4 getCombinedMatrix();
}