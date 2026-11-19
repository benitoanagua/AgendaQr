# Plan de paridad de componentes Polaris + cobertura mobile

**Estado:** Plan normativo de implementación — Fase 2  
**Autoridad:** ADR-0025, ADR-0026, ADR-0027, contrato normativo de diseño v8.3, matriz de cobertura Polaris/Moon/Agenda QR y UX/UI V1 congelada.  
**Rama:** `feat/polaris-mobile-component-parity`

## 1. Objetivo

Llevar `core/ui` a una cobertura amplia y consistente de los patrones de Shopify Polaris que son pertinentes para Agenda QR, con adaptación mobile nativa. Polaris sigue siendo la única autoridad de identidad visual, tokens, tipografía, iconografía, jerarquía, estados y composición. Moon se utiliza exclusivamente como referencia complementaria de patrones de interacción y controles para pantallas táctiles.

No se incorpora Flutter, Dart, Tailwind, React, Moon CSS ni el runtime de Moon. No se incorporan tokens, colores, radios, tipografía, iconos ni animaciones de Moon. Toda implementación vive en Kotlin Multiplatform/Compose y consume tokens semánticos Aq/Polaris.

## 2. Regla de precedencia

1. Agenda QR: reglas de negocio, límites del producto y modelo de dominio.
2. UX/UI V1: flujos, navegación conceptual, copy y contratos funcionales congelados.
3. Polaris reimplementado: identidad visual, tokens, componentes, iconografía, jerarquía y accesibilidad.
4. Adaptación mobile: convenciones nativas de Android/iOS, targets táctiles, insets, IME, gestos de plataforma y patrones complementarios de Moon.
5. Moon: referencia de interacción, no fuente visual ni dependencia.

Cuando un patrón Moon contradiga UX/UI V1, se descarta. Cuando Polaris web no defina un detalle táctil, se implementa la adaptación mobile documentada, siempre con la semántica visual Polaris.

## 3. Qué significa «fiel clon»

La fidelidad se mide por contrato verificable y no por semejanza subjetiva:

- tokens exactos y versionados; componentes sin valores visuales propios;
- anatomía, jerarquía, densidad, tipografía, iconografía, estados y variantes de Polaris;
- comportamiento equivalente: selección, foco, teclado, errores, carga, deshabilitado, confirmación, dismiss y navegación;
- accesibilidad: semántica Compose, lector de pantalla, contraste AA, foco, escalado de fuente y target táctil mínimo de 48 dp;
- variantes responsive para compact, medium y expanded; insets, IME, orientación y safe areas;
- pruebas de contrato por componente y capturas de galería en claro/oscuro.

Una pantalla no se considera completa solo porque el componente exista. Requiere estados, pruebas y evidencia visual suficientes.

## 4. Inventario de patrones complementarios Moon

La revisión de Moon se usa para recordar capacidades y estados a cubrir, no para copiar su apariencia. El catálogo consultado incluye Accordion, Alert, AuthCode, Avatar, BottomSheet, Breadcrumb, Button, Carousel, Checkbox, Chip, CircularProgress, Combobox, Drawer, Dropdown, Form, Group, IconButton, Input, InsetInput, NativeSelect, Loader, MenuItem, Modal, Pagination, Popover, Progress, Radio, Search, Snackbar, Switch, Table, Tabs, Tag, TagsInput, Textarea y Tooltip.

Cada patrón pasa por una decisión de producto:

- **Adoptar y adaptar**: resuelve una necesidad de UX V1 o una deficiencia demostrada de una pieza existente.
- **Reforzar un componente existente**: añadir estados, semántica, comportamiento o pruebas sin crear una API paralela.
- **No aplicar**: no existe flujo que lo necesite; se registra la justificación y no se añade deuda.

## 5. Backlog priorizado

### P0 — Fidelidad y accesibilidad transversal

1. Crear una ficha de contrato por componente compartido: anatomía, tokens semánticos permitidos, variantes, estados, semántica, comportamiento de foco, gestos, screenshots y pruebas.
2. Reconciliar la matriz con código, usos y tests antes de cambiar el estado COMPLETE/PARTIAL.
3. Completar semántica de badges y contadores de navegación/filtros (anuncios de cantidad y estado).
4. Completar semántica de tabs y segmented controls: selected, disabled, navegación por teclado/plataforma, grupo y etiqueta accesible.
5. Completar estados focused, pressed, disabled, loading y error de inputs, búsqueda, chips y selectores.
6. Probar contraste AA, font scale 100/150/200 %, targets de 48 dp y layouts 320/360/600/840 dp.
7. Verificar que los componentes reutilizables no exponen colores, espaciados, radios ni tipografía hardcodeados.

### P1 — Controles de formulario y selección

1. Textarea: conservar multiline real, límite/contador opcional solo si el caso de producto lo exige, label, helper, error, disabled y escala dinámica.
2. Select: evaluación de búsqueda local solo cuando el tamaño real de opciones lo justifique; mantener diálogo/modal Polaris si la UX define lista cerrada.
3. Date/time picker: reducir la divergencia visual de los puentes Material 3; estudiar diálogo/calendario propio con tokens Polaris, formato local, teclado, accesibilidad y pruebas.
4. Grupos de selección: fieldset semántico, helper/error de grupo y estado indeterminado solo cuando exista un caso real.
5. Controles de filtro: scroll horizontal independiente, badges semánticos, estado activo y restauración de selección.

### P1 — Overlays y navegación mobile

1. Bottom sheet: evaluar drag-to-dismiss, estados expandido/medio/compacto, gestos anidados y conflicto con contenido desplazable. Activar solo si existe uso probado; el dismiss accesible por botón/back debe permanecer.
2. Dialog/modal: foco inicial y retorno de foco para todos los diálogos, orden de lectura y dismiss seguro.
3. Action sheet/overflow: mantener acciones explícitas y accesibles; no sustituir automáticamente por popover anclado.
4. Navigation bar/rail/top bar: verificar selección, disabled, badge, más de cuatro destinos, etiquetas y adaptación por ancho.
5. One-hand reach: validar que acciones principales y controles frecuentes permanecen alcanzables sin ocultar información.

### P2 — Presentación de datos y contenido

1. Description list/key-value: componente semántico para pares de metadatos si AqMetaRow no cubre alineación, lectura o valores largos.
2. Activity feed/timeline: solo si el dominio y la UX incorporan cronología diferenciada de una lista ordenada.
3. Media/image viewer y adjuntos: solo para un flujo real de visualización; no duplicar ComprobanteViewer ni QR Preview.
4. Pull-to-refresh, swipe actions, drag-and-drop, sticky headers, breadcrumbs, drawer, FAB, stepper, quantity selector, tooltip/popover y avatar group: no implementar mientras UX V1 no los requiera.

## 6. Reglas para Compose Multiplatform

- API pública commonMain siempre que la capacidad se pueda implementar de forma compartida.
- expect/actual solo para capabilities que realmente dependen de plataforma.
- No introducir dependencias Android-only en commonMain.
- No añadir una dependencia Flutter, Moon ni runtime web.
- No reemplazar componentes Aq* funcionales sin prueba de regresión y justificación.
- Material 3 solo puede permanecer como puente de infraestructura; nunca como autoridad visual.
- Android debe tener pruebas JVM/Robolectric y compilación/ensamblado/lint.
- iOS debe compilar los targets disponibles; runtime y VoiceOver solo se marcan verificados tras ejecutar en Xcode/simulador/dispositivo Apple.

## 7. Definición de terminado por componente

Un componente solo puede pasar a COMPLETE cuando:

1. su anatomía y valores corresponden al contrato Polaris aplicable;
2. sus variantes y estados requeridos están implementados;
3. todos los colores, tamaños, radios, tipografía, elevación y motion provienen de tokens semánticos;
4. los targets táctiles y la semántica son correctos;
5. hay pruebas de estados normal/seleccionado/deshabilitado/error/carga según aplique;
6. se verifica en tema claro y oscuro y en tamaños compact/expanded;
7. el gate de arquitectura y las pruebas afectadas están en verde;
8. el estado iOS distingue compilación de ejecución real, sin afirmar validaciones no realizadas.

## 8. Secuencia de ejecución propuesta

1. Auditoría de uso + estado real; eliminar filas obsoletas o duplicadas de la matriz.
2. Completar P0 y dejar la galería como catálogo de estados.
3. Resolver P1 por componente y por causa compartida, no por retoques aislados de pantalla.
4. Regenerar capturas/contratos de pantallas afectados.
5. Ejecutar `verifyAgendaQrArchitecture`, `verifyDocs`, pruebas de core UI, presentación, Android, assembly y lint.
6. Compilar iOS; registrar NO VERIFICADO para runtime cuando no exista entorno Apple.
7. Actualizar matriz, STATE y ADR solo con resultados observados.

## 9. Límites de producto y de marca

La app conserva su identidad y nombre Agenda QR; no incorpora marca, logos ni claims de Shopify/Moon. Las referencias se mantienen en documentación de ingeniería. La fidelidad se refiere al sistema de componentes y su contrato de UX, sujeta a la política de licencias que ya está reconocida en ADR-0025 y el inventario de terceros.

## 10. Referencias

- Shopify Polaris: https://shopify.dev/docs/api/app-home/latest/web-components
- Shopify Polaris visual design: https://shopify.dev/docs/apps/design/visual-design
- Moon Design System components: https://archive.moon.io/components
- Moon Design System getting started: https://archive.moon.io/gettingStarted
- Agenda QR ADR-0025 y contrato normativo de diseño v8.3.
