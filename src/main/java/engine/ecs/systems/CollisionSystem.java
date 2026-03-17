package engine.ecs.systems;

import engine.ecs.components.ColliderComponent;
import engine.ecs.components.TransformComponent;
import engine.ecs.GameObject;
import engine.ecs.ComponentRegistry;
import engine.world.Board;
import engine.spatial.GridPartition;
import engine.physics.Collision;

import java.util.ArrayList;
import java.util.List;

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
            // FASE 1: Parallel detection
            List<GameObject> pendingCollisions = gameObjects.parallelStream()
                    .filter(go -> go.checkSignature(REQUIRED_SIGNATURE))
                    .collect(
                            ArrayList::new,

                            // 2. Lo que hace el hilo con su objeto
                            (localList, gameObject) -> {
                                TransformComponent transform = gameObject.getComponent(TRANSFORM_ID);

                                gridPartition.processNearby(transform, 1, other -> {
                                    if (gameObject.getId() >= other.getId() || !other.checkSignature(REQUIRED_SIGNATURE)) return;

                                    if (collision.isColliding(gameObject, other, board)) {
                                        localList.add(gameObject);
                                        localList.add(other);
                                    }
                                });
                            },

                            // 3. Al terminar, Java junta las listas de todos los hilos en pendingCollisions
                            ArrayList::addAll
                    );

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
