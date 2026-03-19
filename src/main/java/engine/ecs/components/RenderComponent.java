package engine.ecs.components;

import engine.graphics.Color;
import engine.graphics.interfaces.IShape;

/**
 * Component for storing renderable shape data using the OOP shape system.
 */
public class RenderComponent {

    /**
     * RGBA color used for rendering.
     */
    public final Color color;

    /**
     * Shape type identifier.
     */
    public final IShape shape;

    /**
     * Creates render data with the specified properties.
     *
     * @param color       RGBA color
     * @param shape       Shape instance
     */
    public RenderComponent(Color color, IShape shape) {
        this.color = color;
        this.shape = shape;
    }

}