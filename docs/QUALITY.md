# Agenda QR — Calidad

Fuente única de la estrategia de pruebas, la suite de estados/eventos, las
puertas de calidad y la validación por plataforma. Consolida los antiguos
documentos de `08-validacion` y la sección de puertas de `09-implementacion`.

Cada comando de este documento corresponde a una tarea Gradle real. Distingue
entre ejecución local, CI, aceptación contra backend y validación manual.

## 1. Estrategia de pruebas

La validación funcional cubre destinos QR, operaciones, comprobantes y el
comportamiento UX congelado.

### Destinos / QR

- incorporar desde galería;
- incorporar desde «Desde otra app»;
- incorporar desde cámara;
- incorporar múltiples imágenes;
- detectar/leer QR;
- revisar candidato;
- guardar destino;
- detectar duplicado sin eliminación automática;
- buscar;
- mostrar;
- compartir;
- reemplazar;
- recuperar sin conexión.

### Operaciones

- crear PAGO mínimo;
- crear COBRO mínimo;
- crear operación sin contexto/destino;
- conservar draft al seleccionar contexto;
- evitar duplicado por doble guardado;
- crear operación sin comprobante;
- adjuntar comprobante después.

### Comprobantes

- guardar comprobante sin operación;
- guardar desde galería/otra app;
- asociar después;
- desasociar;
- reasociar;
- múltiples comprobantes;
- duplicado detectado sin bloqueo;
- ambigüedad;
- compartir;
- eliminar comprobante.

### Importación masiva

- separar reconocidos, duplicados y pendientes;
- guardar reconocidos aunque existan pendientes;
- fallo parcial de un elemento;
- revisión posterior de pendientes;
- error all-failed: todos los candidatos fallan → estado Saved (no Error);
- partial save: guardado parcial con reintentos;
- retry after error: reintento tras fallo;
- after successful retry: estado tras reintento exitoso;
- multiple issues: múltiples problemas en el lote;
- small viewport: importación en viewport reducido;
- login error: error de autenticación durante importación.

### Offline / sincronización

- guardar localmente sin conexión;
- pasar a pendiente de sincronización;
- sincronizar al recuperar conexión;
- conservar información ante fallo remoto;
- reintentar sincronización;
- diferenciar «Guardado» de «Sincronizado».

### Recuperación y caos

- Back conserva draft;
- Cancelar abandona la intención;
- doble tap no duplica;
- eventos repetidos no duplican;
- evento tardío no retrocede estado;
- cierre durante guardado;
- cierre durante análisis;
- latencia extrema;
- estado parcial;
- búsqueda sin resultados;
- búsqueda offline.

### Integridad

- editar campo sensible con comprobante;
- editar campo no sensible;
- eliminar operación con comprobantes;
- consultar histórico mínimo tras eliminación;
- cambiar QR del destino y verificar que la historia no se reescribe.

### Accesibilidad

- targets mínimos;
- focus visible;
- texto largo;
- dynamic type;
- lector de pantalla;
- reduced motion;
- estados comprensibles sin depender solo del color.

Criterio: los escenarios conceptuales pueden declararse cubiertos antes de
implementación. Los casos que dependen de runtime, persistencia, plataforma o
dispositivos permanecen pendientes hasta disponer de evidencia real.

## 2. Suite de estados y eventos UX V1

Contrato de pruebas conceptual. Formaliza las reglas UX ya consolidadas y no
introduce requisitos nuevos. Base: UX/UI V1 congelada en
`04-ux/02-especificacion-ux-ui-v1.md`.

### Estados

```text
HOME
SEARCH
ADD
CAMERA
REGISTER
CONTEXT
CONTEXT_SELECTION

SEARCH_IDLE
SEARCHING
SEARCH_RESULTS
SEARCH_EMPTY
SEARCH_ERROR

IMPORTING
ANALYZING
CLASSIFIED
REVIEWING

QR_REVIEW
QR_DUPLICATE
QR_SAVED
QR_ERROR

RECEIPT_REVIEW
RECEIPT_MATCHED
RECEIPT_AMBIGUOUS
RECEIPT_UNASSOCIATED
RECEIPT_ASSOCIATED
RECEIPT_ERROR

PAYMENT_DRAFT
PAYMENT_SAVING
PAYMENT_SAVED
PAYMENT_ERROR

PENDING_SYNC
SYNCING
SYNCED
SYNC_ERROR

BULK_IMPORTING
BULK_ANALYZING
BULK_REVIEW
BULK_PARTIAL_SUCCESS
BULK_ERROR
```

### Transiciones críticas

| Estado | Evento | Resultado |
|---|---|---|
| Home | Buscar | Search |
| Home | Añadir | Add |
| Home | Registrar | Register |
| Search | escribir | Searching |
| Searching | resultados | SearchResults |
| Searching | sin resultados | SearchEmpty |
| Searching | error | SearchError |
| Add | Galería | Importing |
| Add | Desde otra app | Importing |
| Add | Cámara | Camera |
| Camera | QR detectado | QRReview |
| Analyzing | QR | QRReview |
| Analyzing | comprobante | ReceiptReview |
| QRReview | Guardar | Saving → Saved |
| QRReview | duplicado | QRDuplicate |
| ReceiptReview | coincidencia | ReceiptMatched |
| ReceiptReview | ambigüedad | ReceiptAmbiguous |
| ReceiptReview | sin coincidencia | ReceiptUnassociated |
| ReceiptReview | Asociar | ReceiptAssociated |
| Register | seleccionar contexto | ContextSelection |
| ContextSelection | seleccionar | Register |
| Register | Guardar | PaymentSaving |
| PaymentSaving | éxito | PaymentSaved |
| PaymentSaving | fallo | PaymentError |
| PendingSync | conexión | Syncing |
| Syncing | éxito | Synced |
| Syncing | fallo | SyncError |

### Casos críticos

- **UX-001 Doble guardado.** PaymentDraft → Save → PaymentSaving → segundo Save
  ignorado/idempotente. Esperado: una sola operación efectiva.
- **UX-002 Back durante selección.** PaymentDraft(amount=450) → SelectContext →
  ContextSelection → Back → PaymentDraft(amount=450). El draft no pierde
  información.
- **UX-003 Selección de contexto.** El contexto seleccionado vuelve al draft
  original.
- **UX-004 Cancelar registro.** PaymentDraft → Cancel → Home. No se guarda una
  operación no confirmada.
- **UX-005 Pago sin contexto.** Debe ser posible guardar una operación mínima con
  contexto ausente.
- **UX-006 QR desde galería.** Gallery → Importing → Analyzing → QRReview. No
  pedir clasificación si la detección es clara.
- **UX-007 QR desde otra app.** La recepción externa converge en el mismo
  pipeline.
- **UX-008 QR ilegible.** Existe recuperación directa mediante otra imagen o
  back.
- **UX-009 QR duplicado.** Informar sin borrar ni fusionar automáticamente.
- **UX-010 Comprobante sin actividad.** Debe poder guardarse como
  ReceiptUnassociated.
- **UX-011 Coincidencia clara.** Proponer asociación; el usuario confirma.
- **UX-012 Ambigüedad.** No elegir automáticamente entre candidatos materialmente
  diferentes.
- **UX-013 Asociación reversible.** Associated → Detach → Unassociated. El
  archivo permanece.
- **UX-014 Búsqueda global.** Una consulta puede devolver contexto, QR, actividad
  y comprobante.
- **UX-015 Búsqueda vacía.** Ofrecer recuperación contextual sin convertirla en
  error fatal.
- **UX-016 Búsqueda offline.** Los datos locales disponibles siguen siendo
  consultables. Fuente remota inaccesible no equivale a cero resultados.
- **UX-017 Guardado offline.** Save → Saved → PendingSync. No hay pérdida.
- **UX-018 Reconexión.** PendingSync → Syncing → Synced.
- **UX-019 Error de sincronización.** Conservar el dato local y permitir
  reintento.
- **UX-020 Cierre durante guardado.** La implementación debe determinar de forma
  fiable si la mutación local quedó persistida.
- **UX-021 Cierre durante análisis.** La implementación debe definir una política
  determinista de reanudación o descarte explícito.
- **UX-022 Importación masiva parcial.** Elementos reconocidos se pueden guardar
  aunque otros requieran revisión.
- **UX-023 Fallo parcial.** Un fallo de un elemento no convierte todos los
  elementos válidos en fallidos.
- **UX-024 Duplicado dentro de lote.** El duplicado se informa sin impedir
  guardar elementos válidos.
- **UX-025 Pérdida de red.** Search, Analyze, Save y Sync tienen recuperaciones
  diferentes.
- **UX-026 Evento repetido.** Save/Associate/Import no deben duplicar.
- **UX-027 Evento tardío.** Un resultado tardío no puede regresar la máquina a un
  estado anterior inválido.
- **UX-028 Latencia extrema.** Una operación en curso mantiene una identidad
  única y no permite escrituras paralelas accidentales.
- **UX-029 Accesibilidad.** La jerarquía, acción primaria y estado siguen siendo
  comprensibles con texto grande, lector de pantalla y reduced motion.
- **UX-030 Texto largo.** Nombres y metadatos largos no provocan solapamientos ni
  ocultan la acción primaria.
- **UX-031 Estado parcial.** Una sección vacía no invalida el resto del contexto.
- **UX-032 Intención preservada.** Un selector temporal no reemplaza la intención
  del flujo padre.
- **UX-033 Contexto con distintas intenciones.** La misma superficie Context
  puede actuar como consulta o selección según el flujo que la abrió.

### Invariantes

- I01 Back no destruye draft.
- I02 Guardar repetidamente no crea duplicados.
- I03 Offline no implica pérdida.
- I04 Comprobante no requiere QR.
- I05 Operación no requiere contexto.
- I06 QR no requiere cámara.
- I07 Importación válida no depende de importación inválida.
- I08 Asociación es reversible.
- I09 Ambigüedad no se resuelve arbitrariamente.
- I10 Guardado no equivale a sincronizado.
- I11 Error recuperable conserva el trabajo.
- I12 Buscar no requiere elegir tipo antes.
- I13 Contexto preserva la intención del flujo padre.
- I14 Estados técnicos no crean navegación artificial.
- I15 Cerrar la aplicación no convierte un guardado confirmado en estado
  desconocido.
- I16 Evento tardío no puede retroceder a una fase inválida.
- I17 Una operación en curso tiene una única identidad aunque se dispare varias
  veces.
- I18 Todo estado no terminal tiene salida recuperable o cancelable.

Invariantes del sistema visual y accesibilidad (contrato normativo,
`docs/05-design-system/00-contrato-normativo-diseno.md`):

- I19 Contraste de acentos: cada par acento/texto cumple ≥ 4.5:1 en texto normal
  (`AmAccentTest`).
- I20 Todo control interactivo expone su rol y etiqueta accesible
  (`ScreensA11yCoverageTest`, `DestinationsScreenA11yTest`,
  `AmAccessibilityContractTest`), con targets de 48 dp.
- I21 Reduced motion desactiva la transición de superficies.
- I22 No hay bucles de animación decorativos.
- I23 Los controles evitan el borde de reposo universal (guía hairline en
  reposo/foco/error; foco ≥ 3:1).
- I24 Todo icono interactivo lleva descripción accesible; los decorativos se
  marcan como no accesibles.

## 3. Puertas de calidad

Ejecutar con `-PallowDebugSigningForRc=true` (el guard de firma de release se
evalúa en configuración).

| Puerta | Comando | Qué verifica |
|---|---|---|
| Arquitectura + design system + no-scripts | `./gradlew verifyAgendaQrArchitecture` | `verifyDocs`, `verifyNoScripts`, `verifyDesignSystemCompliance`, `verifyDesignSystemFixtures`, `verifyControlMinSize`, `verifyArchitectureBoundaries` |
| Área táctil mínima | `verifyControlMinSize` | `AmMetrics.ControlMinSize == 48.dp` |
| Contrato de diseño | `verifyDesignSystemCompliance` + `verifyDesignSystemFixtures` | literales visuales fuera de tokens, imports prohibidos, copy, bordes, interacción cruda, tabla de excepciones |
| Documentación | `verifyDocs` | enlaces vivos, todo `.md` alcanzable desde `docs/README.md`, sin marcadores transitorios, ADR citadas existentes |
| Contraste y esquema | `AmSchemeTest`, `AmContrastPairsTest`, `AmAccentTest` | umbrales y pares fijados |
| Tipografía y layout | `AmTypographyTest`, `AmPivotLayoutTest`, `AmUiPolishContractTest` | roles, interlineado, escalado de fuente |
| Accesibilidad de componentes | `AmAccessibilityContractTest`, `ScreensA11yCoverageTest` | semántica, roles, targets |
| Correcciones UX (ADR-0018) | `UxFilterReproductionTest`, `UxDateReproductionTest`, `UxEditorDiscardTest`, `UxAmountValidationTest`, `UxHomeHubTest`, `UxUnassociatedTest`, `UxLockSearchTest`, `QrFullscreenTest`, `ExternalFlowTrackerTest`, `AmDateTimePickerTest` | filtros idempotentes, fecha con hora, descarte confirmado, importe válido, hub de Inicio, bandeja sin primarios, biometría/foco, brillo restaurado, gracia de bloqueo, pickers bajo Robolectric |
| Regresión visual | `recordRoborazzi` → `recordVisualHashes` / `verifyVisualHashes` | 80 capturas (40 escenarios × claro/oscuro, incl. tablet w720dp con NavigationRail y textos largos) por SHA-256 (ADR-0007, sensible al entorno) |
| Arranque local-first y PIN | `:feature:destinations:presentation:testDebugUnitTest --tests "*AgendaQrLocalStartupTest"` | arranque real sin cuenta, perfil local, desbloqueo |
| Máquina de estados del acceso local | `--tests "*LocalAccessGateTest"` | configurar → desbloquear → bloquear, saneado, intentos, bloqueo temporal |
| Biometría (atajo fail-closed) + demo | `--tests "*LocalAccessBiometricTest" --tests "*DemoDataTest" --tests "*DemoSeederTest"` | éxito biométrico desbloquea, fallo conserva bloqueo + fallback PIN; dataset demo determinista, siembra idempotente y aislada |
| Mapeo biométrico de plataforma + fail-closed | `--tests "*BiometricPromptErrorMappingTest"` (androidUnitTest de `:feature:destinations:data`) | códigos `BiometricPrompt` → contrato compartido; ningún código de error desbloquea; sin Activity host → `Unavailable` |
| Migración de bytes heredados | `--tests "*LegacyComprobanteBytesMigrationTest"` | idempotencia, no-borrado del origen, fallo parcial sin aborto, reintento recupera, aislamiento por propietario |
| Namespace por perfil + migración | `--tests "*LocalIdentityTest"` | claves por perfil, detección heredada por claves sin cliente remoto, migración idempotente |
| Contrato de QR | `--tests "*QrPreviewContractTest"` | fixture es QR real; bytes del dominio → imagen; payload no-imagen → error |
| Contrato de no solapamiento | `:core:ui:testDebugUnitTest --tests "*AmScaffoldOverlapContractTest"` | el contenido termina por encima del dock/FAB |
| Ensamblado Android | `:androidApp:assembleDebug` / `assembleRelease` | compilación del host |
| Compilación iOS | `:core:ui:compileKotlinIosSimulatorArm64 :feature:destinations:presentation:compileKotlinIosSimulatorArm64` | compila (runtime requiere Xcode) |
| Lint Android | `:androidApp:lint` | sin problemas nuevos |
| Supabase acceptance | `DATABASE_URL=jdbc:postgresql://... ./gradlew :supabase:acceptance:test` | RLS, aislamiento entre usuarios, migraciones |

### Detalle: regresión de importación con fallos parciales

**Causa raíz del bug.** `SaveImportBatchUseCase` capturaba todas las excepciones
internamente sin propagarlas. Cuando todos los candidatos fallaban, el caso de uso
devolvía un estado `Saved` en lugar de `Error`, lo que impedía al usuario
detectar que la importación había fallado por completo.

**Corrección.** El caso de uso ahora propaga los fallos mediante
`ImportBatchSaveResult.failed`, permitiendo que la capa de presentación distinga
entre guardado parcial (algunos elementos guardados, otros pendientes) y fallo
total (ningún elemento guardado → estado `Error` con opción de reintento).

**Semántica de fallos parciales:**

- `ImportBatchSaveResult.saved` — todos los elementos se guardaron correctamente.
- `ImportBatchSaveResult.partial` — algunos elementos se guardaron; otros
  quedan pendientes de revisión.
- `ImportBatchSaveResult.failed` — ningún elemento se guardado; se muestra el
  estado `Error` con opción de reintento.

**Escenarios de regresión cubiertos por `ImportFlowRegressionTest`:**

- **error all-failed:** todos los candidatos fallan → estado `Error` (no `Saved`).
- **partial save:** guardado parcial con elementos pendientes.
- **retry after error:** reintento tras un fallo total.
- **after successful retry:** estado correcto tras un reintento exitoso.
- **multiple issues:** múltiples problemas simultáneos en el lote.
- **small viewport:** importación en viewport reducido (layout adaptado).
- **login error:** error de autenticación durante la importación.

### Detalle: regresión visual (Roborazzi)

**Procedimiento de generación y verificación de hashes:**

1. **Generación:** `./gradlew recordRoborazzi` ejecuta los escenarios en
   Robolectric y genera las capturas PNG en
   `feature/destinations/presentation/build/roborazzi/`.
2. **Hash:** `recordVisualHashes` calcula el SHA-256 de cada captura y lo
   almacena en el manifiesto.
3. **Verificación:** `verifyVisualHashes` compara el SHA-256 de cada captura
   actual contra el manifiesto. Cualquier diferencia indica un cambio visual
   no esperado.

**Sensibilidad al entorno:** el hash es sensible al JDK/OS (ADR-0007). La
verificación debe ejecutarse en el mismo entorno que generó el manifiesto, o
regenerar el manifiesto con `recordVisualHashes` antes de comparar.

### Detalle: jerarquía de acciones (4 niveles)

El sistema de diseño define una jerarquía semántica de acciones documentada en
la javadoc de `AmButtons.kt` y verificable por composición:

| Nivel | Componente | Tratamiento | Uso |
|---|---|---|---|
| 1 — Primaria | `AmPrimaryButton` / `AmBottomAction` | Relleno de acento (`AmColor.Accent`) | El objetivo principal del usuario por sección; como máximo una por contexto |
| 2 — Secundaria | `AmSecondaryButton` | Tonal (`SurfaceContainerLow` + hairline `Divider`); variante `solid` para la acción insignia de una tarjeta | Alternativas importantes que no compiten con la primaria |
| 3 — Terciaria | `AmTextAction` | Texto con área táctil de 48 dp | Acciones contextuales y navegación auxiliar |
| 4 — Destructiva | `AmDangerButton` / `AmDialog(destructive = true)` | Tono peligro + texto específico; confirmación cuando hay pérdida irreversible | Descartar/eliminar; nunca compite visualmente con la primaria |

**Reglas aplicadas en los flujos:**

- En pantalla de lectura/búsqueda no se fuerza acción primaria (S04, S06).
- Importación S12 — tarjeta de duplicado: primaria contextual «Ver existente»
  (acento), secundaria «Guardar de todos modos» (tono secundario), terciaria
  «Descartar» (tono terciario). Tarjeta sin clasificar: primaria «Reintentar
  clasificación»; «Descartar» pide confirmación porque pierde el contenido
  retenido (no recuperable en el flujo); sobre duplicados el descarte es
  directo (el original existe).
- El botón global «Guardar reconocidos» vive fijo abajo y se separa de las
  acciones por elemento; en estado de error su etiqueta pasa a «Reintentar
  guardado».
- Inicio S01: «Escanear QR» (sólida) sobre «Importar imagen» (tonal), apiladas
  a etiqueta completa; la FAB «Añadir destino» conserva el rol de acción
  flotante de plataforma.
- Selector de contexto S08: «Quitar contexto» sale del slot de salida del
  diálogo y vive en el contenido como acción destructiva (peligro + glifo);
  «Cancelar» es la única salida.
- Cobertura: `ActionHierarchyTest` (jerarquía por tipo de incidencia,
  confirmación de descarte, cancelación conservadora, sin acciones
  incompatibles con el estado) + `ScreensA11yCoverageTest` (estados legibles
  como texto) + gates del design system.

Pruebas de módulos:

```bash
./gradlew :core:ui:testDebugUnitTest \
  :feature:destinations:presentation:testDebugUnitTest \
  :feature:destinations:domain:test \
  :feature:destinations:data:testDebugUnitTest -PallowDebugSigningForRc=true
```

## 4. Validación por plataforma

### Android (runtime verificable en este entorno)

Pruebas unitarias JVM (Robolectric/Roborazzi), gates estáticos, lint, ensamblado
debug y release. El arranque real se comprueba componiendo `AgendaQrApp()` en
Robolectric (`AgendaQrLocalStartupTest`).

### iOS

Se compila para `iosSimulatorArm64` (y `iosArm64` en presentación). **El runtime
iOS requiere Xcode y no se verifica en este entorno**: se registra como
`NO VERIFICADO`. La compilación iOS NO sustituye una prueba de runtime.

### Instrumentadas (androidTest)

Compilan y, con emulador/dispositivo, ejecutan los flujos de accesibilidad,
permisos de cámara, anuncios, háptica, movimiento reducido, rotación, fuentes y
compartir. Requieren dispositivo/emulador; sin ellos se registran como
`NO VERIFICADO`.

### Aceptación contra Supabase

`:supabase:acceptance:test` requiere una base de datos real (`DATABASE_URL`). Sin
backend se registra como `NO VERIFICADO`. La sincronización real (subida/bajada)
tampoco se ejercita en pruebas unitarias.

### CI

El workflow de Android (`android-validation.yml`) ejecuta las puertas y el
ensamblado. Si algún paso usa `continue-on-error`, esa limitación debe
considerarse al interpretar el resultado global. La ejecución de CI está fuera
del entorno local y se registra como `NO VERIFICADO` localmente.

## 5. Verificaciones manuales (requieren dispositivo)

No hay pantalla global de error; cada verificación lista pasos y criterio de
éxito.

### TalkBack (Android, dispositivo físico)

1. Orden de lectura en Inicio: título → barra de búsqueda (un solo nodo «Buscar
   en Agenda QR», rol botón) → filas (rol botón + etiqueta) → fila con su
   badge/acción. **Éxito:** cero nodos duplicados.
2. Permiso de cámara denegado con «No volver a preguntar»: el banner anuncia el
   error y «Abrir ajustes» abre los ajustes de la app, alcanzable sin ver la
   pantalla.
3. Escanear un QR real: se oye «Código QR detectado» (liveRegion) y hay háptica
   ligera; un solo anuncio por lectura.
4. Guardar un lote: al llegar a Saved se oye «Guardado» una única vez; al
   vaciarse la cola, «Sincronización completada».
5. Eliminar un destino: el foco inicial cae en «Cancelar» (destructivo);
   confirmar anuncia «Eliminado».
6. Error de campo en formularios: el mensaje se anuncia (liveRegion).

### VoiceOver (iOS, requiere Xcode + dispositivo)

1. Mismos flujos que TalkBack (1–6). **Éxito:** paridad de anuncios (los anuncios
   viven en semántica común; iOS runtime sin validar hasta aquí).
2. Permisos de cámara iOS: pedir por primera vez (NotDetermined), denegar y
   reintentar: el banner ofrece «Abrir ajustes».
3. Dynamic Type al máximo: Inicio y registro sin recortes.

### Predictive back (Android 14+, API 34+)

1. Con el gesto desde el borde: la vista previa de vuelta es progresiva y ninguna
   transición propia interfiere.
2. Durante un flujo con draft: la vista previa no consume el draft hasta soltar.
3. Raíz: el gesto cierra la app sin animación rota.
4. Reduced motion activado: la transición es instantánea.

### Fuente al 200 % (Android)

`adb shell settings put system font_scale 2.0`: recorrer las pantallas en claro y
oscuro. **Éxito:** nada cortado ni solapado.

### Rotación

1. Una superficie abierta se conserva (Saver) y el foco es utilizable.
2. Un formulario lleno conserva el draft y los errores visibles.
3. La cámara se re-liga y sigue detectando.

## 6. Limitaciones del entorno de ejecución

- No hay Xcode: runtime iOS `NO VERIFICADO` (checklist en
  [OPERATIONS.md](OPERATIONS.md)).
- No hay dispositivo físico: TalkBack/VoiceOver interactivo, insets/notch,
  cámara, foco nativo, motion y predictive back reales `NO VERIFICADO` (ver §5).
- No hay backend Supabase en este entorno: aceptación JDBC y sincronización real
  `NO VERIFICADO`.
- La regresión visual (`verifyVisualHashes`) es sensible al entorno (JDK/OS):
  usar en el mismo entorno que generó el manifiesto o regenerarlo (ADR-0007).
- El estado de implementación por plataforma se mantiene en
  [STATE.md](STATE.md).
