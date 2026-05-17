# Guía de Contribución - Physics Engine

Gracias por tu interés en contribuir al Physics Engine. Este documento describe las pautas y procesos para contribuir al proyecto.

## 📋 Tabla de Contenidos

- [Código de Conducta](#código-de-conducta)
- [Flujo de Trabajo con Git](#flujo-de-trabajo-con-git)
- [Convenciones de Commits](#convenciones-de-commits)
- [Estilo de Código](#estilo-de-código)
- [Proceso de Pull Request](#proceso-de-pull-request)
- [Reporte de Bugs](#reporte-de-bugs)
- [Sugerencia de Features](#sugerencia-de-features)

## 🤝 Código de Conducta

- **Respeto**: Trata a todos con respeto y profesionalidad
- **Colaboración**: Fomenta un ambiente colaborativo y constructivo
- **Inclusión**: Valora las perspectivas diversas
- **Comunicación**: Sé claro y constructivo en tus comunicaciones

## 🌿 Flujo de Trabajo con Git

### Branching Strategy

El proyecto utiliza un modelo de branching basado en **feature branches**:

```
main (rama principal)
  ├── feature/nombre-feature (nuevas funcionalidades)
  ├── fix/nombre-bug (correcciones de bugs)
  ├── docs/nombre-documentacion (actualizaciones de documentación)
  ├── refactor/nombre-refactor (refactorizaciones)
  └── test/nombre-test (agregar o mejorar tests)
```

### Pasos para Contribuir

1. **Fork el repositorio**
   ```bash
   # Fork desde GitHub/GitLab
   ```

2. **Clonar tu fork**
   ```bash
   git clone https://github.com/tu-usuario/Physicengine.git
   cd Physicengine
   ```

3. **Añadir upstream remote**
   ```bash
   git remote add upstream https://github.com/usuario-original/Physicengine.git
   ```

4. **Crear rama de feature**
   ```bash
   git checkout -b feature/tu-feature
   ```

5. **Realizar cambios y commits**
   ```bash
   git add .
   git commit -m "feat: agregar soporte para colisiones rectangulares"
   ```

6. **Push a tu fork**
   ```bash
   git push origin feature/tu-feature
   ```

7. **Crear Pull Request**
   - Ve a la página del repositorio original
   - Crea un Pull Request desde tu rama de feature

8. **Mantener tu rama actualizada**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

## 📝 Convenciones de Commits

Este proyecto sigue el estándar **Conventional Commits**.

### Formato

```
<tipo>(<alcance>): <descripción>

[opcional: cuerpo]

[opcional: pie de página]
```

### Tipos de Commits

| Tipo | Descripción | Ejemplo |
|------|-------------|---------|
| `feat` | Nueva funcionalidad | `feat: agregar soporte para colisiones AABB` |
| `fix` | Corrección de bug | `fix: resolver jitter en colisiones bajo compresión` |
| `docs` | Cambios en documentación | `docs: actualizar README con nuevas instrucciones` |
| `style` | Cambios de formato (código) | `style: aplicar formato consistente a Vector2` |
| `refactor` | Refactorización de código | `refactor: extraer lógica de grid a clase separada` |
| `perf` | Mejoras de rendimiento | `perf: optimizar detección de colisiones con cache` |
| `test` | Agregar o actualizar tests | `test: agregar tests para ComponentRegistry` |
| `chore` | Tareas de mantenimiento | `chore: actualizar Gradle a versión 8.5` |
| `build` | Cambios en build system | `build: agregar dependencia de JUnit 5` |
| `ci` | Cambios en CI/CD | `ci: configurar GitHub Actions` |

### Ejemplos

```bash
# Nueva funcionalidad
git commit -m "feat(physics): agregar soporte para colisiones rectangulares AABB"

# Corrección de bug
git commit -m "fix(collision): resolver jitter cuando múltiples objetos se comprimen"

# Documentación
git commit -m "docs: agregar guía de contribución"

# Refactorización
git commit -m "refactor(ecs): simplificar sistema de componentes"

# Rendimiento
git commit -m "perf(grid): reducir overhead de reconstrucción de grid"
```

### Commits con Breaking Changes

Si el cambio introduce una ruptura en la API pública:

```bash
git commit -m "feat(api): cambiar firma de GameObject.addComponent

BREAKING CHANGE: El método addComponent ahora retorna void en lugar de boolean"
```

## 🎨 Estilo de Código

Basado en el análisis del código actual del proyecto:

### Reglas Generales

1. **Indentación**: 4 espacios (no tabs)
2. **Longitud de línea**: Máximo 120 caracteres
3. **Codificación**: UTF-8
4. **Fin de línea**: LF (Unix-style)

### Convenciones de Nomenclatura

| Tipo | Convención | Ejemplo |
|------|------------|---------|
| Clases | PascalCase | `GameObject`, `MovementSystem` |
| Interfaces | PascalCase con prefijo 'I' | `IRenderer`, `IKeyInput` |
| Métodos | camelCase | `addComponent()`, `getVelocity()` |
| Variables | camelCase | `velocity`, `sumForces` |
| Constantes | UPPER_SNAKE_CASE | `SCREEN_WIDTH`, `TOTAL_DOTS` |
| Paquetes | lowercase | `engine.ecs`, `engine.physics` |

### Uso de `final`

- **Campos privados**: Usar `final` cuando sea posible (inmutabilidad)
- **Parámetros de método**: No usar `final` (sobrehead sin beneficio claro)
- **Variables locales**: Usar `final` cuando no se reasignan

**Ejemplo:**
```java
public class GameObject {
    private final int id;              // ✅ final - inmutable
    private long signature = 0L;       // ✅ no final - mutable
    
    public void addComponent(T component) {
        int compId = ...;              // ✅ no final - se reasigna
        // ...
    }
}
```

### Javadoc

- **Clases públicas**: Deben tener Javadoc completo
- **Métodos públicos/protegidos**: Deben tener Javadoc con `@param`, `@return`, `@throws`
- **Comentarios inline**: Explicar el "por qué", no el "qué"

**Ejemplo:**
```java
/**
 * Separates two colliding objects based on their mass ratio.
 * Static objects (mass=0) do not move.
 *
 * @param a               First game object
 * @param b               Second game object
 * @param directionNormal Normal direction from A to B
 * @param overlap         Penetration depth
 */
private void disconnection(GameObject a, GameObject b, Vector2 directionNormal, float overlap) {
    // ...
}
```

### Organización de Imports

- Ordenar imports por grupos:
  1. `java.*`
  2. `javax.*`
  3. Librerías de terceros
  4. Paquetes del proyecto
- Usar wildcards `*` solo para >5 imports del mismo paquete
- Eliminar imports no utilizados

### Estructura de Clase

```java
package engine.paquete;

// Imports

/**
 * Javadoc de clase.
 */
public class ClaseEjemplo {
    // 1. Constantes estáticas
    private static final int CONSTANTE = 10;
    
    // 2. Campos estáticos
    private static int contador = 0;
    
    // 3. Campos de instancia (final primero)
    private final int id;
    private String nombre;
    
    // 4. Constructor
    public ClaseEjemplo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    // 5. Métodos públicos
    public void metodoPublico() {}
    
    // 6. Métodos protegidos
    protected void metodoProtegido() {}
    
    // 7. Métodos privados
    private void metodoPrivado() {}
    
    // 8. Clases anidadas (si aplica)
    private static class ClaseAnidada {}
}
```

### Manejo de Excepciones

- **Lanzar excepciones específicas**: Evitar `Exception` genérica
- **Documentar excepciones**: Incluir `@throws` en Javadoc
- **Validar argumentos**: Lanzar `IllegalArgumentException` para argumentos inválidos

**Ejemplo:**
```java
/**
 * Creates a simulation core.
 *
 * @param renderer Renderer for drawing
 * @throws IllegalArgumentException if renderer is null
 */
public EngineSimulation(IRenderer renderer, ISimulationLogic simulationLogic, IKeyInput keyInput) {
    if (renderer == null) throw new IllegalArgumentException("Renderer cannot be null");
    // ...
}
```

### Comentarios Inline

- **Español en comentarios**: El código actual tiene comentarios en español. Traducir a inglés gradualmente
- **Explicar lógica compleja**: No comentar código obvio
- **TODOs**: Marcar con `TODO:` y asignar a versión/milestone si es posible

**Ejemplo:**
```java
// ✅ Bueno: explica el por qué
// Apply friction damping: v = v * (1 - friction * dt)
// Reduces velocity gradually without risking direction inversion
float frictionFactor = Math.max(0, 1 - physics.getFriction() * dt);

// ❌ Malo: solo repite el código
// Multiply velocity by friction factor
physics.getVelocity().scl(frictionFactor);
```

## 🔀 Proceso de Pull Request

### Antes de Crear PR

1. **Actualizar tu rama**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Ejecutar tests**
   ```bash
   ./gradlew test
   ```

3. **Compilar proyecto**
   ```bash
   ./gradlew build
   ```

4. **Verificar estilo de código** (si hay linter configurado)
   ```bash
   ./gradlew checkstyle
   ```

### Plantilla de PR

```markdown
## Descripción
Breve descricripción de los cambios realizados.

## Tipo de Cambio
- [ ] Bug fix (corrección de bug)
- [ ] New feature (nueva funcionalidad)
- [ ] Breaking change (cambio ruptura)
- [ ] Documentation update (actualización de documentación)

## Testing
- [ ] Tests unitarios agregados/pasados
- [ ] Tests de integración agregados/pasados
- [ ] Manual testing realizado

## Checklist
- [ ] Código sigue las convenciones de estilo
- [ ] Javadoc agregado/actualizado
- [ ] Commits siguen Conventional Commits
- [ ] No hay warnings de compilación
- [ ] Documentación actualizada si es necesario

## Issues Relacionados
Closes #123
```

### Revisión de PR

- **Respetuoso**: Ser constructivo en la revisión
- **Específico**: Apuntar a líneas específicas con feedback
- **Explicativo**: Explicar el por qué de sugerencias
- **Oportuno**: Revisar PRs en tiempo razonable

## 🐛 Reporte de Bugs

### Plantilla de Issue

```markdown
## Descripción
Descripción clara y concisa del bug.

## Pasos para Reproducir
1. Ir a '...'
2. Click en '....'
3. Scroll down to '....'
4. Ver error

## Comportamiento Esperado
Descripción de lo que debería pasar.

## Comportamiento Actual
Descripción de lo que realmente pasa.

## Screenshots
Si es aplicable, agregar screenshots.

## Entorno
- OS: [e.g. Windows 11, Ubuntu 22.04]
- Java Version: [e.g. 21]
- Gradle Version: [e.g. 8.5]

## Logs Adjuntos
[Adjuntar logs relevantes]

## Contexto Adicional
Cualquier otra información sobre el problema.
```

## 💡 Sugerencia de Features

### Plantilla de Feature Request

```markdown
## Descripción
Descripción clara de la feature solicitada.

## Problema que Resuelve
¿Qué problema resuelve esta feature? ¿Para qué usuarios?

## Solución Propuesta
Descripción detallada de la solución propuesta.

## Alternativas Consideradas
¿Qué alternativas se consideraron? ¿Por qué no se eligieron?

## Impacto
- ¿Afecta API pública?
- ¿Requiere cambios en configuración?
- ¿Impacta rendimiento?

## Prioridad
- [ ] Alta
- [ ] Media
- [ ] Baja
```

## 📚 Recursos de Aprendizaje

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Java Code Conventions](https://oracle.com/technetwork/java/codeconventions-150003.pdf)
- [Effective Java](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Clean Code](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)

## 📞 Contacto

- **Issues**: [GitHub Issues](https://github.com/usuario/Physicengine/issues)
- **Discussions**: [GitHub Discussions](https://github.com/usuario/Physicengine/discussions)
- **Email**: [email@example.com]

## 📄 Licencia

Al contribuir, aceptas que tus contribuciones se licencien bajo la misma licencia del proyecto.
