---
description: Genere una lista de verificación personalizada para la función actual en función de los requisitos del usuario.
---

## Propósito de la lista de verificación: "Pruebas unitarias de inglés"

**CONCEPTO CRÍTICO**: Las listas de verificación son ** PRUEBAS UNITARIAS PARA LA REDACCIÓN DE REQUISITOS **: validan la calidad, claridad e integridad de los requisitos en un dominio determinado.

**NO para verificación/prueba**:

- ❌ NO "Verifique que el botón haga clic correctamente"
- ❌ NO "Trabajos de manejo de errores de prueba"
- ❌ NO "Confirmar que la API devuelve 200"
- ❌ NO verificar si el código/implementación coincide con la especificación

**PARA validar la calidad de requisitos**:

- ✅ "¿Están definidos los requisitos de jerarquía visual para todos los tipos de tarjetas?" (exhaustividad)
- ✅ "¿Se cuantifica la 'pantalla prominente' con un tamaño/posicionamiento específico?" (claridad)
- ✅ "¿Los requisitos del estado flotante son consistentes en todos los elementos interactivos?" (coherencia)
- ✅ "¿Se definen los requisitos de accesibilidad para la navegación por teclado?" (cobertura)
- ✅ "¿La especificación define lo que sucede cuando la imagen del logotipo no se carga?" (casos de borde)

**Metáfora**: Si su especificación es código escrito en inglés, la lista de verificación es su suite de pruebas unitarias. Está validando si los requisitos están bien redactados, completos, sin ambigüedades y listos para implementar; NO si la implementación funciona.

## Entrada del usuario

```text
$ARGUMENTS
```

Usted **DEBE** considerar la entrada del usuario antes de continuar (si no está vacía).

## Pasos de ejecución

1. **Configuración**: Ejecute `.specify/scripts/powershell/check-prerequisites.ps1 -Json` desde la raíz del repositorio y analice el JSON para FEATURE_DIR y la lista AVAILABLE_DOCS.
- Todas las rutas de archivo deben ser absolutas.
- Para comillas simples en argumentos como "I 'm Groot", use la sintaxis de escape: por ejemplo,' I '\' 'm Groot' (o doble-quote si es posible: "I 'm Groot").

2. **Aclarar la intención (dinámica)**: Derivar hasta TRES preguntas iniciales de aclaración contextual (sin catálogo predefinido). DEBEN:
- Se generará a partir del fraseo del usuario + señales extraídas de las especificaciones/plan/tareas
- Preguntar solo sobre información que cambie materialmente el contenido de la lista de verificación.
- Omitir individualmente si ya es inequívoco en `$ARGUMENTS`
- Preferir precisión sobre amplitud

Algoritmo de generación:
1. Señales de extracción: palabras clave de dominio de funciones (por ejemplo, auth, latency, UX, API), indicadores de riesgo ("critical", "must", "compliance"), sugerencias de las partes interesadas ("QA", "review", "security team") y entregables explícitos ("a11y", "rollback", "contracts").
2. Señales agrupadas en áreas de enfoque de candidatos (máx. 4) clasificadas por relevancia.
3. Identifique la audiencia probable y el momento (autor, revisor, control de calidad, lanzamiento) si no es explícito.
4. Detectar las dimensiones que faltan: amplitud de alcance, profundidad/rigor, énfasis en el riesgo, límites de exclusión, criterios de aceptación medibles.
5. Formule preguntas elegidas de estos arquetipos:
- Refinamiento del alcance (por ejemplo, "¿Debería esto incluir puntos de contacto de integración con X e Y o limitarse a la corrección del módulo local?")
- Priorización de riesgos (por ejemplo, "¿Cuál de estas áreas de riesgo potencial debería recibir controles de acceso obligatorios?")
- Calibración de profundidad (por ejemplo, "¿Es esta una lista ligera de sanidad pre-commit o una compuerta formal de liberación?")
- Encuadre de la audiencia (por ejemplo, "¿Lo usará solo el autor o sus compañeros durante la revisión de relaciones públicas?")
- Exclusión de límites (por ejemplo, "¿Deberíamos excluir explícitamente los elementos de ajuste de rendimiento esta ronda?")
- Brecha de clase de escenario (por ejemplo, "No se detectan flujos de recuperación: ¿hay rutas de reversión /falla parcial en el alcance?")

Reglas de formato de preguntas
- Si presenta opciones, genere una tabla compacta con columnas: Opción | Candidato | Por qué es importante
- Limitar a un máximo de opciones A–E; omitir tabla si una respuesta formato libre es más clara
- Nunca le pidas al usuario que repita lo que ya dijo
- Evitar categorías especulativas (sin alucinaciones). Si no está seguro, pregunte explícitamente: "Confirme si X pertenece al alcance".

Predeterminado cuando la interacción es imposible:
- Profundidad: Estándar
- Audiencia: Revisor (PR) si está relacionado con código; autor en caso contrario
- Enfoque: los 2 principales grupos de relevancia

Envíe las preguntas (etiqueta Q1/Q2/Q3). Después de las respuestas: si ≥2 clases de escenarios (Alternativo / Excepción / Recuperación / dominio no funcional) siguen sin estar claras, PUEDE hacer hasta DOS seguimientos específicos (Q4/Q5) con una justificación de una línea cada uno (por ejemplo, "Riesgo de ruta de recuperación no resuelto"). No exceda cinco preguntas en total. Omita la escalación si el usuario rechaza explícitamente más.

3. **Comprender la solicitud del usuario **: Combinar `$ARGUMENTS` + respuestas aclaratorias:
- Derivar el tema de la lista de verificación (por ejemplo, seguridad, revisión, implementación, ux)
- Consolidar los elementos explícitos must-have mencionados por el usuario
- Asignar selecciones de enfoque a andamios de categoría
- Inferir cualquier contexto faltante de las especificaciones/planes/tareas (NO alucinar)

4. **Estrategia de carga del contexto**: Leer de FEATURE_DIR:
- spec.md: Requisitos y alcance de las funciones
- plan.md (si existe): Detalles técnicos, dependencias
- tasks.md (si existe): Tareas de implementación

** Estrategia de carga de contexto **:
- Cargue solo las partes necesarias para las áreas de enfoque activas (evite volcar el archivo completo)
- Prefiero resumir las secciones largas en viñetas concisas de escenarios/requisitos
- Use divulgación progresiva: agregue recuperación adicional solo si detecta brechas
- Si los documentos de origen son grandes, genere elementos de resumen provisionales en lugar de incrustar texto sin formato

5. **Generar lista de verificación** - Crear "Pruebas unitarias para requisitos":
- Crear directorio `FEATURE_DIR/checklists/` si no existe
- Generar un nombre de archivo de lista de verificación único:
- Usa un nombre corto y descriptivo basado en el dominio (por ejemplo, `security.md`, `security.md`, `security.md`).
- Formato: `[domain].md`
- Si el archivo existe, añádalo al archivo existente
- Numere los artículos secuencialmente a partir de CHK001
- Cada ejecución de `/speckit.checklist` crea un archivo NUEVO (nunca sobrescribe listas de verificación existentes)

**PRINCIPIO BÁSICO: probar requisitos, no implementación**:
Cada elemento de la lista de verificación DEBE evaluar los REQUISITOS POR SÍ MISMOS para:
- **Integridad**: ¿Están presentes todos los requisitos necesarios?
- **Claridad**: ¿Los requisitos son inequívocos y específicos?
- **Consistencia**: ¿Los requisitos se alinean entre sí?
- **Capacidad de medición**: ¿Se pueden verificar objetivamente los requisitos?
- **Cobertura**: ¿Se abordan todos los escenarios/casos extremos?

** Estructura de la categoría ** - Agrupar los elementos por dimensiones de calidad de los requisitos:
- ** Completitud de los requisitos ** (¿Están documentados todos los requisitos necesarios?)
- ** Claridad de requisitos ** (¿Los requisitos son específicos e inequívocos?)
- ** Consistencia de requisitos ** (¿Los requisitos se alinean sin conflictos?)
- ** Calidad de los criterios de aceptación ** (¿Son medibles los criterios de éxito?)
- ** Cobertura de escenarios ** (¿Se abordan todos los flujos/casos?)
- ** Cobertura del caso Edge ** (¿Se definen las condiciones de contorno?)
- **Requisitos no funcionales** (rendimiento, seguridad, accesibilidad, etc.; ¿están especificados?)
- **Dependencias y supuestos** (¿Están documentados y validados?)
- **Ambigüedades y conflictos** (¿Qué necesita aclaración?)

**CÓMO ESCRIBIR ELEMENTOS DE LA lista DE VERIFICACIÓN - "Pruebas de unidad para inglés"**:

❌ **INCORRECTO** (Implementación de pruebas):
- "Verificar que la página de destino muestre 3 tarjetas de episodios"
- "Los estados de prueba de desplazamiento funcionan en el escritorio"
- "Confirmar que el clic del logotipo navega por el inicio"

✅ **CORRECTO** (Calidad de los requisitos de prueba):
- "¿Se especifica el número exacto y el diseño de los episodios destacados?" [Integridad]
- "¿Se cuantifica la 'pantalla prominente' con un tamaño/posicionamiento específico?" [Claridad]
- "¿Los requisitos del estado flotante son consistentes en todos los elementos interactivos?" [Consistencia]
- "¿Están definidos los requisitos de navegación del teclado para todas las interfaces de usuario interactivas?" [Cobertura]
- "¿Se especifica el comportamiento alternativo cuando la imagen del logotipo no se carga?" [Edge Cases]
- "¿Se definen los estados de carga para los datos de episodios asíncronos?" [Integridad]
- "¿La especificación define la jerarquía visual para los elementos de la interfaz de usuario de la competencia?" [Claridad]

=item estructura
Cada artículo debe seguir este patrón:
- Formato de pregunta preguntando sobre la calidad de los requisitos
- Centrarse en lo que está ESCRITO (o no escrito) en la especificación/plan
- Incluir la dimensión de calidad entre paréntesis [Integridad/Claridad/Consistencia/etc.]
- Consulte la sección de especificaciones `[Spec §X.Y]` al verificar los requisitos existentes
- Utilice el marcador `[Gap]` cuando compruebe requisitos faltantes

**EJEMPLOS POR DIMENSIÓN DE CALIDAD **:

Integridad:
- "¿Están definidos los requisitos de manejo de errores para todos los modos de falla de la API? [Brecha]"
- "¿Se especifican los requisitos de accesibilidad para todos los elementos interactivos? [Integridad]"
- "¿Se definen los requisitos de puntos de interrupción móviles para diseños receptivos? [Brecha]"

Claridad:
- "¿Se cuantifica la 'carga rápida' con umbrales de tiempo específicos? [Claridad, Especificación §NFR-2]"
- "¿Se definen explícitamente los criterios de selección de 'episodios relacionados'? [Claridad, Especificación §FR-5]"
   - "Is 'prominent' defined with measurable visual properties? [Ambiguity, Spec §FR-4]"

   Consistency:
   - "Do navigation requirements align across all pages? [Consistency, Spec §FR-10]"
   - "Are card component requirements consistent between landing and detail pages? [Consistency]"

   Coverage:
   - "Are requirements defined for zero-state scenarios (no episodes)? [Coverage, Edge Case]"
   - "Are concurrent user interaction scenarios addressed? [Coverage, Gap]"
   - "Are requirements specified for partial data loading failures? [Coverage, Exception Flow]"

   Measurability:
   - "Are visual hierarchy requirements measurable/testable? [Acceptance Criteria, Spec §FR-1]"
   - "Can 'balanced visual weight' be objectively verified? [Measurability, Spec §FR-2]"

   **Scenario Classification & Coverage** (Requirements Quality Focus):
   - Check if requirements exist for: Primary, Alternate, Exception/Error, Recovery, Non-Functional scenarios
   - For each scenario class, ask: "Are [scenario type] requirements complete, clear, and consistent?"
   - If scenario class missing: "Are [scenario type] requirements intentionally excluded or missing? [Gap]"
   - Include resilience/rollback when state mutation occurs: "Are rollback requirements defined for migration failures? [Gap]"

   **Traceability Requirements**:
   - MINIMUM: ≥80% of items MUST include at least one traceability reference
   - Each item should reference: spec section `[Spec §X.Y]`, or use markers: `[Gap]`, `[Ambiguity]`, `[Conflict]`, `[Assumption]`
   - If no ID system exists: "Is a requirement & acceptance criteria ID scheme established? [Traceability]"

   **Surface & Resolve Issues** (Requirements Quality Problems):
   Ask questions about the requirements themselves:
   - Ambiguities: "Is the term 'fast' quantified with specific metrics? [Ambiguity, Spec §NFR-1]"
   - Conflicts: "Do navigation requirements conflict between §FR-10 and §FR-10a? [Conflict]"
   - Assumptions: "Is the assumption of 'always available podcast API' validated? [Assumption]"
   - Dependencies: "Are external podcast API requirements documented? [Dependency, Gap]"
   - Missing definitions: "Is 'visual hierarchy' defined with measurable criteria? [Gap]"

   **Content Consolidation**:
   - Soft cap: If raw candidate items > 40, prioritize by risk/impact
   - Merge near-duplicates checking the same requirement aspect
   - If >5 low-impact edge cases, create one item: "Are edge cases X, Y, Z addressed in requirements? [Coverage]"

   **🚫 ABSOLUTELY PROHIBITED** - These make it an implementation test, not a requirements test:
   - ❌ Any item starting with "Verify", "Test", "Confirm", "Check" + implementation behavior
   - ❌ References to code execution, user actions, system behavior
   - ❌ "Displays correctly", "works properly", "functions as expected"
   - ❌ "Click", "navigate", "render", "load", "execute"
   - ❌ Test cases, test plans, QA procedures
   - ❌ Implementation details (frameworks, APIs, algorithms)

   **✅ REQUIRED PATTERNS** - These test requirements quality:
   - ✅ "Are [requirement type] defined/specified/documented for [scenario]?"
   - ✅ "Is [vague term] quantified/clarified with specific criteria?"
   - ✅ "Are requirements consistent between [section A] and [section B]?"
   - ✅ "Can [requirement] be objectively measured/verified?"
   - ✅ "Are [edge cases/scenarios] addressed in requirements?"
   - ✅ "Does the spec define [missing aspect]?"

6. **Structure Reference**: Generate the checklist following the canonical template in `.specify/templates/checklist-template.md` for title, meta section, category headings, and ID formatting. If template is unavailable, use: H1 title, purpose/created meta lines, `##` category sections containing `- [ ] CHK### <requirement item>` lines with globally incrementing IDs starting at CHK001.

7. **Report**: Output full path to created checklist, item count, and remind user that each run creates a new file. Summarize:
   - Focus areas selected
   - Depth level
   - Actor/timing
   - Any explicit user-specified must-have items incorporated

**Important**: Each `/speckit.checklist` command invocation creates a checklist file using short, descriptive names unless file already exists. This allows:

- Multiple checklists of different types (e.g., `ux.md`, `test.md`, `security.md`)
- Simple, memorable filenames that indicate checklist purpose
- Easy identification and navigation in the `checklists/` folder

To avoid clutter, use descriptive types and clean up obsolete checklists when done.

## Example Checklist Types & Sample Items

**UX Requirements Quality:** `ux.md`

Sample items (testing the requirements, NOT the implementation):

- "Are visual hierarchy requirements defined with measurable criteria? [Clarity, Spec §FR-1]"
- "Is the number and positioning of UI elements explicitly specified? [Completeness, Spec §FR-1]"
- "Are interaction state requirements (hover, focus, active) consistently defined? [Consistency]"
- "Are accessibility requirements specified for all interactive elements? [Coverage, Gap]"
- "Is fallback behavior defined when images fail to load? [Edge Case, Gap]"
- "Can 'prominent display' be objectively measured? [Measurability, Spec §FR-4]"

**API Requirements Quality:** `api.md`

Sample items:

- "Are error response formats specified for all failure scenarios? [Completeness]"
- "Are rate limiting requirements quantified with specific thresholds? [Clarity]"
- "Are authentication requirements consistent across all endpoints? [Consistency]"
- "Are retry/timeout requirements defined for external dependencies? [Coverage, Gap]"
- "Is versioning strategy documented in requirements? [Gap]"

**Performance Requirements Quality:** `performance.md`

Sample items:

- "Are performance requirements quantified with specific metrics? [Clarity]"
- "Are performance targets defined for all critical user journeys? [Coverage]"
- "Are performance requirements under different load conditions specified? [Completeness]"
- "Can performance requirements be objectively measured? [Measurability]"
- "Are degradation requirements defined for high-load scenarios? [Edge Case, Gap]"

**Security Requirements Quality:** `security.md`

Sample items:

- "Are authentication requirements specified for all protected resources? [Coverage]"
- "Are data protection requirements defined for sensitive information? [Completeness]"
- "Is the threat model documented and requirements aligned to it? [Traceability]"
- "Are security requirements consistent with compliance obligations? [Consistency]"
- "Are security failure/breach response requirements defined? [Gap, Exception Flow]"

## Anti-Examples: What NOT To Do

**❌ WRONG - These test implementation, not requirements:**

```markdown
- [ ] CHK001 - Verify landing page displays 3 episode cards [Spec §FR-001]
- [ ] CHK002 - Test hover states work correctly on desktop [Spec §FR-003]
- [ ] CHK003 - Confirm logo click navigates to home page [Spec §FR-010]
- [ ] CHK004 - Check that related episodes section shows 3-5 items [Spec §FR-005]
```

**✅ CORRECT - These test requirements quality:**

```markdown
- [ ] CHK001 - Are the number and layout of featured episodes explicitly specified? [Completeness, Spec §FR-001]
- [ ] CHK002 - Are hover state requirements consistently defined for all interactive elements? [Consistency, Spec §FR-003]
- [ ] CHK003 - Are navigation requirements clear for all clickable brand elements? [Clarity, Spec §FR-010]
- [ ] CHK004 - Is the selection criteria for related episodes documented? [Gap, Spec §FR-005]
- [ ] CHK005 - Are loading state requirements defined for asynchronous episode data? [Gap]
- [ ] CHK006 - Can "visual hierarchy" requirements be objectively measured? [Measurability, Spec §FR-001]
```

**Key Differences:**

- Wrong: Tests if the system works correctly
- Correct: Tests if the requirements are written correctly
- Wrong: Verification of behavior
- Correct: Validation of requirement quality
- Wrong: "Does it do X?"
- Correct: "Is X clearly specified?"


