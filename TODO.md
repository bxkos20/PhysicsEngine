# Physics Engine Roadmap

The primary goal is a reusable physics engine with additional capabilities that can host different kinds of projects. `demos/dots` and `demos/pong` are examples of engine usage; the reserved `src/main/java/yourproject` directory is for actual projects. This file tracks engine milestones and remaining work. Completion refers to behavior in the current source tree, not only to a component or API stub existing.

Current release designation: `0.1.0` (early development; not a stable 1.0 release).

## Version 1.0 Roadmap

Target: a stable, documented 2D physics engine that projects can use through a clear API. Version 1.0 does not need every item in the general backlog; it must meet the release criteria below within a deliberately limited scope.

### Milestone 1: Define Scope and Delivery

- [ ] Confirm the supported 1.0 feature set: 2D worlds, edged and toroidal boundaries, movement integration, and elastic circle collisions.
- [ ] Explicitly list unsupported features, including end-to-end AABB collision handling, unless the scope is intentionally expanded.
- [ ] Decide whether 1.0 is consumed from this repository's single Gradle project or delivered as a separate engine library/artifact; document the chosen setup.

**Exit criteria:** a new project can identify the supported physics behavior, limitations, and dependency/setup path without relying on assumptions from the demos.

### Milestone 2: Physics Correctness

- [ ] Reproduce the dense-contact jitter/overlap issue with a deterministic test, then fix it or define and document an acceptable bounded limitation before release.
- [ ] Add focused tests for movement integration, elastic circle collisions, static bodies, and both board boundary modes.
- [ ] Add regression tests for any physics defects found during this milestone.

**Exit criteria:** all behavior inside the declared 1.0 scope has automated tests, and there are no untriaged correctness defects in that scope.

### Milestone 3: Public API and Lifecycle

- [ ] Validate required settings and reject invalid world dimensions, time steps, and physics values with clear errors.
- [ ] Decide and document entity lifecycle guarantees, including whether entity removal is supported in 1.0.
- [ ] Review mutable collections and runtime controls so projects do not need to depend on engine internals to use supported features.
- [ ] Document system ordering, threading behavior, and which state systems may safely modify.

**Exit criteria:** public contracts and failure behavior are documented and have focused tests; projects can use the engine without depending on demo or backend implementation classes.

### Milestone 4: Performance and Runtime Verification

- [ ] Benchmark representative entity counts with sequential and threaded processing; record the tested workload and results.
- [ ] Verify repeated simulation startup, update, and shutdown behavior, including renderer and worker-thread resource cleanup.
- [ ] Optimize spatial-grid rebuilding only if benchmarks show it is necessary for the declared 1.0 workload.

**Exit criteria:** performance claims match measured workloads, and repeated runs do not leak resources or leave engine state unusable.

### Milestone 5: Release Readiness

- [ ] Run `./gradlew clean build` and `./gradlew test` on Java 21 in CI.
- [ ] Verify documented launch/setup examples against the selected delivery model.
- [x] Choose and add a project license (CC BY-NC 4.0).
- [ ] Publish a 1.0 changelog/release note with supported features, known limitations, and migration guidance if needed.

**Exit criteria:** clean CI build and tests pass, release documentation matches the implementation, and the license and artifact/setup path are explicit.

### Not Required for 1.0 by Default

These remain optional unless the supported 1.0 scope is deliberately expanded:

- End-to-end AABB and mixed-shape collision handling.
- Debug GUI and collision layers.
- Camera movement implemented as an engine system.
- General-purpose system access to modify runtime variables such as simulation time scale.
- Incremental grid updates and additional rendering backends.

The general backlog below continues to track these and other future improvements. A 1.0 release should not be delayed merely to complete every optional item.

## Implemented

- [x] ECS entities, component registry, signatures, and component-based systems.
- [x] Typed settings passed through launchers and simulation logic.
- [x] World construction through `WorldFactory` with injected board, collision strategy, and spatial partition.
- [x] Movement integration, friction damping, and board boundary handling.
- [x] Elastic circle collision detection and response, including static bodies.
- [x] Grid-based broad-phase candidate queries.
- [x] Renderer abstraction and `RendererSystem`.
- [x] LibGDX desktop backend with fixed-step simulation/render loops.
- [x] Interacting-particle demo and Pong reference demo.
- [x] Java API and project documentation in English.

## Correctness and Physics

- [ ] Implement end-to-end AABB collision detection and response. `ColliderComponent` has an AABB constructor, but `ElasticCollision` currently uses circle radii only.
- [ ] Add mixed circle/AABB collision tests and response once the implementation exists.
- [ ] Investigate jitter and residual overlap under dense compression; add reproducible regression tests.
- [ ] Validate physics settings and reject invalid dimensions, masses, time steps, and interaction ranges where appropriate.

## Performance and Lifecycle

- [ ] Add incremental spatial-grid updates so entities that remain in the same cell do not require full-grid rebuilding each world update.
- [ ] Define and implement entity removal and runtime spawning lifecycle APIs.
- [ ] Benchmark sequential and threaded processing across representative entity counts and distributions before changing threading thresholds.

## Features

- [ ] Implement camera movement through a dedicated system rather than handling camera motion only in the backend controller.
- [ ] Provide a controlled mechanism for systems to modify runtime engine variables, such as the simulation time scale.
- [ ] Add collision layers or masks to filter candidate pairs.
- [ ] Add optional runtime particle spawning and removal.
- [ ] Add a lightweight in-app debug UI for simulation settings and profiling.
- [ ] Consider additional backends only when a concrete target requires them; the engine already has renderer and input abstractions.

## Documentation and Code Quality

- [ ] Translate remaining Spanish source comments and normalize terminology.
- [ ] Complete and standardize Javadocs for ECS, collision, and simulation contracts.
- [ ] Document the spatial-grid query algorithm and its workload-dependent performance characteristics.
- [ ] Keep documentation aligned with source signatures and verified behavior.

## Known Issue

- Dense, highly compressed contacts can jitter or leave entities slightly overlapping. The root cause and a robust correction are still open.