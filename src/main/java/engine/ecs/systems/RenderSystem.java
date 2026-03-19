package engine.ecs.systems;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.RenderData;
import engine.ecs.components.TransformComponent;
import engine.factories.ShapeFactory;
import engine.graphics.interfaces.IRenderer;
import engine.graphics.interfaces.IShape;

public class RenderSystem {
    private final IRenderer renderer;
    private final ShapeFactory shapeFactory;
    private final long REQUIRED_SIGNATURE;
    
    public RenderSystem(IRenderer renderer, ShapeFactory shapeFactory) {
        this.renderer = renderer;
        this.shapeFactory = shapeFactory;
        this.REQUIRED_SIGNATURE = ComponentRegistry.idToBit(
            ComponentRegistry.getId(RenderData.class)
        ) | ComponentRegistry.idToBit(
            ComponentRegistry.getId(TransformComponent.class)
        );
    }
    
    public void render(Iterable<GameObject> gameObjects) {
        renderer.begin();
        
        for (GameObject gameObject : gameObjects) {
            if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                RenderData renderData = gameObject.getComponent(
                    ComponentRegistry.getId(RenderData.class)
                );
                TransformComponent transform = gameObject.getComponent(
                    ComponentRegistry.getId(TransformComponent.class)
                );
                
                if (renderData != null && transform != null) {
                    // Recreate shape from data
                    IShape shape = recreateShape(renderData);
                    renderer.draw(shape, transform.getPosition().x, transform.getPosition().y, renderData.color);
                }
            }
        }
        
        renderer.end();
    }
    
    private IShape recreateShape(RenderData renderData) {
        switch (renderData.shapeType) {
            case "circle":
                return shapeFactory.createCircle(
                    renderData.shapeParams[0], // radius
                    (int)renderData.shapeParams[1], // quality  
                    renderData.shapeParams[2]  // lineWidth
                );
            case "rect":
                return shapeFactory.createRect(
                    renderData.shapeParams[0], // width
                    renderData.shapeParams[1], // height
                    renderData.shapeParams[2]  // lineWidth
                );
            case "filled_circle":
                return shapeFactory.createFilledCircle(
                    renderData.shapeParams[0], // radius
                    (int)renderData.shapeParams[1]  // quality
                );
            case "filled_rect":
                return shapeFactory.createFilledRect(
                    renderData.shapeParams[0], // width
                    renderData.shapeParams[1]  // height
                );
            default:
                throw new IllegalArgumentException("Unknown shape type: " + renderData.shapeType);
        }
    }
}
