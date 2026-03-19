package engine.ecs.systems;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.TransformComponent;
import engine.physics.Collision;
import engine.spatial.GridPartition;
import engine.world.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollisionSystem extends System {
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    private static final int COLLIDER_ID = ComponentRegistry.getId(ColliderComponent.class);
    private ThreadLocal<List<GameObject>> pairsThread = ThreadLocal.withInitial(ArrayList::new);

    private final GridPartition gridPartition;
    private final Board board;
    private final Collision collision;

    public CollisionSystem(boolean threading, GridPartition gridPartition, Board board, Collision collision) {
        super(ComponentRegistry.idToBit(TRANSFORM_ID) |
                        ComponentRegistry.idToBit(COLLIDER_ID),
                threading
        );
        this.gridPartition = gridPartition;
        this.board = board;
        this.collision = collision;
    }

    @Override
    public void update(float dt, List<GameObject> gameObjects) {
        long start = java.lang.System.nanoTime();
        if (THREADING) {
            // FASE 1: Parallel detection using proper collector
            List<GameObject> pendingCollisions = gameObjects.parallelStream()
                    .filter(go -> go.checkSignature(REQUIRED_SIGNATURE))
                    .flatMap(gameObject -> {
                        TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);
                        List<GameObject> collisions = new ArrayList<>();
                        
                        gridPartition.processNearby(transform, 1, other -> {
                            if (gameObject.getId() >= other.getId() || !other.checkSignature(REQUIRED_SIGNATURE)) return;
                            
                            if (collision.isColliding(gameObject, other, board)) {
                                collisions.add(gameObject);
                                collisions.add(other);
                            }
                        });
                        
                        return collisions.stream();
                    })
                    .collect(Collectors.toList());

            // FASE 2: Resolution
            for (int i = 0; i < pendingCollisions.size(); i += 2) {
                GameObject a = pendingCollisions.get(i);
                GameObject b = pendingCollisions.get(i + 1);
                collision.solveCollision(a, b, board);
            }

        } else {
            for (int i = 0; i < gameObjects.size(); i++) {
                GameObject gameObject = gameObjects.get(i);

                if (gameObject.checkSignature(REQUIRED_SIGNATURE)) {
                    TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);

                    gridPartition.processNearby(transform, 1, other -> {
                        // Ignoramos duplicados y comprobamos firmas
                        if (gameObject.getId() >= other.getId() || !other.checkSignature(REQUIRED_SIGNATURE)) return;

                        if (collision.isColliding(gameObject, other, board))
                            collision.solveCollision(gameObject, other, board);
                    });
                }
            }
        }
        this.lastExecutionTimeMs = (java.lang.System.nanoTime() - start) / 1_000_000f;
    }

    @Override
    protected void processGameObject(float dt, GameObject gameObject) {
    }
}
