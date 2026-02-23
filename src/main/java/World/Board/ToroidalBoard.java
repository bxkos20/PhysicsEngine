package World.Board;

import GameObject.Elements.*;
import com.badlogic.gdx.math.Vector2;

public class ToroidalBoard extends Board {
    public ToroidalBoard(float width, float height) {
        super(width, height);
    }

    /**
     * Teletransporta el objeto si se sale de los bordes.
     * Se debe llamar en cada frame después de mover el objeto.
     */
    public void enforceBounds(Transform t) {
        // Eje X
        if (t.position.x < 0) t.position.x += width;
        else if (t.position.x > width) t.position.x -= width;

        // Eje Y
        if (t.position.y < 0) t.position.y += height;
        else if (t.position.y > height) t.position.y -= height;
    }

    /**
     * Calcula la distancia MÁS CORTA considerando el mundo toroidal.
     */
    public float getDistance(Vector2 a, Vector2 b) {
        float dx = Math.abs(a.x - b.x);
        float dy = Math.abs(a.y - b.y);

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
    public Vector2 getDirectionVector(Vector2 origin, Vector2 target) {
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

        return new Vector2(dx, dy);
    }

    /**
     * Calcula el punto medio real considerando la toroidicidad.
     */
    public Vector2 getMidPoint(Vector2 a, Vector2 b) {
        // Usamos el vector dirección corregido para encontrar el medio
        Vector2 dir = getDirectionVector(a, b);

        // El punto medio es: A + (VectorAB / 2)
        Vector2 mid = new Vector2(a).mulAdd(dir, 0.5f);

        // Aseguramos que el resultado final esté dentro de los límites
        // (Por si el punto medio cae justo en el borde negativo o excesivo)
        if (mid.x < 0) mid.x += width;
        else if (mid.x > width) mid.x -= width;

        if (mid.y < 0) mid.y += height;
        else if (mid.y > height) mid.y -= height;

        return mid;
    }
}