package engine.physics;

import engine.ecs.ComponentRegistry;
import engine.ecs.GameObject;
import engine.ecs.components.ColliderComponent;
import engine.ecs.components.PhysicsComponent;
import engine.ecs.components.TransformComponent;
import engine.math.Vector2;
import engine.world.board.Board;

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

    private static final PhysicsComponent DEFAULT_PHYSICS = new PhysicsComponent(0, 1, 0);


    /**
     * Separates two colliding objects based on their mass ratio.
     * Static objects (mass=0) do not move.
     *
     * @param a               First game object
     * @param b               Second game object
     * @param directionNormal Normal direction from A to B
     * @param overlap         Penetration depth
     */
    private static void disconnection(GameObject a, GameObject b, Vector2 directionNormal, float overlap) {
        PhysicsComponent aPhysics = a.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        aPhysics = (aPhysics == null) ? DEFAULT_PHYSICS : aPhysics;
        TransformComponent aTransform = a.getComponent(ComponentRegistry.getId(TransformComponent.class));

        PhysicsComponent bPhysics = b.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        bPhysics = (bPhysics == null) ? DEFAULT_PHYSICS : bPhysics;
        TransformComponent bTransform = b.getComponent(ComponentRegistry.getId(TransformComponent.class));

        // Obtenemos la masa inversa (0 si es estático/pared)
        float imA = (aPhysics.getMass() == 0) ? 0 : 1 / aPhysics.getMass();
        float imB = (bPhysics.getMass() == 0) ? 0 : 1 / bPhysics.getMass();
        float totalInvMass = imA + imB;

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
    private static void elasticCollision(GameObject a, GameObject b, Vector2 directionNormal) {
        if (!a.checkSignature(ComponentRegistry.getBit(PhysicsComponent.class)) || !b.checkSignature(ComponentRegistry.getBit(PhysicsComponent.class))) return;

        // Handle static vs dynamic collisions
        PhysicsComponent aPhysics = a.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        aPhysics = (aPhysics == null) ? DEFAULT_PHYSICS : aPhysics;
        PhysicsComponent bPhysics = b.getComponent(ComponentRegistry.getId(PhysicsComponent.class));
        bPhysics = (bPhysics == null) ? DEFAULT_PHYSICS : bPhysics;

        if ((aPhysics.getMass() == 0) && (bPhysics.getMass() == 0)) {
            // Both static - no collision response needed
            return;
        }

        // Masa inversa (Manejo de objetos estáticos)
        float imA = (aPhysics.getMass() == 0) ? 0 : 1 / aPhysics.getMass();
        float imB = (bPhysics.getMass() == 0) ? 0 : 1 / bPhysics.getMass();


        // Usamos set y sub para reciclar el vector temporal y no generar Garbage Collection
        Vector2 tmpRelVel = new Vector2();
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
        //Obtain components
        TransformComponent aTransform = a.getComponent(ComponentRegistry.getId(TransformComponent.class));
        ColliderComponent aCollider = a.getComponent(ComponentRegistry.getId(ColliderComponent.class));

        TransformComponent bTransform = b.getComponent(ComponentRegistry.getId(TransformComponent.class));
        ColliderComponent bCollider = b.getComponent(ComponentRegistry.getId(ColliderComponent.class));

        Vector2 tmpDir = new Vector2();
        board.getDirectionVector(aTransform.getPosition(), bTransform.getPosition(), tmpDir);

        float radiiSum = aCollider.getRadius() + bCollider.getRadius();
        float dist = tmpDir.len();
        float overlap = radiiSum - dist;

        if (dist > 0) {
            tmpDir.nor();
        } else {
            // Overlapping at the same position, choose a default separation vector
            tmpDir.set(1, 0);
        }

        // 1. Separar (Ahora respeta paredes estáticas)
        disconnection(a, b, tmpDir, overlap);

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
        TransformComponent aTransform = a.getComponent(ComponentRegistry.getId(TransformComponent.class));
        ColliderComponent aCollider = a.getComponent(ComponentRegistry.getId(ColliderComponent.class));

        TransformComponent bTransform = b.getComponent(ComponentRegistry.getId(TransformComponent.class));
        ColliderComponent bCollider = b.getComponent(ComponentRegistry.getId(ColliderComponent.class));

        float dist2 = board.getDistance2(aTransform.getPosition(), bTransform.getPosition());
        float radiiSum = aCollider.getRadius() + bCollider.getRadius();

        return (dist2 < (radiiSum * radiiSum));
    }
}