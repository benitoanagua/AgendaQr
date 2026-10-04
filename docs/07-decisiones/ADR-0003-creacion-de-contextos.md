# ADR-0003 — Creación y edición de Contextos

**Estado:** PROPUESTA — requiere aprobación  
**Fecha:** 2026-10-04  
**Área:** UX/UI V1 (S06, S08) / Dominio Context  
**Decisor requerido:** humano (propietario de producto/UX)

## Contexto

`SaveContextUseCase` y `UpdateContextUseCase` existen en el dominio y la
persistencia local-first + Supabase de `Context` está completa y probada,
pero **ninguna UI los invoca**: no hay forma de crear un contexto en toda
la app. En consecuencia S06 (Contexto), el selector S08 y los resultados
de búsqueda de tipo Contexto están operativamente muertos (solo legibles
tras seed manual por SQL).

La especificación congelada (`docs/04-ux/02-especificacion-ux-ui-v1.md`)
define S06 y S08 pero **no define un flujo de creación/edición**. La regla
del repo es no inventar UX: esta página propone el mínimo compatible y
**no se implementa nada hasta aprobación explícita**.

## Contrato que cualquier opción debe respetar

- §3: "Back conserva el trabajo del flujo padre" y "Cancelar abandona la
  intención actual".
- §4 S08: "elegir contexto para el flujo padre. Al terminar, vuelve al
  flujo original conservando el draft".
- §8: "El draft sobrevive al selector de contexto. Una operación mínima
  puede existir sin contexto."
- §1: "Una pantalla = una intención principal + una acción principal".
- §3: "Los estados técnicos no crean navegación artificial" (la creación
  no puede ser una pantalla-tipo propia con estados visibles).

## Opciones

### Opción A — Crear desde S08 sin salir del draft

El selector de contexto (S08) ofrece, debajo de la lista existente, la
acción secundaria **"Crear contexto"**. Al tocarla, el mismo diálogo pasa
a un paso de una sola pregunta (S11-style): campo **Nombre** (obligatorio)
y opcionalmente **Nota**; acciones **[ Crear ]** y **[ Cancelar ]**. Al
crear: el contexto se persiste vía `SaveContextUseCase`, queda
seleccionado y el selector vuelve a la lista con el nuevo elemento
marcado; el draft del flujo padre nunca sale de composición.

- **Impacto en S08:** gana una acción secundaria y un paso interno; la
  intención principal ("elegir contexto") no cambia. Con la lista vacía,
  "Crear contexto" es la única acción disponible (acción primaria del
  estado vacío).
- **Impacto en S06:** ninguno (S06 solo lee).
- **Coste de implementación:** bajo (reutiliza el diálogo/selector
  compartido `ContextPickerDialog` de T4 y el caso de uso existente).
- **Tests propuestos:**
  1. Crear desde el editor de operación (Registrar): al terminar, la
     operación conserva todos los campos del draft y `contextId` = nuevo.
  2. Crear desde el editor de destino: ídem con el draft de destino.
  3. Cancelar el paso de creación no persiste nada y restaura la lista
     del selector.
  4. Nombre vacío no habilita [ Crear ] (validación mínima).
  5. Persistencia: el contexto aparece en S06 y en búsqueda global tras
     crear; sobrevive kill/restart (cola de sync encola UPSERT).

### Opción B — Acción primaria en S06 cuando está vacío

S06 (Lista de contextos) en su estado vacío muestra hoy "Sin contextos"
con un "Volver". La propuesta: su acción primaria pasa a ser
**"Crear contexto"** con el mismo formulario mínimo (Nombre/Nota,
[ Crear ] / [ Cancelar ]) en el mismo diálogo.

- **Impacto en S06:** el estado vacío gana acción primaria (hoy ninguna);
  S06 con contextos existentes NO gana creación (evita jerarquía nueva).
- **Impacto en S08:** ninguno; PERO el usuario que llega al selector con
  lista vacía no puede crear sin salir del flujo padre. Con el back stack
  de T2, navegar a S06 y volver conserva el draft (la superficie padre
  sigue compuesta), aunque el camino es más largo: S08 → cancelar →
  Inicio → Contextos → crear → volver → volver → S08 → elegir.
- **Coste:** bajo-medio (nueva superficie de formulario + navegación).
- **Tests propuestos:**
  1. S06 vacío muestra "Crear contexto" como primaria; con ≥1 contexto,
     la acción desaparece (no se inventa jerarquía en S06 lleno).
  2. Crear → persistido (S06 lo lista) → UPSERT en cola de sync.
  3. Round-trip draft: abrir S06 desde el flujo padre, crear, volver con
     Back del sistema y verificar el draft intacto.

### Opción C — A + B combinadas (recomendada por coherencia)

"Crear contexto" disponible en S08 (siempre, acción secundaria; primaria
si vacío) y en S06 vacío (primaria). Un único componente de formulario
compartido; S06 lleno no gana acciones.

- **Impacto:** cubre el caso "quiero crear sin salir de mi flujo" (S08) y
  el caso "exploro contextos y no hay ninguno" (S06) con el mínimo modelo
  mental: una sola pregunta (nombre) en ambos.
- **Coste:** medio (dos entradas, un formulario).
- **Tests propuestos:** unión de A.1–A.5 y B.1–B.3.

## Edición de contexto (sub-decisión)

Ninguna opción anterior define edición. Propuesta mínima: **S06 Detalle**
expone una acción secundaria "Editar" que reutiliza el mismo formulario
(Nombre/Nota) vía `UpdateContextUseCase`, con confirmación implícita
[ Guardar ] / [ Cancelar ]. El rename se propaga por `contextId` (las
relaciones ya son reversibles y por referencia, sin duplicar datos).
Alternativa más conservadora: dejar edición FUERA de V1 y solo aprobar
creación. **Se requiere decisión explícita también aquí.**

## Recomendación

**Opción C** (con o sin edición de V1): es la que menos superficie nueva
introduce por caso de uso cubierto y la única que respeta literalmente
"Al terminar, vuelve al flujo original conservando el draft" para el caso
más frecuente (crear mientras se registra/etiqueta).

## Consecuencias si NO se aprueba nada

- El gap queda registrado: S06/S08/búsqueda por Contexto son solo
  lectura; `SaveContextUseCase`/`UpdateContextUseCase` siguen sin cablear.
- Ningún flujo congelado se altera (estado actual).
