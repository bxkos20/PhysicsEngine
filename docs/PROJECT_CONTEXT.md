# Project Context for AI Assistants

This document is a portable briefing for an AI assistant working on this repository. It summarizes the implemented architecture and current constraints; verify details against source before making behavior-changing claims.

## Project at a Glance

PhysicsEngine is a Java 21, 2D physics and simulation project. It contains a small ECS engine, a desktop LibGDX/LWJGL3 backend, and two runnable simulations: an interacting-particle demo and a basic Pong simulation. It is a local graphical application, not a server or REST API.

The project aims to make simulation behavior configurable and separable from the rendering backend. It is an educational and evolving engine, not a production-grade general-purpose physics library.

## Repository Map

```text
src/main/java/
  engine/app/             EngineSimulation, EngineLauncher, backend abstractions
  engine/config/          Settings, ISettings, typed setting implementations
  engine/ecs/             GameObject, ComponentRegistry, components, systems
  engine/graphics/        Colors, shapes, renderer and camera interfaces
  engine/inputs/          Backend-independent key input and key identifiers
  engine/math/            Vector2, Vector3, Matrix4
  engine/physics/         Collision abstraction and ElasticCollision
  engine/util/            Profiler and thread-local random utility
  engine/world/           World, WorldFactory, boards, spatial partitions
  backend/libgdx/         Desktop backend, LibGDX renderer, camera, keyboard input
  demos/dots/             Particle demo and DotSystem
  pong/                   Pong simulation, settings, and player system
src/test/java/engine/     ECS, components, graphics, math, physics, world tests
docs/                     Architecture decision, API, operations, and project context
```

The Gradle build is a single Java project. There is no `CONTRIBUTING.md`, although older documentation previously linked to one.

## Build and Run

Prerequisite: a Java 21 JDK. Use the checked-in Gradle Wrapper.

```bash
./gradlew build
./gradlew test
./gradlew run                                  # default: pong.Launcher
./gradlew run -PmainClass=demos.dots.Launcher
./gradlew runLauncher -PmainClass=pong.Launcher
```

The graphical launchers require a desktop session with graphics support. `run` and the custom `runLauncher` task both read the `mainClass` Gradle project property. JUnit Jupiter 5 tests run on the JUnit Platform. No JaCoCo task is configured.

## Runtime Architecture

### Composition and initialization

1. A demo launcher creates `Settings` and adds generic plus demo-specific settings.
2. `EngineLauncher.launch(...)` selects `LauncherType.LIBGDX` and delegates to the backend.
3. `BackendSimulation` reads screen, world, simulation, and rendering settings; it creates the LibGDX camera, renderer, input adapter, and `EngineSimulation`.
4. `EngineSimulation` creates a `World` through `WorldFactory`, creates movement and collision systems, then calls `ISimulationLogic.start(world, keyInput, settings)`.
5. The demo logic queues its entities with `World.addEntity(...)` and initializes demo-specific systems.

### Simulation update

Each `EngineSimulation.update(dt)` runs these steps in order:

1. `World.update(dt)` transfers queued entities into the active list, clears the spatial partition, and inserts the active entities again.
2. `MovementSystem` processes entities with `TransformComponent` and `PhysicsComponent`.
3. `CollisionSystem` processes entities with `TransformComponent` and `ColliderComponent`, queries nearby grid cells, and checks candidate pairs.
4. Custom `ISimulationLogic.update(dt, gameObjects)` runs. For dots and Pong, this delegates to the simulation-specific system.

Rendering is a separate `EngineSimulation.render(dt)` call. `RendererSystem` batches drawing for entities with both `TransformComponent` and `RendererComponent`.

`BackendSimulation` uses a fixed-step accumulator for simulation and rendering. It clamps frame time, supports a time scale and pause state, and passes settings for the simulation timestep, render timestep, maximum frame time, and initial time scale. The engine controller itself accepts the `dt` it is given; the fixed-step behavior belongs to the LibGDX backend.

### ECS model

- `GameObject` is an entity with a unique integer ID, a `long` signature, and a fixed-size array of 32 component slots.
- `ComponentRegistry` assigns process-wide IDs to component classes and maps them to signature bits.
- Components are ordinary objects holding state. Common types are `TransformComponent`, `PhysicsComponent`, `ColliderComponent`, and `RendererComponent`.
- `engine.ecs.systems.System` filters the active entity list using required component signatures.
- `World.addEntity` queues insertion until the next update. `World.getGameObjects()` returns the mutable active list itself. There is no entity-removal API yet.

### Physics, boundaries, and collision

- `MovementSystem` applies accumulated force to velocity, applies friction damping, advances position, enforces board boundaries, and clears accumulated force. This is semi-implicit Euler integration.
- `EdgedBoard` clamps positions to the world dimensions and uses ordinary Euclidean distance.
- `ToroidalBoard` wraps positions and computes shortest wrapped distance and direction.
- `ElasticCollision` detects circular overlap, separates bodies by inverse mass, and applies a restitution-based impulse. A body with mass zero is treated as static.
- `ColliderComponent` provides both circle and AABB constructors, but the current `ElasticCollision` implementation reads collider radii and only implements circle collision checks and response. Do not describe rectangular or mixed collision support as complete.
- `WorldFactory` currently chooses `ToroidalGridPartition` regardless of board type. The selected `Board` still controls final distance and boundary behavior.
- Dense, compressed contacts can jitter or retain overlap. This is a known issue, not a solved case.

### Spatial partition and threading

`World.update` clears and rebuilds the grid on every simulation update. `ToroidalGridPartition` wraps grid cell indexes and provides nearby-entity iteration. The `CollisionSystem` derives a search distance from collider broad-phase reach and grid cell size. A grid can reduce collision candidates in typical distributions, but performance is workload-dependent and not guaranteed to be O(n) in the worst case.

`PerformanceSettings.enableMultithreading` controls threaded processing in the engine systems. The base `System` uses a shared fixed thread pool sized to available processors and runs small workloads sequentially. Collision detection may collect candidate pairs in parallel; collision resolution is sequential to avoid concurrent mutation of bodies.

## Configuration

`Settings` is a map from each concrete `ISettings` class to one setting object. `Settings.createDefault()` registers:

| Type | Default |
|---|---|
| `ScreenSettings` | 1280 x 720 pixels |
| `WorldSettings` | 1000 x 1000, `EDGED`, `ELASTIC`, grid size 25 |
| `SimulationSettings` | Simulation step 1/60 s, render step 1/30 s, max frame 0.25 s, time scale 1.0, physics substeps 2 |
| `RenderingSettings` | 50000 batch vertices, 150000 batch indices |
| `PerformanceSettings` | Multithreading enabled |

`Settings.add` replaces an existing value of the same concrete class. `Settings.get` returns `null` for a missing class. Settings are constructed in Java; there is no file loader, environment-variable binding, or runtime reload.

Demo launchers override some defaults:

- `pong.Launcher`: 1000 x 500 edged world, elastic collision, grid size 0 (factory derives a size), default Pong settings, multithreading enabled.
- `demos.dots.Launcher`: 2500 x 2500 toroidal world, grid size 5, 5000 dots, 10 types, radius 2.5, interaction strength 1, distance bounds 10 and 50, 1920 x 1080 screen.

When `gridPartitionSize <= 0`, `WorldFactory` derives the grid cell size as `max(10, max(width, height) / 25)`. `DotSettings` and `PongSettings` are demo-specific settings and are not installed by `Settings.createDefault()`.

## Tests and Verification

The test tree covers component registration and entities, physics components, vector math, elastic collision, world construction, board geometry, and the toroidal grid. After Java changes, run the narrowest relevant tests, then `./gradlew test` or `./gradlew build` as appropriate.

Useful focused test command:

```bash
./gradlew test --tests 'engine.physics.ElasticCollisionTest'
```

Documentation links and behavior claims should be checked against implementation. In particular, do not assume all settings fields are consumed by a runtime system merely because they exist; check the relevant call sites.

## Current Work and Constraints

- Keep the engine independent of LibGDX at its public simulation boundaries; backend-specific behavior belongs under `backend/libgdx`.
- Follow the existing ECS, settings, and Gradle conventions. Prefer a focused change and focused tests over broad refactoring.
- Avoid claiming strict linear collision complexity, complete AABB behavior, entity removal, environment configuration, HTTP endpoints, Docker deployment, or non-desktop runtime support.
- Main unfinished areas are end-to-end AABB collision support, incremental grid updates, collision jitter under compression, settings validation, lifecycle APIs for adding/removing entities, and optional UI/features.
- See [TODO.md](../TODO.md) for the tracked roadmap, [README.md](../README.md) for the project overview, [API reference](api/ENDPOINTS.md) for Java contracts, and [configuration guide](operaciones/CONFIGURACION.md) for defaults and launch commands.