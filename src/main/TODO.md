# 📝 Physics Engine - TODO List

## 🎯 Version 0.2
- [ ] **Create version 0.1 with a minimally functional engine**
  - [X] Implement basic ECS system with entities and components
  - [ ] Create basic physics system with gravity and collisions
  - [X] Implement spatial partitioning for collision optimization
  - [ ] Create basic rendering system for visualization
  - [X] Add basic simulation configuration
  - [X] Create simple demo with interacting dots
  - [ ] Document basic API and usage examples
  - [X] Create simple demo with pong game
  - [ ] Make configuration simulation configurable
  - [ ] Modify project structure !!!
    - [ ] Injectate all the dependencies
    - [ ] Should be simulation configurable
  - [ ] Make component an abstract class

## 🚀 Features
- [ ] **Custom GUI:**
  - [ ] Create a basic rendering system for UI boxes and text/buttons.
  - [ ] Implement mouse click detection (Raycasting/AABB) for GUI interactions.
  - [ ] Create a debug panel to modify global variables (e.g., gravity `G`, Dot size) in real-time.
- [ ] **Advanced Collision System:**
  - [ ] Add support for rect/rectangular colliders (AABB - Axis-Aligned Bounding Box).
  - [ ] Implement mixed collision resolution (Circle vs. AABB).
- [ ] **Dot Density Repulsion System**

## ⚡ Optimization & Refactoring
- [ ] **Efficient Grid Update (Broad-phase):**
  - [ ] Add a `previousCells` array in `ToroidalGridPartition` to track entity positions.
  - [ ] Implement the `refresh(List<engine.gameObject>)` method to only update entities that cross cell boundaries.

## 📖 Documentation & Code Quality
- [ ] **Javadoc & Code Comments:**
  - [ ] Translate all existing inline comments from Spanish to English.
  - [ ] Write standard Javadoc for engine ECS classes (`engine.gameObject`, `System`, `ComponentRegistry`).
  - [ ] Add detailed inline comments explaining the complex math in `CollisionSystem` and `DotSystem`.
- [ ] **Project Documentation (README / Wiki):**
  - [ ] Write a comprehensive `README.md` explaining the engine's purpose and features.
  - [ ] Document the custom Entity-Component-System (ECS) architecture and workflow.
  - [ ] Explain the underlying logic of the Toroidal Spatial Hashing (Grid Partitioning).

## 🐛 Known Bugs
- [ ] **Jitter / Overlap under compression:** When multiple objects push each other simultaneously in a confined space, the collision resolution struggles, causing entities to overlap or vibrate.

## 💡 Future Ideas
- [ ] **Dynamic Spawning:** Allow adding and removing entities via mouse clicks during runtime.
- [ ] **Collision Layers:** Implement bitmask filtering to define which objects are allowed to collide with each other (e.g., UI vs. Physics objects).
- [ ] **Replace LibGDX with my own code** To have all control of the behavior of the code

## 🏛️ Architecture Refactoring (SOLID)

### 1. Decouple from Global Static Configuration (`SimulationConfig`)

**Problem:** The use of a `SimulationConfig` class with static members creates a global state. This prevents running multiple, independent simulations with different settings and tightly couples all engine components to a single source of truth.

**Violated Principle:** **Modularity** and **Testability**. Prevents simulations from being self-contained.

**Tasks:**
- [x] Convert `SimulationConfig` into a non-static Plain Old Java Object (POJO), e.g., `SimulationSettings`.
- [x] The application entry point (e.g., `EngineLauncher`) should be responsible for creating an instance of `SimulationSettings` (e.g., from a file or with default values).
- [x] Inject the `SimulationSettings` instance into `EngineSimulation`'s constructor.
- [x] `EngineSimulation` will then own the configuration and pass relevant parts down to the systems and logic that need it (e.g., passing world dimensions to `World`, or simulation parameters to `ISimulationLogic`).
- [x] Refactor all classes that currently read from the static `SimulationConfig` to receive these values via their constructor or an `init` method.

### 2. Apply Single Responsibility Principle to `EngineSimulation`

**Problem:** The `EngineSimulation` class is responsible for orchestrating the main simulation loop AND it contains the specific logic for how to render entities.

**Violated Principles:**
- **Single Responsibility Principle (SRP):** The class has two distinct responsibilities (orchestration and rendering logic), giving it more than one reason to change.
- **Open/Closed Principle (OCP):** To add a new rendering requirement (e.g., rendering rotation), the core `EngineSimulation` class must be modified.

**Tasks:**
- [ ] Create a new `RenderSystem` class in the `engine.ecs.systems` package.
- [ ] This system will be responsible for iterating over entities that have both a `RenderComponent` and a `TransformComponent`.
- [ ] Move the rendering logic from `EngineSimulation.render()` into the new `RenderSystem`.
- [ ] `EngineSimulation` will now simply hold an instance of `RenderSystem` and call its `update()` method within the main loop.

### 3. Apply Dependency Inversion to `World` Class

**Problem:** The `World` class creates its own dependencies, specifically `MovementSystem` and `CollisionSystem`. This couples the `World` to concrete implementations of these systems.

**Violated Principle:** **Dependency Inversion Principle (DIP).** High-level modules (`World`) should not depend on low-level modules (`MovementSystem`); both should depend on abstractions.

**Tasks:**
- [ ] Modify the `World` constructor to accept its required systems (e.g., `MovementSystem`, `CollisionSystem`) as parameters.
- [ ] Remove the `new MovementSystem(...)` and `new CollisionSystem(...)` calls from inside the `World` class.
- [ ] The responsibility for creating these systems should move up to a higher-level "composition root," likely `EngineSimulation`. `EngineSimulation` will create the systems and then inject them into the `World` when it is constructed.