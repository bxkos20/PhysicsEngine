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
    private static final ThreadLocal<Vector2> DELTA_THREAD = ThreadLocal.withInitial(Vector2::new);


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
        CollisionData contact = getCollisionData(a, b, board);
        if (contact == null) return;

        Vector2 direction = new Vector2(contact.normalX, contact.normalY);

        // 1. Separar (Ahora respeta paredes estáticas)
        disconnection(a, b, direction, contact.penetration);

        // 2. Rebotar
        elasticCollision(a, b, direction);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Checks if distance between objects is less than sum of radii.</p>
     */
    @Override
    public boolean isColliding(GameObject a, GameObject b, Board board) {
        return getCollisionData(a, b, board) != null;
    }

    private static CollisionData getCollisionData(GameObject a, GameObject b, Board board) {
        TransformComponent aTransform = a.getComponent(ComponentRegistry.getId(TransformComponent.class));
        ColliderComponent aCollider = a.getComponent(ComponentRegistry.getId(ColliderComponent.class));
        TransformComponent bTransform = b.getComponent(ComponentRegistry.getId(TransformComponent.class));
        ColliderComponent bCollider = b.getComponent(ComponentRegistry.getId(ColliderComponent.class));

        Vector2 delta = DELTA_THREAD.get();
        board.getDirectionVector(aTransform.getPosition(), bTransform.getPosition(), delta);
        float deltaX = delta.x;
        float deltaY = delta.y;

        if (aCollider.isCircle() && bCollider.isCircle()) {
            float radiiSum = aCollider.getRadius() + bCollider.getRadius();
            float distance2 = deltaX * deltaX + deltaY * deltaY;
            if (distance2 >= radiiSum * radiiSum) return null;

            float distance = (float) Math.sqrt(distance2);
            if (distance == 0) return new CollisionData(1, 0, radiiSum);
            return new CollisionData(deltaX / distance, deltaY / distance, radiiSum - distance);
        }

        if (!aCollider.isCircle() && !bCollider.isCircle()) {
            float overlapX = (aCollider.getWidth() + bCollider.getWidth()) / 2f - Math.abs(deltaX);
            float overlapY = (aCollider.getHeight() + bCollider.getHeight()) / 2f - Math.abs(deltaY);
            if (overlapX <= 0 || overlapY <= 0) return null;

            if (overlapX < overlapY) {
                return new CollisionData(deltaX < 0 ? -1 : 1, 0, overlapX);
            }
            return new CollisionData(0, deltaY < 0 ? -1 : 1, overlapY);
        }

        if (aCollider.isCircle()) {
            return circleAabbContact(deltaX, deltaY, aCollider, bCollider);
        }

        CollisionData contact = circleAabbContact(-deltaX, -deltaY, bCollider, aCollider);
        if (contact == null) return null;
        return new CollisionData(-contact.normalX, -contact.normalY, contact.penetration);
    }

    /** delta points from the circle center to the AABB center. */
    private static CollisionData circleAabbContact(float deltaX, float deltaY, ColliderComponent circle, ColliderComponent box) {
        float halfWidth = box.getWidth() / 2f;
        float halfHeight = box.getHeight() / 2f;
        float localCircleX = -deltaX;
        float localCircleY = -deltaY;
        float closestX = Math.max(-halfWidth, Math.min(halfWidth, localCircleX));
        float closestY = Math.max(-halfHeight, Math.min(halfHeight, localCircleY));
        float contactX = deltaX + closestX;
        float contactY = deltaY + closestY;
        float distance2 = contactX * contactX + contactY * contactY;
        float radius = circle.getRadius();

        if (distance2 >= radius * radius) return null;

        if (distance2 > 0) {
            float distance = (float) Math.sqrt(distance2);
            return new CollisionData(contactX / distance, contactY / distance, radius - distance);
        }

        float distanceToLeft = localCircleX + halfWidth;
        float distanceToRight = halfWidth - localCircleX;
        float distanceToBottom = localCircleY + halfHeight;
        float distanceToTop = halfHeight - localCircleY;
        float nearestFace = Math.min(Math.min(distanceToLeft, distanceToRight),
                Math.min(distanceToBottom, distanceToTop));

        if (nearestFace == distanceToLeft) return new CollisionData(1, 0, radius + nearestFace);
        if (nearestFace == distanceToRight) return new CollisionData(-1, 0, radius + nearestFace);
        if (nearestFace == distanceToBottom) return new CollisionData(0, 1, radius + nearestFace);
        return new CollisionData(0, -1, radius + nearestFace);
    }

    private static final class CollisionData {
        private final float normalX;
        private final float normalY;
        private final float penetration;

        private CollisionData(float normalX, float normalY, float penetration) {
            this.normalX = normalX;
            this.normalY = normalY;
            this.penetration = penetration;
        }
    }
}