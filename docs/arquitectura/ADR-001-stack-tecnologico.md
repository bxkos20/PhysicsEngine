# ADR-001: Selección del Stack Tecnológico

## Estado
Aceptado

## Fecha
2026-05-17

## Contexto
Se requiere desarrollar un motor de física 2D con las siguientes características:
- Alto rendimiento para simular miles de partículas
- Arquitectura flexible y extensible
- Renderizado en tiempo real
- Soporte para multithreading
- Código portable entre plataformas

## Decisiones

### Java 21 como Lenguaje Principal

**Razón:**
- **Records y Pattern Matching**: Java 21 introduce features modernas que reducen boilerplate
- **Virtual Threads (Project Loom)**: Aunque no se utiliza actualmente, ofrece potencial para concurrencia mejorada
- **Performance**: JVM con JIT compilation ofrece rendimiento competitivo para cálculos numéricos
- **Ecosistema Mature**: Amplia disponibilidad de librerías y herramientas
- **Portabilidad**: "Write Once, Run Anywhere" - esencial para un engine multiplataforma

**Alternativas Consideradas:**
- **C++**: Mayor rendimiento crudo pero complejidad de gestión de memoria y build
- **Rust**: Seguridad de memoria pero curva de aprendizaje steep y ecosistema menos mature para game dev
- **C#**: Excelente para Unity pero dependiente de .NET runtime

### LibGDX para Renderizado y Backend

**Razón:**
- **Abstracción de Backend**: Unifica código para desktop, Android, iOS y web (GWT)
- **Batch Rendering**: Optimizado para dibujar miles de sprites en una sola llamada
- **OpenGL Wrapper**: Acceso directo a GPU sin necesidad de escribir shaders desde cero
- **Input Handling**: Unificado para teclado, mouse y touch
- **Comunidad Activa**: Amplia documentación y ejemplos disponibles
- **Ligero**: Sin dependencias pesadas como Unity o Unreal

**Alternativas Consideradas:**
- **LWJGL**: Más bajo nivel pero requiere más código boilerplate
- **JavaFX**: Orientado a UI de aplicaciones, no optimizado para game loops
- **Propio Renderer**: Mayor control pero tiempo de desarrollo significativo (notado en TODO.md como idea futura)

### Gradle para Build System

**Razón:**
- **Performance**: Build incremental y daemon mode para compilaciones rápidas
- **Dependency Management**: Resolución de dependencias robusta
- **Multi-project Support**: Ideal para proyectos modulares (engine, demos, backend)
- **Gradle Wrapper**: Elimina necesidad de instalar Gradle localmente
- **Plugins Ecosistema**: Amplia disponibilidad para testing, profiling, etc.

**Alternativas Consideradas:**
- **Maven**: Más estándar pero configuración más verbosa y menos flexible
- **Ant**: Obsoleto, sin gestión de dependencias moderna

### JUnit 5 para Testing

**Razón:**
- **Standard de Industria**: Ampliamente adoptado en ecosistema Java
- **Parameterized Tests**: Útil para probar diferentes configuraciones de física
- **Integration con Gradle**: Soporte nativo y seamless

## Arquitectura ECS (Entity-Component-System)

**Razón:**
- **Performance**: Permite procesar componentes en memoria contigua (cache-friendly)
- **Flexibilidad**: Composición de comportamiento en runtime sin herencia
- **Parallelism**: Systems pueden procesar entidades en paralelo fácilmente
- **Data-Oriented**: Separa datos de lógica, ideal para simulaciones masivas

**Alternativas Consideradas:**
- **OOP Tradicional**: Herencia profunda, difícil de paralelizar
- **Data-Oriented Design (DOD) Puro**: Mayor rendimiento pero complejidad alta

## Particionamiento Espacial con Grid Hashing

**Razón:**
- **O(n) vs O(n²)**: Reduce complejidad de detección de colisiones de cuadrática a lineal
- **Toroidal Topology**: Soporta mundos con wrap-around sin bordes físicos
- **Simple Implementation**: Fácil de entender y mantener
- **Cache Friendly**: Acceso localizado a memoria

**Alternativas Consideradas:**
- **Quadtree**: Mejor para distribuciones no uniformes pero más complejo
- **Spatial Hashing**: Similar pero con overhead de hash function
- **Brute Force**: O(n²) - inviable para >1000 entidades

## Multithreading con Thread Pool Manual

**Razón:**
- **Control Granular**: Permite optimizar distribución de trabajo por CPU core
- **Evita GC Pressure**: Reutiliza threads en lugar de crear/destroy por frame
- **CountDownLatch**: Sincronización simple y eficiente
- **Fallback a Sequential**: Para workloads pequeños donde overhead de threading supera beneficios

**Alternativas Consideradas:**
- **parallelStream()**: Más simple pero genera GC pressure por lambda allocations
- **CompletableFuture**: Más verboso y overhead similar
- **Actor Model (Akka)**: Overkill para este caso de uso

## Consecuencias

### Positivas
- Alto rendimiento con código Java puro
- Portabilidad multiplataforma sin cambios
- Arquitectura modular y extensible
- Build system moderno y eficiente

### Negativas
- Dependencia de LibGDX limita opciones de backend (notado en TODO.md como reemplazo futuro)
- JVM startup time (no significativo para aplicaciones de larga duración)
- Memory overhead de JVM comparado con C++

## Referencias
- [LibGDX Documentation](https://libgdx.com/)
- [Java 21 Features](https://openjdk.org/projects/jdk/21/)
- [ECS Architecture](https://github.com/SanderMertens/ecs-faq)
