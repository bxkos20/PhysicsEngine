# 📝 Physics Engine - TODO List

## 🎯 Version 0.1 - Minimally Functional Engine
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
