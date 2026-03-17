package engine.physics;

import engine.ecs.GameObject;
import engine.world.Board;

public abstract class Collision {

    public abstract void solveCollision(GameObject a, GameObject b, Board board);

    public abstract boolean isColliding(GameObject a, GameObject b, Board board);
}
