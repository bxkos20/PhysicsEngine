package World.Board;

import GameObject.Elements.*;
import com.badlogic.gdx.math.Vector2;

public abstract class Board {
    float width;
    float height;

    public Board(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public abstract float getDistance(Vector2 a, Vector2 b);

    public abstract Vector2 getDirectionVector(Vector2 origin, Vector2 target);

    public abstract Vector2 getMidPoint(Vector2 a, Vector2 b);

    public abstract void enforceBounds (Transform t);
}