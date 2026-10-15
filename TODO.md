# Physics Engine Roadmap

The primary goal is a reusable physics engine with additional capabilities that can host different kinds of projects. `demos/dots` and `demos/pong` are examples of engine usage; the reserved `src/main/java/yourproject` directory is for actual projects. This file tracks engine milestones and remaining work. Completion refers to behavior in the current source tree, not only to a component or API stub existing.

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