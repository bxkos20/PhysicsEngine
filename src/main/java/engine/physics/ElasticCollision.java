package engine.physics;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import engine.math.Vector2;
import engine.world.Board;

/**
 * Implements elastic collision detection and response.
 * 
 * <p>Uses impulse-based collision resolution with mass-weighted separation.
 * Supports static objects (mass=0) which do not move during collision.</p>
 * 
 * <h3>Collision Response:</h3>
 * <ol>
 *   <li>Separate overlapping objects based on mass ratio</li>
 *   <li>Apply impulse to change velocities</li>
 *   <li>Respect restitution (bounciness) coefficient</li>
 * </ol>
 * 
 * @see Collision
 */
public class ElasticCollision extends Collision {
    /** Cached component ID for TransformComponent */
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    
    /** Cached component ID for PhysicsComponent */
    private static final int PHYSICS_ID = ComponentRegistry.getId(PhysicsComponent.class);
    
    /** Cached component ID for ColliderComponent */
    private static final int COLLIDER_ID = ComponentRegistry.getId(ColliderComponent.class);

    /** Reusable vector for relative velocity calculation (avoids GC) */
    private final Vector2 tmpRelVel = new Vector2();
    
    /** Reusable vector for direction/normal (avoids GC) */
    private final Vector2 tmpDir = new Vector2();


    /**
     * Separates two colliding objects based on their mass ratio.
     * Static objects (mass=0) do not move.
     * 
     * @param a                First game object
     * @param b                Second game object
     * @param directionNormal  Normal direction from A to B
     * @param overlap          Penetration depth
     */
    private void disconnection(GameObject a, GameObject b, Vector2 directionNormal, float overlap) {
        PhysicsComponent aPhysics = a.getComponent(PHYSICS_ID);
        TransformComponent aTransform = a.getComponent(TRANSFORM_ID);

        PhysicsComponent bPhysics = b.getComponent(PHYSICS_ID);
        TransformComponent bTransform = b.getComponent(TRANSFORM_ID);

        // Obtenemos la masa inversa (0 si es estático/pared)
        float imA = (aPhysics.getMass() == 0) ? 0 : 1 / aPhysics.getMass();
        float imB = (bPhysics.getMass() == 0) ? 0 : 1 / bPhysics.getMass();
        float totalInvMass = imA + imB;

        // Si ambos son estáticos (totalInvMass = 0), no hacer nada para evitar NaN
        if (totalInvMass <= 0) return;

        // Calculamos cuánto se mueve cada uno proporcionalmente a su "ligereza"
        // El objeto más ligero se mueve más. El estático (im=0) no se mueve nada.
        float separationA = overlap * (imA / totalInvMass);
        float separationB = overlap * (imB / totalInvMass);

        // A se mueve en contra de la normal
        aTransform.getPosition().mulAdd(directionNormal, -separationA);
        // B se mueve a favor de la normal
        bTransform.getPosition().mulAdd(directionNormal, separationB);
    }

    /**
     * Applies elastic collision impulse to two objects.
     * Uses coefficient of restitution for bounciness.
     * 
     * @param a               First game object
     * @param b               Second game object
     * @param directionNormal Normal direction from A to B
     */
    private void elasticCollision(GameObject a, GameObject b, Vector2 directionNormal) {
        if (!a.checkSignature(PHYSICS_ID) || !b.checkSignature(PHYSICS_ID)) return;
        
        // Handle static vs dynamic collisions
        PhysicsComponent aPhysics = a.getComponent(PHYSICS_ID);
        PhysicsComponent bPhysics = b.getComponent(PHYSICS_ID);
        
        if (aPhysics.getMass() == 0 && bPhysics.getMass() == 0) {
            // Both static - no collision response needed
            return;
        }

        // Masa inversa (Manejo de objetos estáticos)
        float imA = (aPhysics.getMass() == 0) ? 0 : 1 / aPhysics.getMass();
        float imB = (bPhysics.getMass() == 0) ? 0 : 1 / bPhysics.getMass();

        // Si ambos son masa infinita, no hay rebote
        if (imA + imB == 0) return;

        // Usamos set y sub para reciclar el vector temporal y no generar Garbage Collection
        tmpRelVel.set(bPhysics.getVelocity()).sub(aPhysics.getVelocity());

        float normalVelocity = tmpRelVel.dot(directionNormal);

        // Si se están separando, salir
        if (normalVelocity > 0) return;

        // Calcular restitución
        float e = Math.min(aPhysics.getRestitution(), bPhysics.getRestitution());

        // Fórmula del Impulso (j)
        float numerator = -(1 + e) * normalVelocity;
        float denominator = imA + imB;
        float j = numerator / denominator;

        aPhysics.getVelocity().mulAdd(directionNormal, -j * imA);
        bPhysics.getVelocity().mulAdd(directionNormal, j * imB);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Resolves collision by separating objects and applying impulse.</p>
     */
    @Override
    public void solveCollision(GameObject a, GameObject b, Board board) {
        TransformComponent aTransform = a.getComponent(TRANSFORM_ID);
        ColliderComponent aCollider = a.getComponent(COLLIDER_ID);

        TransformComponent bTransform = b.getComponent(TRANSFORM_ID);
        ColliderComponent bCollider = b.getComponent(COLLIDER_ID);

        board.getDirectionVector(aTransform.getPosition(), bTransform.getPosition(), tmpDir);

        float radiiSum = aCollider.getRadius() + bCollider.getRadius();

        float dist = tmpDir.len();

        // Evita división por cero al normalizar si están en el mismo pixel exacto
        dist = Math.max(dist, 0.1f);

        tmpDir.nor(); // Normalizamos

        // 1. Separar (Ahora respeta paredes estáticas)
        disconnection(a, b, tmpDir, radiiSum - dist);

        // 2. Rebotar
        elasticCollision(a, b, tmpDir);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Checks if distance between objects is less than sum of radii.</p>
     */
    @Override
    public boolean isColliding(GameObject a, GameObject b, Board board) {
        TransformComponent aTransform = a.getComponent(TRANSFORM_ID);
        ColliderComponent aCollider = a.getComponent(COLLIDER_ID);

        TransformComponent bTransform = b.getComponent(TRANSFORM_ID);
        ColliderComponent bCollider = b.getComponent(COLLIDER_ID);

        float dist2 = board.getDistance2(aTransform.getPosition(), bTransform.getPosition());
        float radiiSum = aCollider.getRadius() + bCollider.getRadius();

        return (dist2 < radiiSum * radiiSum);
    }
}