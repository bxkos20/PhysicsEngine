package world.collision;

import gameObject.components.ComponentRegistry;
import gameObject.components.core.ColliderComponent;
import gameObject.components.core.PhysicsComponent;
import gameObject.components.core.TransformComponent;
import gameObject.GameObject;
import world.board.Board;
import com.badlogic.gdx.math.Vector2;

public class ElasticCollision extends Collision {
    private static final int TRANSFORM_ID = ComponentRegistry.getId(TransformComponent.class);
    private static final int PHYSICS_ID = ComponentRegistry.getId(PhysicsComponent.class);
    private static final int COLLIDER_ID = ComponentRegistry.getId(ColliderComponent.class);

    // Optimización: Vector temporal para no crear basura (new Vector2) en cada frame
    private final Vector2 tmpRelVel = new Vector2();
    private final Vector2 tmpDir = new Vector2();


    /**
     * Separa dos objetos respetando sus masas.
     * Si uno es estático (masa 0), no se mueve.
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

    private void elasticCollision(GameObject a, GameObject b, Vector2 directionNormal) {
        if (!a.checkSignature(PHYSICS_ID) || !b.checkSignature(PHYSICS_ID)) return;
        //TODO: If one have an the other no make the bouncing too

        PhysicsComponent aPhysics = a.getComponent(PHYSICS_ID);
        PhysicsComponent bPhysics = b.getComponent(PHYSICS_ID);

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