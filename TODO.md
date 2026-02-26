# 📝 Physics Engine - TODO List

## 🚀 Nuevas Características (Features)
- [ ] **GUI Personalizada (Custom UI):**
  - [ ] Crear un sistema básico de renderizado de cajas de texto y botones.
  - [ ] Implementar detección de clics del ratón (Raycasting/AABB) sobre los botones de la GUI.
  - [ ] Crear un panel para modificar variables globales (`G`, tamaño) en tiempo real.
- [ ] **Sistema de Colisiones Avanzado:**
  - [ ] Añadir soporte para colisionadores cuadrados/rectangulares (AABB).
  - [ ] Implementar resolución de colisiones mixtas (Círculo vs Cuadrado).

## ⚡ Optimización y Refactorización
- [ ] **Actualización Eficiente del Grid (Broad-phase):**
  - [ ] Crear array `previousCells` en `ToroidalGridPartition` para recordar la celda de cada entidad.
  - [ ] Crear la función `refresh(List<GameObject>)` para actualizar solo los Dots que crucen de celda.

## 🐛 Bugs Conocidos (Known Issues)
- [ ] **Solapamiento por compresión (Jitter/Overlap):** Si muchos objetos se empujan entre sí al mismo tiempo en un espacio reducido, el sistema de resolución de colisiones no da abasto y los objetos acaban solapándose o vibrando.

## 💡 Backlog / Ideas a Futuro (Brainstorming)
- [ ] **Spawning Dinámico:** Permitir añadir y eliminar Dots haciendo clic en la pantalla mientras el juego corre.
- [ ] **Capas de Colisión (Collision Layers):** Filtrar qué objetos pueden chocar contra qué objetos.
