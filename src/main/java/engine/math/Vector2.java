package engine.math;

public class Vector2 {
    public float x, y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2() {
        this(0, 0);
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 add(Vector2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 mulAdd(Vector2 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        return this;
    }

    public Vector2 sub(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float len2() {
        return x * x + y * y;
    }

    public Vector2 nor() {
        float len = len();
        if (len != 0) {
            this.x /= len;
            this.y /= len;
        }
        return this;
    }

    public float dot(Vector2 other) {
        return 0;
    }

    public Vector2 scl(float f) {
        this.x *= f;
        this.y *= f;
        return this;
    }

    public Vector2 set(Vector2 vector) {
        this.x = vector.x;
        this.y = vector.y;
        return this;
    }
}
