# Projects and Reference Demos

## Project Goal

The main product in this repository is the reusable physics engine, with simulation, ECS, rendering, input, and world capabilities. It is intended to provide a foundation that can host different kinds of projects. Projects define their own goals and behavior while depending on the engine's public APIs.

The directory `src/main/java/yourproject/` is reserved for those actual projects. It is currently empty by design. Do not treat it as an engine demo or assume it contains a runnable starter implementation.

## Projects Versus Demos

- `src/main/java/engine/` contains reusable engine code. It should not depend on project or demo packages.
- `src/main/java/backend/libgdx/` adapts LibGDX to engine interfaces such as `IRenderer` and `IKeyInput`.
- `src/main/java/yourproject/` is the reserved home for actual applications built on the engine.
- `src/main/java/demos/` contains examples that demonstrate engine usage. The particle simulation and Pong are demos, not the engine's primary product.

Projects and demos can each have a launcher, settings, an `ISimulationLogic` implementation, components, and systems. They should depend on engine abstractions; the engine should not depend on any individual project or demo.

## Adding a Project

The repository is currently one Gradle project; `yourproject` is a Java package area, not a separate Gradle module. To host a project in this source tree:

1. Create a package beneath `src/main/java/yourproject/`, for example `yourproject/orbit/` with Java package `yourproject.orbit`.
2. Add a project launcher that creates `Settings`, adds engine and project-specific settings, and calls `EngineLauncher.launch(...)` with a project-owned `ISimulationLogic` implementation.
3. In `ISimulationLogic.start`, read settings, create `GameObject` instances and their components, and queue them through `world.addEntity(...)`.
4. Create project-specific behavior in systems that extend `SystemECS`. Declare required component types in `super(...)` and implement `processGameObject` or `processUpdate`.
5. Call project systems from `ISimulationLogic.update`. This hook runs after the engine's movement and collision systems.
6. Depend on engine interfaces and domain types. Keep LibGDX imports and platform-specific details out of the project layer.
7. Add tests under `src/test/java` and run `./gradlew test`.

When a project launcher exists, it can be selected without changing the default entry point:

```bash
./gradlew run -PmainClass=yourproject.orbit.Launcher
```

## Using Demos as Examples

The existing demos provide working patterns to inspect:

- `demos.dots.Launcher` and `DotSimulationLogic` show a particle simulation with custom settings, components, types, and a custom system.
- `demos.pong.Launcher` and `Simulation` show a compact interactive application with project-specific settings, player components, and a `PlayerSystem`.

Use these as examples of engine integration, not as definitions of what all future projects must be. Pong is a demo, just like dots.

## Separate Gradle Projects

If an application needs its own lifecycle, dependency graph, release process, or artifact, it may be better as a separate Gradle project. This repository does not currently publish the engine as a dependency or define a multi-project Gradle build, so that packaging work would need to be designed separately.