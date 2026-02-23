package GameObject.Elements;

import com.badlogic.gdx.math.Vector2;

// 1. La posición (Compartida)
public class Transform {
    public Vector2 position;

    public Transform(float x, float y) {
        this.position = new Vector2(x, y);
    }
}