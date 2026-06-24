package engine.ecs.systems.implementations;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.RendererComponent;
import engine.ecs.components.TransformComponent;
import engine.ecs.systems.System;
import engine.graphics.interfaces.IRenderer;

import java.util.List;

public class RendererSystem extends System {

    private final IRenderer renderer;
    final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    final int RENDERER_ID = ComponentRegistry.getId(RendererComponent.class);

    /**
     * Creates a system with the specified component signature.
     */
    public RendererSystem(IRenderer renderer) {
        super(false,
                TransformComponent.class,
                RendererComponent.class);
        this.renderer = renderer;
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
        RendererComponent renderComponent = gameObject.getComponent(RENDERER_ID);
        TransformComponent transformComponent = gameObject.getComponent(TRANSFORM_ID);
        renderer.draw(renderComponent.shape, transformComponent.getPosition().x, transformComponent.getPosition().y, renderComponent.color);
    }

    @Override
    protected void processUpdate(float dt, List<GameObject> gameObjects) {
        renderer.begin();
        processSequential(dt, gameObjects);
        renderer.end();
    }
}
