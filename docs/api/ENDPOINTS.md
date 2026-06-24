# Documentación de APIs - Physics Engine

## ⚠️ Nota Importante

Este proyecto es un **motor de física 2D** (Physics Engine), no una API REST. No expone endpoints HTTP ni servicios web. En su lugar, proporciona una **API programática en Java** que se utiliza directamente en el código.

## 📚 API Programática

El motor expone sus funcionalidades a través de clases e interfaces Java. A continuación se documentan los puntos de entrada principales.

## 🎯 Puntos de Entrada Principales

### 1. EngineSimulation

**Paquete:** `engine.app.EngineSimulation`

**Propósito:** Controlador principal de la simulación de física.

**Métodos Públicos:**

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `EngineSimulation()` | `IRenderer renderer`, `ISimulationLogic simulationLogic`, `IKeyInput keyInput` | - | Constructor. Inicializa el mundo con dimensiones configuradas. |
| `update()` | `float deltaTime` | `void` | Actualiza la simulación por un timestep. |
| `render()` | - | `void` | Renderiza todas las entidades del mundo. |
| `addEntity()` | `GameObject entity` | `void` | Añade una entidad al mundo. |
| `getWorld()` | - | `World` | Retorna la instancia del mundo. |
| `getProfilingInfo()` | - | `String` | Retorna información de profiling en formato string. |

**Excepciones:**
- `IllegalArgumentException`: Si el renderer es null en el constructor.

---

### 2. World

**Paquete:** `engine.world.World`

**Propósito:** Contenedor de todas las entidades y sistemas del mundo.

**Métodos Públicos:**

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `World()` | `Board board`, `Collision collision`, `GridPartition gridPartition` | - | Constructor con inyección de dependencias. |
| `addEntity()` | `GameObject gameObject` | `void` | Encola una entidad para añadir (se añade al inicio del siguiente update). |
| `getGameObjects()` | - | `List<GameObject>` | Retorna la lista principal de entidades. |
| `update()` | `float dt` | `void` | Actualiza todos los sistemas por un frame. |
| `getProfilingInfo()` | - | `String` | Retorna información de profiling de sistemas. |
| `forEachObject()` | `Consumer<GameObject> action` | `void` | Ejecuta una acción sobre cada entidad. |

---

### 3. GameObject (Entity)

**Paquete:** `engine.ecs.GameObject`

**Propósito:** Contenedor de entidad en arquitectura ECS.

**Métodos Públicos:**

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `GameObject()` | - | - | Constructor. Crea entidad con ID único. |
| `addComponent()` | `T component` | `void` | Añade un componente a la entidad. |
| `getComponent()` | `int componentId` | `T` | Retorna un componente por su ID registrado. |
| `hasComponent()` | `int componentId` | `boolean` | Verifica si la entidad tiene un componente. |
| `getSignature()` | - | `long` | Retorna el bitmask de componentes de la entidad. |
| `checkSignature()` | `long otherSignature` | `boolean` | Verifica si la entidad tiene todos los componentes del mask. |
| `getId()` | - | `int` | Retorna el ID único de la entidad. |

---

### 4. ComponentRegistry

**Paquete:** `engine.ecs.ComponentRegistry`

**Propósito:** Registro de IDs de tipos de componentes.

**Métodos Estáticos Públicos:**

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `getId()` | `Class<?> componentClass` | `int` | Obtiene o asigna ID único para una clase de componente. Thread-safe. |
| `getBit()` | `Class<?> componentClass` | `long` | Retorna el bitmask para una clase de componente. |
| `idToBit()` | `int id` | `long` | Convierte un ID de componente a su bitmask. |
| `getAllComponents()` | - | `Map<Class<?>, Integer>` | Retorna copia de todos los mapeos de componentes. |

---

### 5. ISimulationLogic

**Paquete:** `engine.app.implementation.ISimulationLogic`

**Propósito:** Interfaz para lógica de simulación personalizada.

**Métodos:**

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `start()` | `World world`, `IKeyInput keyInput` | `void` | Llamado una vez durante inicialización. Debe crear entidades y sistemas. |
| `update()` | `float deltaTime`, `List<GameObject> gameObjects` | `void` | Llamado cada ciclo de actualización. Maneja lógica específica de simulación. |
| `getProfilingInfo()` | - | `String` | Retorna información de profiling del último ciclo. |

---

### 6. Componentes Principales

#### TransformComponent
**Paquete:** `engine.ecs.components.TransformComponent`

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `TransformComponent()` | `float x`, `float y` | - | Constructor con posición inicial. |
| `getPosition()` | - | `Vector2` | Retorna el vector de posición (mutable). |
| `setPosition()` | `Vector2 position` | `void` | Establece posición desde vector. |
| `setPosition()` | `float x`, `float y` | `void` | Establece posición desde coordenadas. |

#### PhysicsComponent
**Paquete:** `engine.ecs.components.PhysicsComponent`

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `PhysicsComponent()` | `float mass`, `float restitution`, `float friction` | - | Constructor con propiedades físicas. |
| `addForce()` | `Vector2 force` | `void` | Añade fuerza al acumulador. |
| `addForce()` | `float x`, `float y` | `void` | Añade fuerza desde componentes. |
| `getVelocity()` | - | `Vector2` | Retorna velocidad actual. |
| `setVelocity()` | `Vector2 velocity` | `void` | Establece velocidad desde vector. |
| `setVelocity()` | `float x`, `float y` | `void` | Establece velocidad desde componentes. |
| `getSumForces()` | - | `Vector2` | Retorna fuerzas acumuladas. |
| `setSumForces()` | `Vector2 sumForces` | `void` | Establece fuerzas acumuladas. |
| `getMass()` | - | `float` | Retorna masa. |
| `setMass()` | `float mass` | `void` | Establece masa. |
| `getRestitution()` | - | `float` | Retorna coeficiente de restitución. |
| `setRestitution()` | `float restitution` | `void` | Establece coeficiente de restitución. |
| `getFriction()` | - | `float` | Retorna coeficiente de fricción. |
| `setFriction()` | `float friction` | `void` | Establece coeficiente de fricción. |

#### ColliderComponent
**Paquete:** `engine.ecs.components.ColliderComponent`

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `ColliderComponent()` | `float radius` | - | Constructor con radio de colisión. |
| `getRadius()` | - | `float` | Retorna el radio de colisión. |
| `setRadius()` | `float radius` | `void` | Establece el radio de colisión. |

#### RenderComponent
**Paquete:** `engine.ecs.components.RendererComponent`

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `RenderComponent()` | `Color color`, `Shape shape` | - | Constructor con color y forma. |

---

### 7. Systems

#### System (Base Abstract)
**Paquete:** `engine.ecs.systems.System`

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `System()` | `boolean threading`, `Class<?>... components` | - | Constructor con configuración de threading y componentes requeridos. |
| `update()` | `float dt`, `List<GameObject> gameObjects` | `void` | Actualiza todas las entidades que coinciden con la firma. |
| `shutdown()` | - | `void` (static) | Cierra el thread pool compartido. |
| `getCoreCount()` | - | `int` (static) | Retorna número de núcleos CPU disponibles. |

#### MovementSystem
**Paquete:** `engine.ecs.systems.implementations.MovementSystem`

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `MovementSystem()` | `boolean threading`, `Board board` | - | Constructor. |
| `processGameObject()` | `float dt`, `GameObject gameObject` | `void` (protected) | Procesa física de una entidad. |

#### CollisionSystem
**Paquete:** `engine.ecs.systems.implementations.CollisionSystem`

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `CollisionSystem()` | `boolean threading`, `GridPartition gridPartition`, `Board board`, `Collision collision` | - | Constructor. |
| `processUpdate()` | `float dt`, `List<GameObject> gameObjects` | `void` (protected) | Sobrescribe update para lógica personalizada de colisiones. |

---

### 8. Matemáticas

#### Vector2
**Paquete:** `engine.math.Vector2`

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `Vector2()` | `float x`, `float y` | - | Constructor con componentes. |
| `Vector2()` | - | - | Constructor cero. |
| `set()` | `float x`, `float y` | `Vector2` | Establece componentes. Retorna this para chaining. |
| `add()` | `Vector2 other` | `Vector2` | Suma vector. Retorna this. |
| `add()` | `float x`, `float y` | `Vector2` | Suma escalares. Retorna this. |
| `mulAdd()` | `Vector2 vec`, `float scalar` | `Vector2` | Añade vector escalado. Retorna this. |
| `sub()` | `Vector2 other` | `Vector2` | Resta vector. Retorna this. |
| `len()` | - | `float` | Retorna longitud (magnitud). |
| `len2()` | - | `float` | Retorna longitud al cuadrado (evita sqrt). |
| `nor()` | - | `Vector2` | Normaliza a longitud unitaria. Retorna this. |
| `dot()` | `Vector2 other` | `float` | Producto punto. |
| `scl()` | `float f` | `Vector2` | Escala vector. Retorna this. |
| `set()` | `Vector2 vector` | `Vector2` | Copia valores de otro vector. Retorna this. |

---

## 🔌 Integración con Backend

### IRenderer
**Paquete:** `engine.graphics.interfaces.IRenderer`

Interfaz para implementaciones de renderizado (LibGDX, custom, etc.).

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `begin()` | - | `void` | Inicia batch de renderizado. |
| `draw()` | `Shape shape`, `float x`, `float y`, `Color color` | `void` | Dibuja una forma en posición específica. |
| `end()` | - | `void` | Finaliza batch de renderizado. |
| `getProfilingInfo()` | - | `String` | Retorna información de profiling. |

### IKeyInput
**Paquete:** `engine.inputs.IKeyInput`

Interfaz para input de teclado.

| Método | Parámetros | Retorno | Descripción |
|--------|------------|---------|-------------|
| `isPress()` | `Key key` | `boolean` | Verifica si una tecla está presionada. |

---

## 📝 Ejemplo de Uso

```java
// 1. Crear simulación
EngineSimulation simulation = new EngineSimulation(
    renderer, 
    new MySimulationLogic(), 
    keyInput
);

// 2. Crear entidad
GameObject entity = new GameObject();
entity.addComponent(new TransformComponent(x, y));
entity.addComponent(new PhysicsComponent(mass, restitution, friction));
entity.addComponent(new ColliderComponent(radius));
entity.addComponent(new RenderComponent(color, shape));

// 3. Añadir al mundo
simulation.addEntity(entity);

// 4. Game loop
while (running) {
    float deltaTime = getDeltaTime();
    simulation.update(deltaTime);
    simulation.render();
}
```

---

## 🚫 No Aplicable

Este proyecto **NO** utiliza:
- Spring Boot
- REST Controllers
- OpenAPI/Swagger
- Endpoints HTTP
- APIs web

Es una **librería/engine de física** que se integra directamente en aplicaciones Java.
