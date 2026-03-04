---
description: Realiza un análisis no destructivo de consistencia y calidad entre spec.md, plan.md y tasks.md después de generar tareas.
---

## Entrada del usuario

```text
$ARGUMENTS
```

Usted **DEBE** considerar la entrada del usuario antes de continuar (si no está vacía).

## Objetivo`r`n
Identifique inconsistencias, duplicaciones, ambigüedades y elementos poco especificados en los tres artefactos principales (`spec.md`, `plan.md`, `tasks.md`) antes de la implementación. Este comando DEBE ejecutarse solo después de que `/speckit.tasks` haya producido con éxito un `tasks.md` completo.

## Restricciones operativas

**ESTRICTAMENTE SOLO LECTURA**: **no** modifique ningún archivo. Genere un informe de análisis estructurado. Ofrezca un plan opcional de remediación (el usuario debe aprobar explícitamente antes de ejecutar manualmente cualquier comando de edición posterior).

**Autoridad de la Constitución**: La constitución del proyecto (`.specify/memory/constitution.md`) es **no negociable** dentro de este alcance de análisis. Los conflictos con la constitución son automáticamente CRÍTICOS y requieren ajustar la especificación, el plan o las tareas; no diluir, reinterpretar ni ignorar silenciosamente el principio. Si un principio debe cambiar, eso debe ocurrir en una actualización separada y explícita de la constitución fuera de `/speckit.analyze`.

## Pasos de ejecución

### 1. Inicializar el contexto del análisis

Ejecute `.specify/scripts/powershell/check-prerequisites.ps1 -Json -RequireTasks -IncludeTasks` una vez desde la raíz del repositorio y analice JSON para FEATURE_DIR y AVAILABLE_DOCS. Derivar rutas absolutas:

- SPEC = FEATURE_DIR/spec.md
- PLAN = FEATURE_DIR/plan.md
- TASKS = FEATURE_DIR/tasks.md

Abortar con un mensaje de error si falta algún archivo requerido (indicar al usuario que ejecute el comando de requisito previo que falta).
Para comillas simples en argumentos como "I 'm Groot", use la sintaxis de escape: por ejemplo,' I '\' 'm Groot' (o double-quote si es posible: "I 'm Groot").

### 2. Cargar artefactos (divulgación progresiva)

Cargue solo el contexto mínimo necesario de cada artefacto:

**Desde spec.md:**

- Descripción general/contexto
Requisitos funcionales
- Requisitos no funcionales
Historias de usuarios
- Casos límite (si están presentes)

**Desde plan.md:**

- Opciones de arquitectura/pila
- Referencias del modelo de datos
Fases
Limitaciones técnicas

**Desde tasks.md:**

- Task IDs
- Descripciones
- Phase grouping
- Marcadores paralelos [P]
- Rutas de archivo de referencia

**De constitución:**

- Cargar `.specify/memory/constitution.md` para validar principios

### 3. Construye modelos semánticos

Crear representaciones internas (no incluir artefactos sin procesar en la salida):

- **Inventario de requisitos**: Cada requisito funcional + no funcional con una clave estable (derive un slug basado en una frase imperativa; por ejemplo, "El usuario puede cargar archivo" → `user-can-upload-file`)
- ** Historia de usuario/inventario de acciones **: Acciones discretas del usuario con criterios de aceptación
- ** Mapeo de cobertura de tareas **: Asigne cada tarea a uno o más requisitos o historias (inferencia por palabra clave /patrones de referencia explícitos como ID o frases clave)
- ** Conjunto de reglas constitucionales **: Extraer nombres de principios y declaraciones normativas de DEBE/DEBE

### 4. Pasos de detección (Token-Efficient Analysis)

Concéntrese en hallazgos de alta señal. Limite a 50 hallazgos en total; agregue el resto en un resumen de desbordamiento.

#### A. Detección de duplicación

- Identificar requisitos casi duplicados
- Marcar redacción de menor calidad para consolidación

#### B. Detección de ambigüedad

- Marcar adjetivos vagos (rápidos, escalables, seguros, intuitivos, robustos) que carecen de criterios medibles
- Marcar marcadores de posición sin resolver (TODO, TKTK, ???, `<placeholder>`, etc.)

#### C. Subespecificación

- Requisitos con verbos pero sin objeto o resultado medible
- A las historias de usuario les falta la alineación de los criterios de
- Tareas que hacen referencia a archivos o componentes no definidos en la especificación/plan

#### D. Alineación con la constitución

- Cualquier requisito o elemento del plan que entre en conflicto con un principio OBLIGATORIO
- Faltan secciones obligatorias o puertas de calidad de la constitución

#### E. Lagunas de cobertura

- Requisitos con cero tareas asociadas
- Tareas sin requisito/historia mapeada
- Requisitos no funcionales no reflejados en tareas (por ejemplo, rendimiento, seguridad)

#### F. Inconsistencia

- Deriva terminológica (el mismo concepto se nombra de manera diferente en los archivos)
- Entidades de datos referenciadas en el plan pero ausentes en las especificaciones (o viceversa)
- Contradicciones en el orden de las tareas (por ejemplo, tareas de integración antes de las tareas de configuración fundamentales sin nota de dependencia)
- Requisitos conflictivos (por ejemplo, uno requiere Next.js mientras que otro especifica Vue)

### 5. Asignación de gravedad

Utilice esta heurística para priorizar los hallazgos:

- **CRÍTICO**: Viola el DEBER de constitución, falta el artefacto de especificación central o el requisito con cobertura cero que bloquea la funcionalidad de referencia
- **ALTO**: Requisito duplicado o conflictivo, atributo ambiguo de seguridad/rendimiento, criterio de aceptación no comprobable
- **MEDIO**: deriva terminológica, falta cobertura de tareas para requisitos no funcionales, caso límite poco especificado
- **BAJO**: Mejoras de estilo/redacción, redundancia menor que no afecta el orden de ejecución

### 6. Producir un informe de análisis compacto

Salida de un informe de Markdown (sin escritura de archivos) con la siguiente estructura:

## Informe de análisis de especificaciones

| ID | Categoría | Gravedad | Ubicación(es) | Resumen | Recomendación |
|----|----------|----------|-------------|---------|----------------|
| A1 | Duplicación | ALTA | spec.md: L120-134| Dos requisitos similares... | Combinar fraseo; mantener una versión más clara |

(Agregue una fila por hallazgo; genere ID estables con prefijo por inicial de categoría).

** Tabla de resumen de cobertura:**

| Clave de requisito | ¿Tiene tarea? | ID de tarea | Notas |
|-----------------|-----------|----------|-------|

** Problemas de alineación de la Constitución:** (si corresponde)

** Tareas no asignadas:** (si corresponde)

**Métricas:**

Necesidades totales
Tareas totales
- Cobertura % (requisitos con >=1 tarea)
- Recuento de ambigüedad
- Recuento de duplicados
- Recuento de problemas críticos

### 7. Proporciona las siguientes acciones

Al final del informe, genere un bloque conciso de Próximas acciones:

- Si existen problemas CRÍTICOS: recomiende resolverlos antes de `/speckit.implement`
- Si solo es BAJO/MEDIO: el usuario puede continuar, pero proporcionar sugerencias de mejora
- Proporcionar sugerencias de comandos explícitas: por ejemplo, "Ejecutar /speckit.specify con refinamiento", "Ejecutar /speckit.plan para ajustar la arquitectura", "Editar manualmente tasks.md para agregar cobertura de 'performance-metrics'"

### 8. Ofrecer reparación

Pregúntele al usuario: "¿Le gustaría que sugiriera ediciones de remediación concretas para los principales problemas N?" (NO los aplique automáticamente.)

OPERATING PRINCIPLES

### Eficiencia del contexto

- ** Tokens mínimos altos-signal *: Céntrate en los hallazgos procesables, no en la documentación exhaustiva
- ** Divulgación progresiva **: Cargue artefactos de forma incremental; no vierta todo el contenido en el análisis
- **Token-efficient output**: Limitar la tabla de resultados a 50 filas; resumir el desbordamiento
- ** Resultados deterministas **: la repetición sin cambios debe producir identificaciones y recuentos consistentes

### Pautas de análisis

- **NUNCA modifique archivos** (este análisis es de solo lectura)
- **NUNCA alucine con las secciones faltantes ** (si está ausente, repórtelas con precisión)
- **Priorizar las violaciones de la constitución ** (estas siempre son CRÍTICAS)
- **Usar ejemplos sobre reglas exhaustivas ** (citar instancias específicas, no patrones genéricos)
- **Informe cero problemas con gracia** (emita un informe de éxito con estadísticas de cobertura)

## Context

$ARGUMENTS


