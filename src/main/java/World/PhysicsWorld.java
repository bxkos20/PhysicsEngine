package World;

import GameObject.GameObject;
import World.Board.Board;
import World.Board.Grid.GridPartition;
import World.Collision.Collision;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {
    public final float G = 25.0f;

    // Lista de entidades
    private final List<GameObject> objects;
    private final List<GameObject> objectsToAdd; // Buffer para añadir con seguridad

    // Geometría del mundo
    public final Board board;
    public final Collision collision;
    public final GridPartition gridPartition;

    // Inyección de Dependencia: Recibimos el Board, no lo creamos
    public PhysicsWorld(Board board, Collision collision, GridPartition gridPartition) {
        this.board = board;
        this.collision = collision;
        this.gridPartition = gridPartition;
        this.objects = new ArrayList<>();
        this.objectsToAdd = new ArrayList<>();
    }

    public void addObject(GameObject obj) {
        objectsToAdd.add(obj);
    }

    public List<GameObject> getObjects() {
        return objects;
    }

    public void update(float dt) {
        // 0. Gestión de listas (Añadir nuevos nacimientos)
        if (!objectsToAdd.isEmpty()) {
            objects.addAll(objectsToAdd);
            objectsToAdd.clear();
        }

        // --- FASE 1: PREPARACIÓN DE IA ---
        // Llenamos la Grid con las posiciones actuales para que la IA sepa quién está cerca
        gridPartition.clear();
        for (GameObject obj : objects) {
            gridPartition.add(obj);
        }

        // --- FASE 2: LÓGICA / IA ---
        // Los objetos consultan la Grid y aplican fuerzas (pero NO se mueven aún)
        for (GameObject obj : objects) {
            obj.update(dt, this);
        }

        // --- FASE 3: INTEGRACIÓN FÍSICA ---
        // Aplicamos velocidades y movemos los objetos
        for (GameObject obj : objects) {
            if (obj.physics != null) {
                obj.physics.update(obj.transform, dt);
                board.enforceBounds(obj.transform);
            }
        }

        // --- FASE 4: ACTUALIZACIÓN DE GRID ---
        // Como los objetos se han movido, la Grid anterior ya no es válida.
        // La reconstruimos para detectar colisiones en la nueva posición.
        gridPartition.clear();
        for (GameObject obj : objects) {
            gridPartition.add(obj);
        }

        // --- FASE 5: RESOLUCIÓN DE COLISIONES ---
        // Usamos la Grid actualizada para encontrar solapamientos reales
        for (GameObject obj : objects){
            if (obj.collider == null) continue;

            // Buscamos vecinos en la nueva posición
            for (GameObject other : gridPartition.getNearby(obj, 1)){
                if (obj == other) continue;
                
                // Optimización: Solo resolver par A-B una vez (evitar B-A)
                if (obj.hashCode() < other.hashCode()) {
                    collision.solveCollision(obj, other, board);
                }
            }
        }
    }
}
