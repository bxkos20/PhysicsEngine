package engine.physics;

import engine.ecs.GameObject;
import engine.world.Board;

/**
 * Abstract base class for collision detection and resolution.
 * 
 * <p>Defines the interface for collision handling between game objects.
 * Implementations provide specific collision response strategies
 * (e.g., elastic collisions, inelastic collisions).</p>
 * 
 * @see ElasticCollision
 * @see engine.ecs.systems.CollisionSystem
 */
public abstract class Collision {

    /**
     * Resolves a collision between two objects.
     * Modifies velocities/positions to separate the objects.
     * 
     * @param a     First game object
     * @param b     Second game object
     * @param board Board for world coordinate handling
     */
    public abstract void solveCollision(GameObject a, GameObject b, Board board);

    /**
     * Checks if two objects are currently colliding.
     * 
     * @param a     First game object
     * @param b     Second game object
     * @param board Board for world coordinate handling
     * @return true if objects are colliding
     */
    public abstract boolean isColliding(GameObject a, GameObject b, Board board);
}
