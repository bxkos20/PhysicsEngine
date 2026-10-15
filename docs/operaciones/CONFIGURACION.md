# Configuration and Operations

## Configuration Model

Configuration is created in Java code. There is no `SimulationConfig` class, configuration file loader, environment-variable binding, or runtime reload mechanism. `Settings` stores one `ISettings` instance per concrete class. Adding another object of the same class replaces the previous one; requesting a class that was not added returns `null`.

`Settings.createDefault()` installs these engine settings:

| Settings type | Field | Default | Meaning |
|---|---|---:|---|
| `ScreenSettings` | `width`, `height` | 1280, 720 | Window and viewport size in pixels. |
| `WorldSettings` | `width`, `height` | 1000, 1000 | World dimensions in simulation units. |
| `WorldSettings` | `boardType` | `EDGED` | Boundary behavior: `EDGED` or `TOROIDAL`. |
| `WorldSettings` | `collisionType` | `ELASTIC` | Collision strategy selected by `WorldFactory`. |
| `WorldSettings` | `gridPartitionSize` | 25 | Spatial grid cell size. A non-positive value selects a factory-derived size. |
| `SimulationSettings` | `fixedTimestepSimulation` | 1/60 s | Fixed simulation step used by the LibGDX loop. |
| `SimulationSettings` | `fixedTimestepRendering` | 1/30 s | Fixed render interval used by the LibGDX loop. |
| `SimulationSettings` | `maxFrameTime` | 0.25 s | Maximum frame delta accumulated by the backend. |
| `SimulationSettings` | `defaultTimescale` | 1.0 | Initial simulation time multiplier. |
| `SimulationSettings` | `physicsSubSteps` | 2 | Stored simulation setting; verify consumers before assuming it changes the current movement system. |
| `RenderingSettings` | `batchVertices` | 50000 | Renderer batch vertex capacity. |
| `RenderingSettings` | `batchIndices` | 150000 | Renderer batch index capacity. |
| `PerformanceSettings` | `enableMultithreading` | `true` | Enables threaded processing in core systems. |

Settings fields are public mutable fields. The current classes do not validate all values, so callers should provide sensible positive dimensions and timestep values.

## Demo Configurations

The launcher values override defaults by adding instances to `Settings`:

| Launcher | World | Simulation-specific settings |
|---|---|---|
| `pong.Launcher` | 1000 x 500, `EDGED`, elastic collision, grid size 0 (factory-derived) | Default `PongSettings`; multithreading enabled. |
| `demos.dots.Launcher` | 2500 x 2500, `TOROIDAL`, elastic collision, grid size 5 | 5000 dots, 10 types, radius 2.5, interaction strength 1, interaction distances 10 and 50; 1920 x 1080 screen. |

The dots demo's `DotSettings` is not part of `Settings.createDefault()` and must be added by its launcher. The Pong demo similarly adds `PongSettings`.

When `WorldSettings.gridPartitionSize <= 0`, `WorldFactory` selects `max(10, max(width, height) / 25)` as the cell size. It currently creates a `ToroidalGridPartition` regardless of board type; board geometry still controls the final distance and boundary calculations.

## Runtime Input

The LibGDX backend implements a fixed-step accumulator for simulation and rendering. It clamps the frame delta to `maxFrameTime`, then uses `fixedTimestepSimulation`, `fixedTimestepRendering`, and `defaultTimescale`.

Pong reads player input through `IKeyInput`: player one uses W/S and player two uses O/L. Camera pan and zoom are handled by the LibGDX camera controller.

`BackendSimulation` currently exposes `timeScale`, `pause`, and `render` state, but does not poll keys to change those fields. Its class documentation lists Up/Down, Left/Right, P, and R controls, but those controls are not wired in the current render loop. Likewise, dot interaction values are randomized when `DotInteractionManager` is created; there is no runtime E-key hook to randomize them again.

## Build, Test, and Run

Use the included Gradle Wrapper and a Java 21 JDK:

```bash
./gradlew build
./gradlew test
./gradlew run                         # pong.Launcher
./gradlew run -PmainClass=demos.dots.Launcher
./gradlew runLauncher -PmainClass=pong.Launcher
```

The graphical launchers need a desktop graphics environment. The dedicated `runLauncher` task uses the same `mainClass` project property as `run`.

To run a focused test class:

```bash
./gradlew test --tests 'engine.physics.ElasticCollisionTest'
```

## Profiling

`EngineSimulation.getProfilingInfo()` reports recent timing averages for world update, movement, collision, rendering, and custom simulation logic. Profiling is currently always wired into the relevant classes; there is no `ENABLE_PROFILING` configuration field.

## Operational Limits

- There are no environment variables or external config files.
- There is no Dockerfile, Compose file, HTTP service, or server deployment target.
- No `jacocoTestReport` task is configured in `build.gradle`.
- The integrated backend uses LibGDX/LWJGL3 for desktop.
- The collision strategy currently handles circles end to end. AABB construction exists on `ColliderComponent`, but AABB collision detection and response are not implemented by `ElasticCollision`.
- Dense collision scenarios can jitter or leave small overlaps; inspect timestep, masses, and the collision response before treating parameter changes as a guaranteed fix.
