package World.Board;

import GameObject.Components.Core.TransformComponent;
import com.badlogic.gdx.math.Vector2;

public class ToroidalBoard extends Board {
    public ToroidalBoard(float width, float height) {
        super(width, height);
    } //TODO: Try dont use unecesary if-else

    /**
     * Teletransporta el objeto si se sale de los bordes.
     * Se debe llamar en cada frame después de mover el objeto.
     */
    public void enforceBounds(TransformComponent transform) {
        // Eje X
        if (transform.getPosition().x < 0) transform.getPosition().x += width;
        else if (transform.getPosition().x > width) transform.getPosition().x -= width;

        // Eje Y
        if (transform.getPosition().y < 0) transform.getPosition().y += height;
        else if (transform.getPosition().y > height) transform.getPosition().y -= height;
    }

    /**
     * Calcula la distancia MÁS CORTA considerando el mundo toroidal.
     */
    public float getDistance(Vector2 origin, Vector2 target) {
        float dx = Math.abs(origin.x - target.x);
        float dy = Math.abs(origin.y - target.y);

        // Si la distancia directa es mayor que la mitad del mundo,
        // el camino corto es dando la vuelta por el otro lado.
        if (dx > width / 2) dx = width - dx;
        if (dy > height / 2) dy = height - dy;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Devuelve el vector que apunta desde 'origin' hacia 'target'
     * tomando el camino más corto (atravesando paredes si es necesario).
     * Útil para que los agentes sepan hacia dónde ir.
     */
    public Vector2 getDirectionVector(Vector2 origin, Vector2 target, Vector2 out) {
        float dx = target.x - origin.x;
        float dy = target.y - origin.y;

        if (dx > width / 2) {
            dx -= width; // Es más corto ir hacia la izquierda cruzando el borde
        } else if (dx < -width / 2) {
            dx += width; // Es más corto ir hacia la derecha cruzando el borde
        }

        // Ajuste para el eje Y (Alto)
        if (dy > height / 2) {
            dy -= height; // Es más corto ir hacia arriba cruzando el borde
        } else if (dy < -height / 2) {
            dy += height; // Es más corto ir hacia abajo cruzando el borde
        }

        return out.set(dx, dy);
    }

    /**
     * Calcula el punto medio real considerando la toroidicidad.
     */
    public Vector2 getMidPoint(Vector2 origin, Vector2 target, Vector2 out) {
        // Obtenemos el VectorAB
        getDirectionVector(origin, target, out);

        // El punto medio es: (VectorAB / 2) + A
        out.scl(0.5f).add(origin);

        // Aseguramos que el resultado final esté dentro de los límites
        // (Por si el punto medio cae justo en el borde negativo o excesivo)
        if (out.x < 0) out.x += width;
        else if (out.x > width) out.x -= width;

        if (out.y < 0) out.y += height;
        else if (out.y > height) out.y -= height;

        return out;
    }
}