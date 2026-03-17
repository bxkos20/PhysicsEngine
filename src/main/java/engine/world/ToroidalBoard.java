package engine.world;

import engine.ecs.components.TransformComponent;
import engine.math.Vector2;

public class ToroidalBoard extends Board {
    public ToroidalBoard(float width, float height) {
        super(width, height);
    }

    /**
     * Teletransporta el objeto si se sale de los bordes.
     * Se debe llamar en cada frame después de mover el objeto.
     */
    public void enforceBounds(TransformComponent transform) {
        transform.getPosition().x = (transform.getPosition().x % width + width) % width;
        transform.getPosition().y = (transform.getPosition().y % height + height) % height;
    }

    /**
     * Calcula la distancia MÁS CORTA considerando el mundo toroidal.
     */
    public float getDistance(Vector2 origin, Vector2 target) {
        return (float) Math.sqrt(getDistance2(origin, target));
    }

    /**
     * Calcula la distancia al cuadrado MÁS CORTA considerando el mundo toroidal.
     */
    public float getDistance2(Vector2 origin, Vector2 target) {
        float dx = origin.x - target.x;
        float dy = origin.y - target.y;

        dx -= width * Math.round(dx / width);
        dy -= height * Math.round(dy / height);

        return dx * dx + dy * dy;
    }

    /**
     * Devuelve el vector que apunta desde 'origin' hacia 'target'
     * tomando el camino más corto (atravesando paredes si es necesario).
     * Útil para que los agentes sepan hacia dónde ir.
     */
    public void getDirectionVector(Vector2 origin, Vector2 target, Vector2 out) {
        float dx = target.x - origin.x;
        float dy = target.y - origin.y;

        dx -= width * Math.round(dx / width);
        dy -= height * Math.round(dy / height);

        out.set(dx, dy);
    }

    /**
     * Calcula el punto medio real considerando la toroidicidad.
     */
    public void getMidPoint(Vector2 origin, Vector2 target, Vector2 out) {
        // Obtenemos el VectorAB
        getDirectionVector(origin, target, out);

        // El punto medio es: (VectorAB / 2) + A
        out.scl(0.5f).add(origin);

        // Aseguramos que el resultado final esté dentro de los límites
        out.x = (out.x % width + width) % width;
        out.y = (out.y % height + height) % height;
    }
}