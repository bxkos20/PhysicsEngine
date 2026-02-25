package World.Collision;

import GameObject.Components.ComponentRegistry;
import GameObject.Components.Core.ColliderComponent;
import GameObject.Components.Core.PhysicsComponent;
import GameObject.Components.Core.TransformComponent;
import GameObject.GameObject;
import World.Board.Board;
import com.badlogic.gdx.math.Vector2;

public class ElasticCollision extends Collision {

    // Optimización: Vector temporal para no crear basura (new Vector2) en cada frame
    private final Vector2 tmpRelVel = new Vector2();

    /**
     * Separa dos objetos respetando sus masas.
     * Si uno es estático (masa 0), no se mueve.
     */
    private void disconnection(GameObject a, GameObject b, Vector2 directionNormal, float overlap) {
        PhysicsComponent aPhysics = a.getComponent(PhysicsComponent.class);
        TransformComponent aTransform = a.getComponent(TransformComponent.class);

        PhysicsComponent bPhysics = b.getComponent(PhysicsComponent.class);
        TransformComponent bTransform = b.getComponent(TransformComponent.class);

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
        if (!a.checkSignature(ComponentRegistry.getBit(PhysicsComponent.class)) ||
                        !b.checkSignature(ComponentRegistry.getBit(PhysicsComponent.class))) return;

        PhysicsComponent aPhysics = a.getComponent(PhysicsComponent.class);
        PhysicsComponent bPhysics = b.getComponent(PhysicsComponent.class);

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
        TransformComponent aTransform = a.getComponent(TransformComponent.class);
        ColliderComponent aCollider = a.getComponent(ColliderComponent.class);

        TransformComponent bTransform = b.getComponent(TransformComponent.class);
        ColliderComponent bCollider = b.getComponent(ColliderComponent.class);

        Vector2 direction = board.getDirectionVector(aTransform.getPosition(), bTransform.getPosition());

        float distSq = direction.len2();
        float radiiSum = aCollider.getRadius() + bCollider.getRadius();

        if (distSq < radiiSum * radiiSum) { // Is colliding
            float dist = (float) Math.sqrt(distSq);

            // Evita división por cero al normalizar si están en el mismo pixel exacto
            dist = Math.max(dist , 0.1f);

            direction.nor(); // Normalizamos

            // 1. Separar (Ahora respeta paredes estáticas)
            disconnection(a, b, direction, radiiSum - dist);

            // 2. Rebotar
            elasticCollision(a, b, direction);
        }
    }
}