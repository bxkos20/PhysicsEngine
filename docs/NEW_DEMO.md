# Creating a New Demo

In this repository, a "new project" usually means adding another runnable simulation to the existing Gradle project. It is not a separate Gradle module. The starter package is `src/main/java/demos/yourproject`; copy it to a lowercase package name such as `demos/orbit` and rename `YourProject*` classes to match.

## Package Structure

```text
src/main/java/demos/yourproject/
  Launcher.java                 Settings and application entry point
  YourProjectSimulation.java    ISimulationLogic implementation
  components/                    Demo-specific component data
  settings/                      Demo-specific ISettings values
  systems/                       Demo-specific SystemECS behavior
  types/                         Demo-specific enums and value types
```

Keep the ownership boundaries clear:

- `Launcher` composes generic engine settings with demo settings and starts the selected backend.
- `YourProjectSimulation` creates initial entities in `start` and updates custom systems in `update`.
- Components contain demo-specific data; systems implement behavior over entities with required components.
- Demo code should depend on engine contracts, not on LibGDX. Put platform-specific code in `backend/libgdx` or behind an engine interface.

## Steps

1. Copy `src/main/java/demos/yourproject` to a lowercase package directory and update each Java `package` declaration and import.
2. Add the simulation's settings as a class implementing `ISettings`; register it in the launcher with `settings.add(...)`.
3. In `Launcher.main`, call `Settings.createDefault()`, override `WorldSettings` and other engine settings as needed, then call `EngineLauncher.launch(LauncherType.LIBGDX, simulation, settings)`.
4. In `ISimulationLogic.start`, read required settings, create entities with components, and enqueue them with `world.addEntity(...)`.
5. Create each custom system by extending `SystemECS`; declare required component classes in `super(...)`, then implement `processGameObject` or override `processUpdate` for a custom pass.
6. In `ISimulationLogic.update`, invoke custom systems with the supplied `deltaTime` and active `gameObjects` list. This logic runs after the engine movement and collision systems.
7. Run the demo with `./gradlew run -PmainClass=demos.<name>.Launcher` or `./gradlew runLauncher -PmainClass=demos.<name>.Launcher`.
8. Add or update focused tests under `src/test/java`, then run `./gradlew test`.

## Run the Starter Scaffold

```bash
./gradlew run -PmainClass=demos.yourproject.Launcher
```

The scaffold creates one rendered entity and moves it through a demo-specific `ExampleSystem`. Replace that behavior, component, and settings with the needs of the new simulation. Pong is another example of a demo package: `demos.pong` contains its launcher, simulation logic, player component, settings, and player system.

## Standalone Gradle Project

If the goal is an independent application rather than another demo in this engine, create a separate Gradle project and add PhysicsEngine as a dependency or include its engine source as a module. This repository is currently configured as a single Gradle project and does not publish an engine artifact.