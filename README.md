# Physics Engine

A 2D physics and simulation engine written in Java 21. The project combines a small Entity-Component-System (ECS) core, configurable world geometry, collision processing, and a LibGDX desktop renderer. It is both an engine-learning project and a runnable sandbox with particle and Pong demos.

## Current Capabilities

- ECS entities use registered component IDs and bitmask signatures for system filtering.
- Physics movement uses semi-implicit Euler integration, accumulated forces, friction damping, and board boundary handling.
- Elastic circle collisions use overlap separation and impulse-based velocity response, including zero-mass static objects.
- A grid partition narrows collision candidates. Its benefit depends on entity distribution and grid sizing; it is not a strict O(n) worst-case guarantee.
- Worlds support edged boundaries and toroidal wrap-around distance and movement behavior.
- Systems can process work in parallel using a shared fixed thread pool. Collision detection can run in parallel; collision resolution is sequential to avoid concurrent mutation of the same bodies.
- The renderer is abstracted behind `IRenderer`; the integrated backend uses LibGDX and batched drawing.
- Settings are passed as typed objects rather than read from a global static configuration.

## Technology

| Technology | Version | Role |
|---|---:|---|
| Java | 21 | Engine and simulations |
| Gradle Wrapper | Repository-pinned | Build and dependency management |
| LibGDX | 1.12.1 | Desktop window, input, camera, and rendering backend |
| JUnit Jupiter | 5.10.2 | Unit and black-box tests |

## Source Layout

```text
src/main/java/
    engine/
        app/                 Simulation controller and backend abstractions
        config/              Typed settings and setting implementations
        ecs/                 GameObject, component registry, components, systems
        graphics/            Colors, shapes, renderer and camera interfaces
        inputs/              Backend-independent keyboard input
        math/                Vector and matrix primitives
        physics/             Collision strategies
        util/                Profiling and random utilities
        world/               World, factory, boards, and spatial partition
    backend/libgdx/         LibGDX launcher, input, camera, and renderer
    demos/dots/             Interacting-particle demo
    demos/pong/             Pong demo
    demos/yourproject/      Runnable starter demo scaffold
src/test/java/engine/     ECS, math, physics, board, and world tests
docs/                     Architecture, API, operations, and project context
```

## Requirements and Build

Install a Java 21 JDK. Gradle is provided through the wrapper, so a separate Gradle installation is not required.

```bash
./gradlew build
./gradlew test
```

The graphical applications require a desktop environment that can create a LibGDX/LWJGL3 window.

## Run a Simulation

Pong is a demo and is the default application entry point:

```bash
./gradlew run
```

Select the particle demo explicitly:

```bash
./gradlew run -PmainClass=demos.dots.Launcher
```

The repository also defines a dedicated launcher task:

```bash
./gradlew runLauncher -PmainClass=demos.pong.Launcher
./gradlew runLauncher -PmainClass=demos.dots.Launcher
```

## Architecture and Update Flow

`EngineLauncher` selects a backend. The LibGDX backend creates the renderer, input adapter, and `EngineSimulation`; each simulation implements `ISimulationLogic` so demo-specific entity setup and behavior remain outside the core engine.

An `EngineSimulation.update(dt)` performs the following work:

1. `World.update` moves queued entities into the active list, clears the spatial grid, and rebuilds it from the current entity positions.
2. `MovementSystem` processes entities with transform and physics components.
3. `CollisionSystem` finds candidates through the grid and checks/resolves collisions.
4. The custom simulation logic updates its own behavior and systems.

Rendering is a separate call. `RendererSystem` draws entities that have both transform and renderer components. The LibGDX backend drives update and render using fixed-step accumulators and the values in `SimulationSettings`.

An entity is a `GameObject` with components such as `TransformComponent`, `PhysicsComponent`, `ColliderComponent`, and `RendererComponent`. Components hold state; systems own processing. `World.addEntity` queues additions until the next world update. The current world API does not provide entity removal.

The code is organized in layers: demo/application code depends on engine contracts; the engine core owns ECS, world, math, and physics rules; backend adapters implement rendering and input interfaces. Higher-level simulation code should depend on abstractions rather than LibGDX implementations. This boundary is in place for renderer and input, while `WorldFactory` still chooses concrete board, collision, and partition implementations. See [Layered Architecture and Execution Flow](docs/arquitecture/ARCHITECTURE.md).

## Add a Demo

In this repository, a new project means a new runnable simulation under `src/main/java/demos/<lowercase-name>/`, not a separate Gradle build. The starter implementation lives in `demos/yourproject`; setup steps and package responsibilities are in [Creating a New Demo](docs/NEW_DEMO.md).

## Configuration

`Settings.createDefault()` creates `PerformanceSettings`, `RenderingSettings`, `ScreenSettings`, `SimulationSettings`, and `WorldSettings`. A launcher can replace any setting by adding another instance of the same class, and can add simulation-specific settings such as `DotSettings` or `PongSettings`.

The dots launcher currently configures a 2500 x 2500 toroidal world, elastic collisions, a 5000-dot simulation with 10 types, and a 1920 x 1080 window. Pong configures a 1000 x 500 edged world. See [Configuration and Operations](docs/operaciones/CONFIGURACION.md) for default values and details.

## Known Limitations

- `ColliderComponent` has a rectangular AABB constructor, but the only collision strategy, `ElasticCollision`, currently performs circle-based calculations using collider radii. Rectangular collider response is therefore not implemented end to end.
- Dense, highly compressed contact scenarios can produce jitter or residual overlap.
- The spatial grid is cleared and rebuilt on every world update; incremental cell updates are not implemented.
- Settings are currently supplied in Java code. Environment-variable and file-based configuration are not implemented.
- The integrated runtime backend is desktop LibGDX/LWJGL3; other LibGDX target backends are not configured here.
- No HTTP service, Docker setup, or declared project license is present.

## Documentation

- [Project context for AI assistants](docs/PROJECT_CONTEXT.md)
- [Layered architecture and execution flow](docs/arquitecture/ARCHITECTURE.md)
- [Creating a new demo](docs/NEW_DEMO.md)
- [Architecture decision record: technology stack](docs/arquitecture/ADR-001-stack-tecnologico.md)
- [Programmatic API reference](docs/api/ENDPOINTS.md)
- [Configuration and operations](docs/operaciones/CONFIGURACION.md)
- [Project TODO and roadmap](TODO.md)
