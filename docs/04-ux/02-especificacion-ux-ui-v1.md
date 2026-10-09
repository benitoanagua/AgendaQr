# Agenda QR — Especificación UX/UI V1 congelada

**Estado:** FROZEN — V1.1 para contrato funcional; contrato visual desarrollado por ADR-0010 y `docs/05-design-system/00-xauxa-contrato-normativo.md`  
**Fecha:** 2026-10-05  
**Alcance:** contrato UX/UI conceptual; no es implementación Compose.

## Historial de enmiendas

| Versión | Fecha | Alcance |
|---|---|---|
| V1.0 | 2026-09-23 | Congelación original. |
| V1.1 | 2026-10-05 | Enmienda por ADR-0005 (lenguaje visual Metro dentro de Xauxa): S01, S06, §11 y §12 reescritos. El resto de secciones (§§1–10, S02–S05, S07–S12) queda sin cambios. Intención, navegación conceptual, modelo mental, reglas de negocio y recuperación de errores: intactos. |

La implementación vigente en `main` queda **desalineada** de V1.1 hasta la pasada de implementación que aplique esta enmienda (registrado en `docs/09-implementacion/01-estado.md`); la spec manda sobre el código (regla de D-17).

## 1. Contrato principal

> Una pantalla = una intención principal + una acción principal.

La pantalla puede contener información, campos y acciones secundarias necesarias. La regla define jerarquía de intención, no número de controles.

La UX/UI V1 queda congelada. Un cambio posterior que altere intención, navegación, modelo mental, jerarquía primaria o recuperación requiere una decisión UX explícita.

## 2. Modelo mental

Agenda QR se organiza alrededor del trabajo humano:

    CONTEXTO
    ├── QR
    ├── ACTIVIDADES
    └── COMPROBANTES

Contexto es una superficie de continuidad, no una nueva taxonomía de navegación.

## 3. Reglas congeladas

- Buscar es global: contexto, QR, actividad y comprobante.
- Buscar no exige elegir primero el tipo de resultado.
- Galería es la entrada principal para imágenes que ya existen.
- Desde otra app es una entrada de primer nivel.
- Cámara es un medio de captura, no el modelo mental principal.
- El sistema clasifica automáticamente cuando tiene confianza suficiente.
- Una importación válida no queda bloqueada por otra inválida.
- Un comprobante puede existir sin QR y sin actividad asociada.
- Una actividad puede existir sin QR.
- Una operación puede registrarse sin contexto.
- Las asociaciones son reversibles.
- Back conserva el trabajo del flujo padre.
- Cancelar abandona la intención actual.
- Guardar repetidamente no crea duplicados.
- Guardado y Sincronizado son estados distintos.
- Offline es transversal, no una pantalla.
- Los errores son recuperables y contextuales.
- La ambigüedad real genera una sola pregunta clara.
- Los estados técnicos no crean navegación artificial.

## 4. Pantallas

### S01 — Inicio (V1.1, ADR-0005)
Intención: encontrar o iniciar algo. Buscar domina visualmente; Añadir y Registrar son acciones secundarias.

Composición (la intención no cambia; solo cambia la presentación):

    [título de página: Agenda QR — display ligero]
    [barra de búsqueda — primer bloque, dominante]
    [rejilla de tiles]
      Añadir · Registrar · Favoritos · Contextos
      [tile ancho vivo: último QR o actividad reciente]
    [sección "recientes": filas abiertas SIN borde]

- Tiles iniciales: Añadir, Registrar, Favoritos, Contextos y un tile ancho vivo (último QR o actividad reciente). Ninguna capacidad actual se elimina; solo cambia su presentación.
- La acción principal de la pantalla y las secundarias viven en la barra de aplicación inferior (§12, Navegación y chrome).
- El tile vivo cambia su contenido SOLO cuando cambia el dato; no hay rotación en bucle (§12, Movimiento).

### S02 — Añadir
Intención: traer algo que el usuario ya tiene.

    GALERÍA
    DESDE OTRA APP
    CÁMARA

Jerarquía: Galería → Desde otra app → Cámara.

### S03 — Cámara
Intención: capturar un QR físico. La detección debe ser automática cuando sea posible. No se muestran métricas técnicas del scanner.

### S04 — Buscar
Intención: encontrar cualquier cosa de Agenda QR. Los resultados pueden ser heterogéneos y muestran su tipo mediante texto/semántica.

### S05 — Resultados
Superficie de decisión. Al volver se conserva la consulta.

### S06 — Contexto (V1.1, ADR-0005)
Intención: comprender y continuar dentro de un contexto. No es un dashboard financiero. La acción primaria depende del flujo que llevó al usuario allí.

Composición: la superficie del contexto se estructura como **pivot de secciones QR · Actividades · Comprobantes**, reflejando directamente el modelo mental de §2; no crea taxonomía nueva de navegación. El acento del contexto (§12, Color) tiñe sus encabezados de sección y elementos de foco. Volver/guardar y acciones de la pantalla viven en la barra de aplicación inferior (§12, Navegación y chrome).

### S07 — Registrar
Intención: registrar una actividad. El contexto ayuda pero no bloquea el registro.

### S08 — Seleccionar contexto
Intención: elegir contexto para el flujo padre. Al terminar, vuelve al flujo original conservando el draft.

### S09 — Revisar QR
Intención: verificar lo que Agenda QR entendió antes de guardar. Debe mostrar evidencia suficiente sin exponer payload técnico innecesario.

### S10 — Revisar comprobante
Intención: confirmar y, cuando corresponda, asociar.

    ASOCIAR
    Guardar sin asociar

Ambos son resultados legítimos.

### S11 — Resolver ambigüedad
Intención: resolver una única duda que el sistema no puede decidir con seguridad.

### S12 — Resultado de importación masiva
Superficie de decisión derivada de Añadir.

    ✓ reconocidos
    ! duplicados
    ? pendientes
    [ GUARDAR RECONOCIDOS ]
    Revisar N pendientes

Los válidos no dependen de los pendientes.

## 5. Estados transversales

Estos son estados, no destinos de navegación:

    IMPORTANDO
    ANALIZANDO
    GUARDANDO
    GUARDADO
    PENDIENTE
    SINCRONIZANDO
    SINCRONIZADO
    ERROR RECUPERABLE
    DUPLICADO
    ASOCIADO
    SIN ASOCIAR

Contrato local-first:

    GUARDADO → PENDIENTE → SINCRONIZANDO → SINCRONIZADO

## 6. Importación

Galería y Desde otra app convergen:

    IMPORTAR → ANALIZAR → CLASIFICAR → REVISAR → GUARDAR

Clasificación conceptual: QR, COMPROBANTE, DESCONOCIDO, POSIBLE DUPLICADO.

Importando y Analizando son estados; no requieren un botón Continuar.

## 7. Búsqueda

Puede devolver Contexto, QR, Actividad o Comprobante. No hay selección previa de tipo.

## 8. Registro

El draft sobrevive al selector de contexto.

    Registrar
      ↓
    Seleccionar contexto
      ↓
    Volver a Registrar

Una operación mínima puede existir sin contexto.

## 9. Comprobantes

    COMPROBANTE
    ├── asociado
    └── sin asociar

Un comprobante puede guardarse antes de conocer la actividad y asociarse después.

## 10. Errores

Todo error debe responder: qué ocurrió, qué pasó con los datos y qué puede hacer el usuario.

Ejemplos:

    No pudimos leer este QR.
    [ ELEGIR OTRA IMAGEN ]

    No pudimos sincronizar.
    Tu información está guardada.
    [ REINTENTAR ]

No existe un ErrorScreen global como destino de navegación.

## 11. Accesibilidad y movimiento (V1.1, ADR-0005)

- targets interactivos de 44–48dp como mínimo según plataforma;
- significado independiente del color;
- estados expresados también mediante texto/semántica;
- tipografía adaptable;
- reduced motion respetado;
- comprensión independiente de la animación.

Reglas adicionales V1.1:

- **Contraste:** cada acento de la paleta Metro usa el color de texto (blanco o negro) cuya razón WCAG cumpla texto normal ≥ 4.5:1 y texto grande ≥ 3:1 (tabla calculada en §12, Color). La misma regla aplica en modo oscuro. Los colores de estado (error, advertencia, éxito) son semánticos, distintos de los acentos, y el significado nunca depende solo del color.
- **Tiles accesibles:** cada tile expone rol de botón, etiqueta de texto visible, estado y orden de foco lógico. El foco por teclado/TalkBack es visible sobre el color del tile en ambos temas.
- **Etiqueta visible en iconos:** TODO icono interactivo va acompañado de etiqueta de texto visible, también en la barra de aplicación inferior. Es una **decisión de accesibilidad** (el significado no depende solo de un símbolo) y una **desviación consciente de Metro**, cuya barra solo mostraba las etiquetas al expandirse. Los iconos sin acción (decorativos) se marcan como no accesibles para lectores de pantalla.
- **Errores de campo:** texto de error contextual debajo del campo, con icono + texto (no solo color).
- **Reduced motion:** sin tilt, sin entrada escalonada, sin flip de live tile; solo cambio de estado inmediato (transición de duración 0). La comprensión no depende de la animación.
- **Sin bucles de animación:** los loops decorativos no forman parte del lenguaje (invariante Xauxa 6); el live tile realiza una única transición por cambio de dato.

## 12. Contrato visual Xauxa (expresión Metro) — V1.1, ADR-0005

> **Precedencia:** esta sección conserva la decisión de producto visual adoptada por ADR-0005. Para reglas transversales detalladas, estados, accesibilidad, pruebas, gobernanza y resolución de contradicciones, prevalece `docs/05-design-system/00-xauxa-contrato-normativo.md` (ADR-0010). Si un detalle de esta sección contradice el contrato normativo, se debe reconciliar documentalmente; no se permite implementar una mezcla tácita.

Metro es referencia de lenguaje, no de assets: prohibido usar Segoe UI, Segoe MDL2 u otros assets/fuentes de Microsoft con licencia restringida; solo fuentes e iconos con licencia libre, registrados en `docs/05-design-system/06-licencias-terceros.md`. Xauxa sigue siendo el único sistema de diseño; Metro se expresa como enmienda de Xauxa.

Se conservan los invariantes Xauxa: radio 0 en contenedores rectangulares (los círculos reales siguen siendo círculos), sin sombras/elevación decorativa, composición full-bleed, foco visible, targets 44–48 dp, tokens semánticos (no valores crudos en features).

### Principios (M1)

1. Contenido antes que cromo.
2. La tipografía es la interfaz.
3. Los tiles son la superficie de entrada.
4. El movimiento es identidad (con propósito).

Reglas base que se mantienen: composición antes de crear componentes; lenguaje visual del scanner Xauxa para cámara, sin métricas técnicas; los restos históricos rounded/shadow de Xauxa no sustituyen el Design System normativo.

### Rejilla y tiles (M3)

- Unidad base 4 dp (la de Xauxa). Margen de pantalla y separación entre tiles definidas como tokens en múltiplos de 4 en `XauxaTokens.kt`.
- Rejilla de 4 columnas en compacto; tamaños de tile: pequeño (1×1), mediano (2×2) y ancho (4×2).
- Cada tile = un solo bloque de color plano, con el **icono centrado** y la **etiqueta de texto siempre visible, abajo a la izquierda**. Sin borde, sin sombra.
- El tile ancho y el tile vivo muestran su contenido (último QR o actividad reciente) en texto; el icono se reduce o se omite, pero la etiqueta se mantiene siempre visible.

### Color (M4/M5)

- Acento de sistema por defecto: `0067B8` (texto blanco, 5.78:1).
- Paleta Metro de acentos asignables a contextos: el color se deriva de forma **determinista** del identificador del contexto, sin persistir el color ni cambiar el dominio. Elegir color manualmente queda como punto abierto (ADR-0005), coherente con ADR-0003 (sin edición de contextos en V1).
- Los colores de estado (error, advertencia, éxito) siguen siendo semánticos y distintos de los acentos; el significado nunca depende solo del color.

Tabla de contraste calculada (script Python sin dependencias, WCAG 2.x sobre luminancia relativa; razón contra texto blanco y contra texto negro). Regla: texto normal ≥ 4.5:1; texto grande ≥ 3:1. Cada acento usa el color de texto que cumple; si ninguno cumple, se excluye.

| Acento | Hex | Texto blanco | Texto negro | Texto asignado | Estado |
|---|---|---|---|---|---|
| lime | A4C400 | 2.00 | 10.47 | negro | ADMITIDO |
| green | 60A917 | 2.92 | 7.18 | negro | candidato no incluido (paleta acotada a 12) |
| emerald | 008A00 | 4.53 | 4.64 | negro | ADMITIDO |
| teal | 00ABA9 | 2.84 | 7.40 | negro | ADMITIDO |
| cyan | 1BA1E2 | 2.90 | 7.24 | negro | ADMITIDO |
| cobalt | 0050EF | 6.19 | 3.39 | blanco | ADMITIDO |
| indigo | 6A00FF | 6.87 | 3.06 | blanco | ADMITIDO |
| violet | AA00FF | 5.06 | 4.15 | blanco | ADMITIDO |
| magenta | D80073 | 5.04 | 4.17 | blanco | ADMITIDO |
| crimson | A20025 | 8.19 | 2.56 | blanco | ADMITIDO |
| red | E51400 | 4.74 | 4.43 | blanco | ADMITIDO |
| orange | FA6800 | 2.98 | 7.04 | negro | ADMITIDO |
| amber | F0A30A | 2.11 | 9.95 | negro | ADMITIDO |
| yellow | E3C800 | 1.68 | 12.53 | negro | candidato no incluido (paleta acotada a 12) |
| brown | 825A2C | 6.09 | 3.45 | blanco | candidato no incluido (paleta acotada a 12) |
| steel | 647687 | 4.68 | 4.48 | blanco | candidato no incluido (paleta acotada a 12) |
| sistema | 0067B8 | 5.78 | 3.63 | blanco | acento por defecto |

**Paleta final admitida: 12 acentos futuros de contexto** (lime, emerald, teal, cyan, cobalt, indigo, violet, magenta, crimson, red, orange, amber) + el acento de sistema `0067B8`. Ningún candidato fue excluido por contraste; los 4 no incluidos se descartan solo por acotar la paleta al máximo de 12 y quedan documentados. Nota: emerald con texto negro (4.64) queda cerca del umbral — aceptado y registrado en ADR-0005.

### Tipografía (M6)

- Títulos de página en **display ligero** (Archivo peso 300, ≥ 40 sp), en lugar de negrita. Requiere empaquetar un peso Light de Archivo (OFL 1.1): tarea de implementación registrada en `docs/05-design-system/05-xauxa-tipografia.md` y en `docs/05-design-system/06-licencias-terceros.md`.
- Encabezados de sección pequeños (14–16 sp) en color de acento (acento de sistema o del contexto).
- Cuerpo en Roboto/San Francisco (familia de sistema), sin cambios.
- Capitalización de oración: sin mayúsculas forzadas ni minúsculas forzadas.
- El escalado de fuente del sistema se mantiene (§11).

### Navegación y chrome (M7)

- **Barra de aplicación inferior** en cada pantalla: 2–4 acciones con **icono + etiqueta de texto** y menú "…" para el resto. Aloja la acción principal y las secundarias de la pantalla; sustituye a los botones "Volver/Guardar/Cerrar sesión" sueltos en el cuerpo.
- Flecha atrás en la barra: requerida en iOS; en Android es redundante y el Back del sistema se mantiene.
- S06 usa pivot QR · Actividades · Comprobantes (ver S06). S04/S05 conservan resultados agrupados por tipo con encabezados de sección.
- No hay panorama horizontal en V1.1.

### Campos de entrada (M8)

- Relleno plano (`surfaceContainer`), etiqueta fija pequeña encima del campo (no flotante).
- Borde de 2 dp con acento solo en foco o error; sin outline Material en reposo.
- Texto de error contextual debajo, con icono + texto (no solo color).

### Bordes (M9)

- Los bordes pasan a uso **funcional**: foco, campo en foco/error, tile seleccionado.
- Se elimina el borde de reposo en filas, botones, badges y tiles.
- La separación estructural se logra **mediante espacio y bloques de color**.

### Botones (M10)

- Acción principal: bloque sólido de acento o acción en la app bar.
- Acciones secundarias: texto con icono, sin caja.

### Movimiento (M11)

- Turnstile entre pantallas (ya existe en `XauxaTurnstileNav`).
- Tilt al presionar un tile: ≤150 ms.
- Entrada escalonada de tiles: 30–50 ms por tile, total ≤300 ms.
- El live tile cambia su contenido SOLO cuando cambia el dato, con una transición por cambio; no hay rotación en bucle (los loops decorativos no forman parte del lenguaje).
- Con reduced motion: sin tilt, sin escalonado, sin flip; solo cambio de estado inmediato. La comprensión no depende de la animación (§11).

### Iconografía (M12)

- **Set: Lucide** (licencia ISC — texto del aviso en `docs/05-design-system/06-licencias-terceros.md`). Se consume mediante una dependencia KMP que cubra Android, iOS y wasm (el laboratorio corre en wasm). Candidata: `com.composables:icons-lucide` — documentada; verificación de versión, compatibilidad con Kotlin 2.2.20 y target wasm pendiente en la pasada de implementación (no se modifican archivos de build en esta pasada documental).
- Estilo: glifos de línea, trazo uniforme, un solo color, sin relleno. Concesión registrada: Lucide usa terminales redondeadas, menos angulosas que el Metro original; se acepta (punto abierto en ADR-0005).
- Etiqueta visible: ver §11 (decisión de accesibilidad y desviación consciente de Metro).
- Tamaños de icono como tokens en `XauxaTokens.kt` (múltiplos de 4 dp); nunca literales en `core/ui` (el gate `verifyDesignSystemCompliance` los rechaza).
- Mapeo mínimo (candidatos a confirmar contra el catálogo real de Lucide durante la implementación; no se dan por existentes): buscar → `Search`; añadir → `Plus`; registrar → icono de recibo o lápiz sobre cuadrado (`Receipt`/`SquarePen`); favorito → `Star`; contexto → `Folder` o `Layers`; atrás → `ArrowLeft`; guardar → `Check`; cancelar → `X`; más → `Ellipsis`; Galería → `Image`; Cámara → `Camera`.
- Fuera de la lista mínima no se añaden iconos sin decisión; los iconos en cada fila de una lista no forman parte de Metro.

### Modo oscuro (M13)

- Fondo negro puro; los tiles conservan los mismos acentos. La regla de contraste de cada acento con su texto (tabla de Color) es válida también contra el fondo negro del tema oscuro. El foco visible debe mantenerse sobre el color del tile en ambos temas (§11).

## 13. Fuera de esta congelación

No definido en V1.1: implementación Kotlin/Compose de esta enmienda (rejilla, app bar, pivot, campos planos, integración Lucide, Archivo Light), navegación concreta, ViewModels/state holders, persistencia concreta, nuevos componentes, tests instrumentados ni validación runtime de accesibilidad de los tiles. El código actual queda desalineado de V1.1 hasta la pasada de implementación dedicada (registrado en `docs/09-implementacion/01-estado.md`).

## 14. Estado

**UX/UI V1.1: FROZEN** (enmienda ADR-0005, 2026-10-05).
