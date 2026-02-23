package World.Collision;

import GameObject.GameObject;
import World.Board.Board;

public abstract class Collision {

    public abstract void solveCollision(GameObject a, GameObject b, Board board);
}
