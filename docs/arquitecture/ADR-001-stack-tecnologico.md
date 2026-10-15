# ADR-001: Technology Stack

## Status
Accepted

## Date
2026-05-17

## Context

The project is a 2D physics and simulation engine intended for interactive demos, including particle workloads. The implementation needs a maintainable Java core, a real-time desktop display, configurable simulation behavior, and a testable foundation for ECS and collision code.

## Decisions

### Java 21

Java 21 is the language baseline configured by the Gradle toolchain.

Reasons:

- The JVM provides a mature runtime, tooling ecosystem, and JIT compilation.
- Java's type system and interfaces support the engine's pluggable simulation, renderer, input, board, and collision abstractions.
- The project can use current language and runtime features when useful; the codebase does not currently depend on virtual threads.
- Java has a lower-level learning and memory-management burden than C++ while retaining good performance for this project.

Alternatives considered included C++ for lower-level control, Rust for memory safety, and C# for its game-development ecosystem. Java was chosen for this project's balance of approachability, portability, and tooling.

### LibGDX and LWJGL3

LibGDX supplies the current application lifecycle, desktop window, input, camera integration, and rendering backend. The engine-facing code depends on interfaces such as `IRenderer` and `IKeyInput` rather than LibGDX types in its simulation APIs. The configured runtime is desktop LibGDX with LWJGL3; other LibGDX platform targets are not configured in this repository.

The renderer uses batched drawing. A custom renderer remains a possible future direction, but replacing LibGDX is not required for the engine's current abstractions.

### Gradle

Gradle with the repository's wrapper handles compilation, dependency resolution, application launch, and JUnit execution. The build is currently a single Java project, not a configured multi-project build.

### JUnit Jupiter 5

JUnit Jupiter is used for tests of ECS behavior, math primitives, collision behavior, world construction, board geometry, and spatial partitioning. Tests run through Gradle's JUnit Platform integration.

### Entity-Component-System

The engine uses `GameObject` entities, component objects, and systems that filter entities by component signatures. This separates simulation data from the processing logic and allows demos to compose behavior without subclassing engine entities. The implementation is a compact ECS, not a data-oriented archetype store: components are held in a fixed-size object array on each entity.

### Grid-Based Spatial Partitioning

`GridPartition` and `ToroidalGridPartition` provide nearby-entity queries to reduce the number of collision candidates in typical spatially distributed workloads. The reduction depends on occupancy and cell sizing; it is not a strict O(n) worst-case guarantee. The grid is rebuilt during each `World.update`.

`WorldFactory` currently constructs a `ToroidalGridPartition` for both board types. `Board` implementations determine actual boundary handling and distance calculations. The only collision strategy currently supplied is `ElasticCollision`.

### Shared Thread Pool

The base system implementation uses a shared fixed thread pool sized to the number of available processors. Settings can disable threaded processing. The collision system can collect detected pairs in parallel, then resolves those pairs sequentially to avoid concurrent mutation during response. Small general-system workloads fall back to sequential processing.

## Consequences

### Positive

- The engine core can be exercised independently of the current graphics backend through its interfaces.
- A single source tree contains the reusable engine and runnable simulations.
- Gradle Wrapper makes the build reproducible without requiring a system Gradle installation.
- The ECS, physics, geometry, and spatial partition have focused automated tests.

### Trade-offs and Limitations

- The integrated runtime is tied to desktop LibGDX/LWJGL3 until additional backends are configured.
- JVM startup and memory overhead are higher than a native executable, though the current interactive simulations are long-running applications.
- The spatial grid is rebuilt every update and its performance depends on workload distribution.
- AABB colliders can be constructed, but `ElasticCollision` currently performs circle-based narrow-phase collision checks and response only.
- Collision response can jitter or leave overlap in dense compression scenarios.

## References

- [LibGDX documentation](https://libgdx.com/)
- [Java 21](https://openjdk.org/projects/jdk/21/)
- [ECS FAQ](https://github.com/SanderMertens/ecs-faq)
