package world.collision;

import gameObject.GameObject;
import world.board.Board;

public abstract class Collision {

    public abstract void solveCollision(GameObject a, GameObject b, Board board);

    public abstract boolean isColliding(GameObject a, GameObject b, Board board);
}
