package world;

import gameObject.GameObject;
import world.board.Board;
import world.board.grid.GridPartition;
import world.collision.Collision;
import world.systems.CollisionSystem;
import world.systems.DotSystem;
import world.systems.MovementSystem;

import java.util.ArrayList;
import java.util.List;

public class World {
    // Lista de entidades
    private final List<GameObject> gameObjects;
    private final List<GameObject> objectsToAdd; // Buffer para añadir con seguridad

    // Geometría del mundo
    public final Board board;
    public final Collision collision;
    public final GridPartition gridPartition;

    //Systems
    MovementSystem movementSystem;
    CollisionSystem collisionSystem;
    DotSystem dotSystem;

    // Inyección de Dependencia: Recibimos el Board, no lo creamos
    public World(Board board, Collision collision, GridPartition gridPartition) {
        this.board = board;
        this.collision = collision;
        this.gridPartition = gridPartition;
        this.gameObjects = new ArrayList<>();
        this.objectsToAdd = new ArrayList<>();
        this.movementSystem = new MovementSystem(true, board);
        this.collisionSystem = new CollisionSystem(true, gridPartition, board, collision);
        this.dotSystem = new DotSystem(true, gridPartition, board);
    }

    public void addObject(GameObject obj) {
        objectsToAdd.add(obj);
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public void update(float dt) {
        // 0. Gestión de listas (Añadir nuevos nacimientos)
        if (!objectsToAdd.isEmpty()) {
            gameObjects.addAll(objectsToAdd);
            objectsToAdd.clear();
        }

        // --- FASE 1: PREPARACIÓN DE IA ---
        // Llenamos la Grid con las posiciones actuales para que la IA sepa quién está cerca
        gridPartition.clear();
        gridPartition.add(gameObjects);

        // --- FASE 2: LÓGICA / IA ---
        // Los objetos consultan la Grid y aplican fuerzas (pero NO se mueven aún)
        dotSystem.update(dt, gameObjects);

        // --- FASE 3: INTEGRACIÓN FÍSICA ---
        movementSystem.update(dt, gameObjects);

        // --- FASE 5: RESOLUCIÓN DE COLISIONES ---
        // Usamos la Grid actualizada para encontrar solapamientos reales
        collisionSystem.update(dt, gameObjects);
    }

    // En World.java
    public String getProfilingInfo() {
        return String.format("Dots: %.2fms | Physics: %.2fms | Collisions: %.2fms",
                dotSystem.getLastExecutionTimeMs(),
                movementSystem.getLastExecutionTimeMs(),
                collisionSystem.getLastExecutionTimeMs());
    }
}
