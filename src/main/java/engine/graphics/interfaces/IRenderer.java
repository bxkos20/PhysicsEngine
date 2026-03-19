package engine.graphics.interfaces;

import engine.graphics.Color;
import engine.world.World;

/**
 * Interface for rendering abstraction.
 * 
 * <p>Provides batched rendering of shapes with position and color.
 * Implementations handle GPU batching, shader management, and draw calls.</p>
 * 
 * <h3>Usage Pattern:</h3>
 * <pre>
 * renderer.begin();
 * renderer.draw(shape, x, y, color);
 * renderer.end();
 * </pre>
 */
public interface IRenderer {
    /** Begins a rendering batch. Must be called before draw(). */
    void begin();
    
    /** Ends the batch and submits draw calls to GPU. */
    void end();
    
    /**
     * Draws a shape at the specified position with color.
     * 
     * @param shape Shape to draw
     * @param x     World X position
     * @param y     World Y position
     * @param color RGBA color
     */
    void draw(IShape shape, float x, float y, Color color);
    
    /**
     * Sets the camera for view transformation.
     * 
     * @param camera Camera to use for projection
     */
    void setCamera(ICamera camera);

    /**
     * Returns profiling information for the last render call.
     *
     * @return Formatted string with render time in milliseconds
     */
    String getProfilingInfo();
}