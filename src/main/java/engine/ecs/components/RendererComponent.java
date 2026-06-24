package engine.ecs.components;

import engine.graphics.Color;
import engine.graphics.Shape;

/**
 * Component for storing renderable shape data using the OOP shape system.
 */
public class RendererComponent {

    /**
     * RGBA color used for rendering.
     */
    public final Color color;

    /**
     * Shape type identifier.
     */
    public final Shape shape;

    /**
     * Creates render data with the specified properties.
     *
     * @param color       RGBA color
     * @param shape       Shape instance
     */
    public RendererComponent(Color color, Shape shape) {
        this.color = color;
        this.shape = shape;
    }

}