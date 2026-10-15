# Programmatic API Reference

This project exposes Java classes and interfaces. It does not provide HTTP endpoints, a REST service, Spring controllers, or an OpenAPI specification. The filename is retained for existing links; this document describes the engine's Java API.

## Simulation Entry Points

### `EngineLauncher`

`engine.app.EngineLauncher.launch(LauncherType, ISimulationLogic, Settings)` selects a backend and starts the supplied simulation. The current launcher type used by the demos is `LauncherType.LIBGDX`.

### `EngineSimulation`

`engine.app.EngineSimulation` is the engine-side simulation controller. Its constructor is:

```java
EngineSimulation(IRenderer renderer, ISimulationLogic simulationLogic,
                 IKeyInput keyInput, Settings settings)
```

Public operations:

| Method | Behavior |
|---|---|
| `update(float dt)` | Updates the world, movement and collision systems, then custom simulation logic. `dt` is in seconds. |
| `render(float dt)` | Renders entities through `RendererSystem`. |
| `getWorld()` | Returns the simulation's `World`. |
| `getProfilingInfo()` | Returns timing information from engine systems and custom simulation logic. |

The controller does not expose `addEntity`; add entities through `simulation.start(...)` using the supplied `World`.

### `ISimulationLogic`

Implement `engine.app.implementation.ISimulationLogic` to make a simulation reusable with the engine:

```java
void start(World world, IKeyInput keyInput, Settings settings);
void update(float deltaTime, List<GameObject> gameObjects);
String getProfilingInfo();
```

`start` is called during engine initialization. It is typically used to create and enqueue entities and initialize demo-specific systems. `update` is called after the core movement and collision systems on each simulation step.

## World and Entity API

### `World`

The constructor receives its collaborators:

```java
World(Board board, Collision collision, GridPartition gridPartition)
```

`addEntity(GameObject)` queues an entity for insertion at the next `World.update(float dt)`. That update also clears and rebuilds the spatial grid. `getGameObjects()` returns the active internal list, not a defensive copy; direct modifications can bypass the intended queue and spatial-index lifecycle. There is currently no entity-removal API.

### `GameObject` and `ComponentRegistry`

`GameObject` provides a unique integer ID and stores components by registered type ID. Its primary operations are `addComponent(T)`, `getComponent(int)` or `getComponent(Class<?>)`, `hasComponent(int)`, `getSignature()`, `checkSignature(long)`, and `getId()`.

`ComponentRegistry.getId(Class<?>)` assigns an ID to a component class. `getBit(Class<?>)` and `idToBit(int)` provide signature masks. A `GameObject` has a fixed component array of 32 entries; component IDs must remain within that storage capacity.

## Components and Systems

| Type | Purpose and current behavior |
|---|---|
| `TransformComponent` | Mutable 2D position used by movement, collision, and rendering. |
| `PhysicsComponent` | Velocity, accumulated force, mass, restitution, and friction. `MovementSystem` integrates forces and clears them after each update. |
| `ColliderComponent` | Provides circle construction (`ColliderComponent(float radius)`) and AABB construction (`ColliderComponent(float width, float height)`). See the collision limitation below. |
| `RendererComponent` | Holds a `Color` and `Shape` for drawing. |
| `MovementSystem` | Processes entities with transform and physics components and applies the configured board bounds. |
| `CollisionSystem` | Uses the spatial partition for broad-phase candidates and a `Collision` strategy for collision checks and response. |
| `RendererSystem` | Draws entities with transform and renderer components through `IRenderer`. |

The base `engine.ecs.systems.SystemECS` filters entities by required component signature. It can process eligible work sequentially or use a shared fixed thread pool when threading is enabled. `CollisionSystem` gathers candidate pairs in parallel when configured to do so and resolves them sequentially.

### Collision limitation

Although `ColliderComponent` can represent AABBs, the only current strategy, `ElasticCollision`, uses circle radii for narrow-phase detection and resolution. Rectangular collision behavior is not implemented end to end. Use circle colliders with the current strategy.

## Backend Interfaces

- `IRenderer` defines `begin()`, `draw(Shape, float x, float y, Color)`, `end()`, `setCamera(ICamera)`, and `getProfilingInfo()`.
- `IKeyInput.isPress(Key)` abstracts keyboard state from the engine.
- `Board` abstracts world-boundary handling and distance/direction calculations. The factory can create `EdgedBoard` or `ToroidalBoard`.
- `Collision` abstracts collision tests and response. Only `ElasticCollision` is currently provided.
- `GridPartition` abstracts spatial insertion and nearby-entity queries. The factory currently constructs `ToroidalGridPartition`.

## Configuration API

`Settings` maps each concrete `ISettings` class to one instance. `Settings.createDefault()` creates `PerformanceSettings`, `RenderingSettings`, `ScreenSettings`, `SimulationSettings`, and `WorldSettings`. Calling `add` with the same concrete class replaces the previous value; `get` returns `null` if that class was not registered.

Simulations may add their own settings types, as the demos do with `DotSettings` and `PongSettings`. The API and defaults are described in [Configuration and Operations](../operaciones/CONFIGURACION.md).

## Minimal Entity Example

```java
GameObject particle = new GameObject();
particle.addComponent(new TransformComponent(100f, 100f));
particle.addComponent(new PhysicsComponent(1f, 1f, 0.5f));
particle.addComponent(new ColliderComponent(5f));
particle.addComponent(new RendererComponent(color, shape));
world.addEntity(particle);
```

The entity becomes active at the next world update. For a complete runnable setup, start from `demos.pong.Launcher`, `demos.dots.Launcher`, or the `demos.yourproject` starter scaffold; these assemble settings and invoke `EngineLauncher`.
