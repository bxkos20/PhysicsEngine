# Physics Engine

Un motor de física 2D escrito en Java que implementa una arquitectura Entity-Component-System (ECS) con soporte para simulación de partículas, detección de colisiones y renderizado optimizado.

## 📋 Descripción

Este proyecto es un motor de física educativo y de alto rendimiento diseñado para simulaciones de partículas en 2D. Utiliza una arquitectura ECS (Entity-Component-System) para maximizar la flexibilidad y el rendimiento, permitiendo procesar miles de entidades en paralelo mediante multithreading.

### Características Principales

- **Arquitectura ECS**: Sistema de componentes flexible para composición de entidades
- **Física Realista**: Integración semi-implícita de Euler con colisiones elásticas
- **Particionamiento Espacial**: Grid hashing toroidal para detección de colisiones eficiente (O(n) vs O(n²))
- **Multithreading**: Procesamiento paralelo utilizando todos los núcleos CPU disponibles
- **Topología Toroidal**: Mundo con wrap-around (sin bordes físicos)
- **Renderizado Optimizado**: Batch rendering con LibGDX
- **Configuración Centralizada**: Parámetros de simulación ajustables vía `SimulationConfig`

## 🛠 Stack Tecnológico

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 | Lenguaje principal |
| Gradle | - | Gestión de dependencias y build |
| LibGDX | 1.12.1 | Renderizado y backend de gráficos |
| JUnit | 5.10.2 | Testing |

## 📦 Estructura del Proyecto

```
src/main/java/
├── engine/              # Núcleo del motor
│   ├── ecs/            # Entity-Component-System
│   │   ├── GameObject.java
│   │   ├── ComponentRegistry.java
│   │   ├── components/ # Transform, Physics, Collider, Render
│   │   └── systems/    # Movement, Collision, System base
│   ├── physics/        # Física y colisiones
│   │   ├── Collision.java
│   │   └── ElasticCollision.java
│   ├── world/          # Mundo y particionamiento
│   │   ├── World.java
│   │   ├── board/      # Board, ToroidalBoard
│   │   └── spatial/    # GridPartition, ToroidalGridPartition
│   ├── math/           # Vector2, Matrix4, Vector3
│   ├── graphics/       # Renderizado y formas
│   ├── inputs/         # Input handling
│   ├── config/         # SimulationConfig
│   └── app/            # EngineSimulation, EngineLauncher
├── backend/            # Implementaciones de backend
│   └── libgdx/         # Backend LibGDX
├── demos/              # Ejemplos de uso
│   └── dots/           # Demo de partículas
└── pong/             # Configuración inicial
    ├── Launcher.java
    ├── Simulation.java
    └── Configuration.java
```

## 🚀 Prerrequisitos

- **Java 21** o superior
- **Gradle** (incluido con el proyecto via Gradle Wrapper)
- Sistema operativo: Windows, macOS o Linux

## 📥 Instalación

```bash
# Clonar el repositorio
git clone <repository-url>
cd Physicengine

# El proyecto incluye Gradle Wrapper, no es necesario instalar Gradle por separado
```

## 🔧 Compilación

```bash
# Compilar el proyecto
./gradlew build

# Limpiar y compilar
./gradlew clean build
```

## 🧪 Ejecutar Tests

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con cobertura
./gradlew test jacocoTestReport
```

## ▶️ Ejecutar la Aplicación

```bash
# Ejecutar la aplicación
./gradlew run

# O ejecutar directamente con Java
./gradlew build
java -jar build/libs/physics-engine-1.0.jar
```

## ⚙️ Configuración

La configuración se centraliza en `engine/config/SimulationConfig.java`. Parámetros principales:

- **Display**: Dimensiones de pantalla (1280x720 por defecto)
- **World**: Dimensiones del mundo de simulación (1000x1000)
- **Simulation**: Número de partículas, timestep, constantes de gravedad
- **Rendering**: Tamaño de batch, radio de partículas
- **Performance**: Habilitar multithreading, tamaño de grid, profiling

## 📚 Arquitectura ECS

El motor utiliza un patrón Entity-Component-System:

- **Entity (GameObject)**: Contenedor único con ID y bitmask de componentes
- **Component**: Datos puros (Transform, Physics, Collider, Render)
- **System**: Lógica de procesamiento que opera sobre entidades con componentes específicos

### Pipeline de Actualización

1. **Phase 0**: Añadir entidades pendientes
2. **Phase 1**: Reconstruir grid espacial
3. **Phase 2**: Lógica personalizada (ISimulationLogic)
4. **Phase 3**: Integración física (MovementSystem)
5. **Phase 4**: Detección y resolución de colisiones (CollisionSystem)

## 🔬 Documentación

- [ADR-001: Stack Tecnológico](docs/arquitectura/ADR-001-stack-tecnologico.md)
- [Documentación de APIs](docs/api/ENDPOINTS.md)
- [Configuración Operativa](docs/operaciones/CONFIGURACION.md)
- [Guía de Contribución](CONTRIBUTING.md)

## 🐛 Bugs Conocidos

- **Jitter/Overlap bajo compresión**: Cuando múltiples objetos se empujan simultáneamente en un espacio confinado, la resolución de colisiones puede causar superposición o vibración de entidades.

## 💡 Ideas Futuras

- GUI personalizada con detección de clicks
- Soporte para colisiones rectangulares (AABB)
- Sistema de repulsión por densidad de puntos
- Spawning dinámico durante runtime
- Capas de colisión con bitmask filtering
- Reemplazar LibGDX con código propio

## 📄 Licencia

[Specifique la licencia aquí]

## 👥 Autores

[Specifique los autores aquí]
