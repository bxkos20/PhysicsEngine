package World.Board;

import GameObject.Components.Core.TransformComponent;
import com.badlogic.gdx.math.Vector2;

public abstract class Board {
    float width;
    float height;

    public Board(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public abstract float getDistance(Vector2 origin, Vector2 target);

    public abstract float getDistance2(Vector2 origin, Vector2 target);

    public abstract void getDirectionVector(Vector2 origin, Vector2 target, Vector2 out);

    public abstract void getMidPoint(Vector2 origin, Vector2 target, Vector2 out);

    public abstract void enforceBounds (TransformComponent transform);
}