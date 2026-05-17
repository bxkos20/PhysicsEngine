# Documentación Operativa y de Configuración

## 📋 Resumen

Este documento describe la configuración operativa del Physics Engine, incluyendo variables de entorno, parámetros de configuración y estrategias de despliegue.

## 🔧 Configuración del Proyecto

El proyecto utiliza **configuración basada en código** en lugar de archivos externos (application.yml, application.properties). Toda la configuración se centraliza en:

**Archivo:** `src/main/java/engine/config/SimulationConfig.java`

### Estructura de Configuración

La configuración se organiza en clases estáticas anidadas por categoría:

| Categoría | Clase | Propósito |
|-----------|-------|-----------|
| Display | `SimulationConfig.Display` | Dimensiones de ventana/viewport |
| World | `SimulationConfig.World` | Dimensiones del mundo de simulación |
| Simulation | `SimulationConfig.Simulation` | Parámetros de física y timestep |
| Rendering | `SimulationConfig.Rendering` | Configuración de renderizado |
| Performance | `SimulationConfig.Performance` | Optimizaciones y profiling |

## 📊 Tabla de Parámetros de Configuración

### Display (Pantalla)

| Parámetro | Tipo | Valor Por Defecto | Obligatorio | Descripción |
|-----------|------|-------------------|-------------|-------------|
| `SCREEN_WIDTH` | int | 1280 | No | Ancho del viewport en píxeles |
| `SCREEN_HEIGHT` | int | 720 | No | Alto del viewport en píxeles |

### World (Mundo)

| Parámetro | Tipo | Valor Por Defecto | Obligatorio | Descripción |
|-----------|------|-------------------|-------------|-------------|
| `WORLD_WIDTH` | int | 1000 | No | Ancho del mundo en unidades de simulación |
| `WORLD_HEIGHT` | int | 1000 | No | Alto del mundo en unidades de simulación |
| `WORLD_ASPECT_RATIO` | float | 1.0 | No | Ratio de aspecto (calculado automáticamente) |

### Simulation (Simulación)

| Parámetro | Tipo | Valor Por Defecto | Obligatorio | Descripción |
|-----------|------|-------------------|-------------|-------------|
| `TOTAL_DOTS` | int | 1500 | No | Número total de partículas/entidades |
| `DOT_TYPES` | int | 10 | No | Número de tipos de partículas distintos |
| `DOTS_PER_TYPE` | int | 150 | No | Partículas por tipo (calculado) |
| `FIXED_TIMESTEP_SIMULATION` | float | 0.0167 | No | Timestep fijo de física (1/60 = 60Hz) |
| `FIXED_TIMESTEP_RENDERING` | float | 0.0333 | No | Timestep fijo de renderizado (1/30 = 30Hz) |
| `MAX_FRAME_TIME` | float | 0.25 | No | Tiempo máximo de frame para evitar spiral of death |
| `DEFAULT_TIMESCALE` | float | 1.0 | No | Multiplicador de tiempo (1.0 = tiempo real) |
| `GRAVITY_CONSTANT` | float | 50.0 | No | Fuerza de atracción gravitacional entre partículas |
| `MIN_INTERACTION_DISTANCE` | float | 15.0 | No | Distancia mínima para cálculos de interacción |
| `MAX_INTERACTION_DISTANCE` | float | 100.0 | No | Distancia máxima para cálculos de interacción |

### Rendering (Renderizado)

| Parámetro | Tipo | Valor Por Defecto | Obligatorio | Descripción |
|-----------|------|-------------------|-------------|-------------|
| `BATCH_VERTICES` | int | 50000 | No | Máximo de vértices por batch |
| `BATCH_INDICES` | int | 150000 | No | Máximo de índices por batch |
| `DEFAULT_DOT_RADIUS` | float | 5.0 | No | Radio por defecto de partículas en unidades del mundo |

### Performance (Rendimiento)

| Parámetro | Tipo | Valor Por Defecto | Obligatorio | Descripción |
|-----------|------|-------------------|-------------|-------------|
| `ENABLE_MULTITHREADING` | boolean | true | No | Habilita procesamiento paralelo para colisiones |
| `GRID_CELL_SIZE` | int | 50 | No | Tamaño de celda para particionamiento espacial (píxeles) |
| `ENABLE_PROFILING` | boolean | true | No | Habilita salida de timing/profiling |

## 🔒 Variables de Entorno

**Estado:** Actualmente, el proyecto **NO utiliza variables de entorno**. Toda la configuración está codificada en `SimulationConfig.java`.

### Variables de Entorno Recomendadas (Futuro)

Si se desea externalizar la configuración, se recomienda implementar las siguientes variables de entorno:

| Variable | Propósito | Valor Por Defecto | Obligatorio |
|----------|-----------|-------------------|-------------|
| `PHYSICS_ENGINE_SCREEN_WIDTH` | Ancho de pantalla | 1280 | No |
| `PHYSICS_ENGINE_SCREEN_HEIGHT` | Alto de pantalla | 720 | No |
| `PHYSICS_ENGINE_WORLD_WIDTH` | Ancho del mundo | 1000 | No |
| `PHYSICS_ENGINE_WORLD_HEIGHT` | Alto del mundo | 1000 | No |
| `PHYSICS_ENGINE_TOTAL_DOTS` | Número de partículas | 1500 | No |
| `PHYSICS_ENGINE_ENABLE_MULTITHREADING` | Habilitar multithreading | true | No |
| `PHYSICS_ENGINE_ENABLE_PROFILING` | Habilitar profiling | true | No |

## 🐳 Contenedorización (Docker)

**Estado:** Actualmente, el proyecto **NO incluye** Dockerfile ni docker-compose.yml.

### Estrategia de Contenedorización Recomendada

Para desplegar el proyecto en contenedores, se recomienda la siguiente estructura:

#### Dockerfile Sugerido

```dockerfile
# Etapa de build
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build --no-daemon

# Etapa de runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### docker-compose.yml Sugerido

```yaml
version: '3.8'
services:
  physics-engine:
    build: .
    ports:
      - "8080:8080"
    environment:
      - PHYSICS_ENGINE_SCREEN_WIDTH=1280
      - PHYSICS_ENGINE_SCREEN_HEIGHT=720
      - PHYSICS_ENGINE_ENABLE_MULTITHREADING=true
```

### Construcción de Imagen

```bash
# Construir imagen
docker build -t physics-engine:latest .

# Ejecutar contenedor
docker run -p 8080:8080 physics-engine:latest
```

## 🚀 Comandos de Operación

### Compilación

```bash
# Compilar proyecto
./gradlew build

# Compilar sin tests
./gradlew build -x test

# Limpiar y compilar
./gradlew clean build
```

### Ejecución

```bash
# Ejecutar aplicación
./gradlew run

# Ejecutar con parámetros de JVM personalizados
./gradlew run -DjvmArgs="-Xmx2G -Xms512M"
```

### Testing

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests específicos
./gradlew test --tests PhysicsEngineTest

# Ejecutar tests con reporte de cobertura
./gradlew test jacocoTestReport
```

### Limpieza

```bash
# Limpiar build artifacts
./gradlew clean

# Limpiar incluyendo caches
./gradlew clean cleanCache
```

## 📈 Monitoring y Profiling

### Habilitar Profiling

El profiling se habilita vía `SimulationConfig.Performance.ENABLE_PROFILING = true`.

### Métricas Disponibles

El motor proporciona información de profiling a través del método `getProfilingInfo()`:

| Métrica | Fuente | Descripción |
|---------|--------|-------------|
| Tiempo de física | `MovementSystem` | Tiempo de integración física en ms |
| Tiempo de colisiones | `CollisionSystem` | Tiempo de detección/resolución en ms |
| Tiempo de renderizado | `IRenderer` | Tiempo de dibujado en ms |
| Tiempo de simulación | `ISimulationLogic` | Tiempo de lógica personalizada en ms |

### Acceso a Profiling

```java
String profilingInfo = simulation.getProfilingInfo();
System.out.println(profilingInfo);
// Salida: "Physics: 2.34ms | Collisions: 5.67ms | Rendering: 1.23ms | Simulation: 0.45ms"
```

## 🔍 Troubleshooting

### Problemas Comunes

#### 1. OutOfMemoryError

**Síntoma:** `java.lang.OutOfMemoryError: Java heap space`

**Solución:**
```bash
./gradlew run -DjvmArgs="-Xmx4G"
```

#### 2. Bajo Rendimiento con Muchas Partículas

**Síntoma:** FPS baja con >1000 partículas

**Soluciones:**
- Aumentar `GRID_CELL_SIZE` en `SimulationConfig.Performance`
- Verificar que `ENABLE_MULTITHREADING = true`
- Reducir `TOTAL_DOTS` en `SimulationConfig.Simulation`

#### 3. Jitter en Colisiones

**Síntoma:** Entidades vibran o se superponen

**Soluciones:**
- Reducir `FIXED_TIMESTEP_SIMULATION` para mayor precisión
- Aumentar `restitution` en `PhysicsComponent`
- Revisar masas de entidades (evitar masas muy dispares)

## 📝 Notas de Implementación

### TODOs Relacionados con Configuración

Según el archivo `TODO.md`, hay mejoras pendientes:

1. **Make simulation configurable** (Versión 0.2)
   - Inyectar todas las dependencias
   - La simulación debe ser configurable

2. **JSON save method** (SimulationConfig.java)
   - Implementar método para guardar/cargar configuración desde JSON

### Limitaciones Actuales

- Configuración hardcoded en código fuente
- No soporta recarga de configuración en runtime
- No hay validación de valores de configuración
- No hay soporte para perfiles de configuración (dev/prod)

## 🔮 Roadmap de Configuración

1. **Corto Plazo:**
   - Implementar carga de configuración desde JSON/YAML
   - Añadir validación de parámetros
   - Soporte para variables de entorno

2. **Medio Plazo:**
   - Perfiles de configuración (dev, test, prod)
   - Recarga de configuración en runtime
   - UI para ajustar parámetros en tiempo real

3. **Largo Plazo:**
   - Sistema de presets de configuración
   - Optimización automática de parámetros basada en hardware
