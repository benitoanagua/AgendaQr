# Prompt maestro — AgendaQr con Google Stitch + TDesign Screen Templates

## Misión

Trabaja como agente autónomo senior de producto, UX/UI, design systems, Kotlin Multiplatform/Compose, accesibilidad, navegación, QA y automatización. Repositorio: https://github.com/benitoanagua/AgendaQr

**La base de referencia para la UI/UX ahora es el proyecto de Google Stitch MCP llamado exactamente `TDesign Screen Templates`.** Usa las herramientas MCP de Google Stitch conectadas al entorno para descubrir el proyecto, leer su contenido real, enumerar pantallas y consultar los diseños, variantes, componentes, flujos y especificaciones disponibles. No inventes IDs, pantallas, assets ni contenido de Stitch. Confirma que el proyecto al que accediste es el correcto y registra el identificador/nombre exacto y los artefactos consultados. Si el MCP no expone algún contenido, anótalo como bloqueado/no accesible y continúa con lo verificable; no simules que lo inspeccionaste.

No te limites a escribir recomendaciones. Audita el repositorio y el proyecto Stitch, reconcilia las fuentes y ejecuta los cambios que permitan las herramientas, con pruebas reales. Trabaja en una rama dedicada si puedes; no sobrescribas trabajo ajeno ni declares éxito sin evidencia.

## Contexto técnico confirmado en el repo

- Kotlin Multiplatform y Compose.
- `core/ui`: tokens, tema, iconografía y componentes compartidos.
- `feature/destinations/presentation`: pantallas y estado UI.
- `feature/destinations/domain` y `data`: reglas de negocio, persistencia local-first y sincronización.
- `build-logic/src/main/kotlin/ScreenContractGate.kt`: ya existe un contrato estructurado por pantalla y un gate; inspecciona su implementación completa antes de añadir otro sistema.
- `docs/04-ux/02-especificacion-ux-ui-v1.md`: la especificación conserva intención y navegación como congeladas.
- `docs/04-ux/03-contrato-interaccion-pantallas.md`: documento generado desde ScreenContractGate.
- `docs/05-design-system/00-contrato-normativo-diseno.md`: contrato normativo actual.
- `docs/07-decisiones/ADR-0023-adopcion-fiel-tdesign-mobile.md`: adopta `tdesign_flutter@0.2.7` como referencia de tokens y componentes.
- `docs/07-decisiones/ADR-0007-capturas-de-referencia.md`: declara que no se versionan capturas ni hashes por la variabilidad del render.
- `docs/07-decisiones/ADR-0015-referencia-stitch-jerarquia-financiera.md`: ADR histórica que acotaba Stitch a una referencia Ant Design Mobile para patrones específicos; debe revisarse porque la fuente principal de diseño ahora es TDesign Screen Templates.
- `docs/QUALITY.md`, `docs/STATE.md`, `docs/README.md` y `.github/workflows/android-validation.yml`: estrategia de calidad, estado declarado, índice y CI.
- Existe una matriz de contratos que incluye rutas, entradas/salidas, destinos, acción primaria, estados, gestos, capturas, componentes y tests. Extiéndela en vez de duplicarla.

## Principio rector y precedencia

Adopta **TDesign Screen Templates en Google Stitch como referencia primaria de composición de pantallas, jerarquía, patrones de interacción y experiencia UX/UI**, usando la implementación oficial TDesign Mobile como referencia normativa de componentes/tokens cuando corresponda. No confundas un diseño de Stitch con una especificación de comportamiento completa: inspecciona las pantallas y prototipos reales, registra qué está explícito y qué es inferencia, y verifica componentes contra la fuente oficial de TDesign.

Orden de decisión:
1. Restricciones de producto, privacidad, integridad de datos y reglas de negocio explícitas.
2. Diseños y especificaciones reales del proyecto Google Stitch `TDesign Screen Templates` para composición, estructura, intención y flujo UX/UI.
3. Fuente oficial de TDesign Mobile para tokens, componentes, estados, medidas y patrones que el proyecto Stitch use o referencie.
4. Contrato de pantalla estructurado del repositorio y ADR vigentes reconciliados con las dos fuentes anteriores.
5. Implementación Compose en `core/ui` y pantallas.
6. Pruebas y CI como comprobación ejecutable de que los anteriores coinciden.

No permitas que una ADR antigua contradiga silenciosamente esta nueva dirección. Enmienda o reemplaza las decisiones históricas con un registro claro, manteniendo su historial. No cambies reglas de negocio para copiar un mockup. Sí tienes autorización explícita para cambiar intención de pantalla, navegación, jerarquía de acciones, back stack y gestos para alinearte con TDesign Screen Templates/TDesign, siempre que la justificación y los cambios se propaguen a documentación, código y pruebas. Los cambios de producto que excedan esa autorización deben señalarse separadamente, no introducirse de forma encubierta.

## Fase 1 — Inventario y análisis de evidencia

1. Verifica rama/commit actual y estado de trabajo; lee README, PRODUCT, ARCHITECTURE, STATE, QUALITY, mapa docs, UX/UI, contrato de pantallas, contrato normativo, ADRs, tests, navegación y workflows.
2. Inspecciona por completo `ScreenContractGate.kt`: modelo, inventario de superficies, vocabularios cerrados, reglas de validación, verificación contra símbolos reales, pruebas de gate, generación documental y manifiesto de capturas. Determina qué controles existen, cuáles son eficaces y qué huecos reales quedan.
3. Con Google Stitch MCP, localiza el proyecto `TDesign Screen Templates` y recopila de forma verificable todos sus artefactos relevantes: pantallas, IDs, links internos, vistas, variantes, componentes, flujos/prototipos, instrucciones y recursos de diseño. No te detengas en la primera pantalla ni asumas que el nombre por sí solo demuestra fidelidad a TDesign.
4. Construye el inventario del código real: rutas, pantallas, overlays, sheets, diálogos, pickers, estados, eventos, entradas externas, acciones, gestos, back stack, estados de error/vacío/carga, componentes y tests.
5. Compara Stitch ↔ especificación UX/UI ↔ contrato estructurado ↔ ADR ↔ componentes `core/ui` ↔ código de pantalla ↔ pruebas ↔ capturas.
6. Crea una matriz de divergencias con severidad, evidencia de ambos lados, decisión propuesta, archivos a cambiar y prueba que demostrará la resolución.
7. Distingue: contradicción, ausencia, duplicación, implementación incorrecta, diferencia de plataforma justificada, patrón no documentado en Stitch, limitación de MCP y deuda no verificable.

No uses solo búsqueda textual o nombres de componentes como prueba de fidelidad. Cuando el diseño de Stitch y la fuente oficial difieran, captura la evidencia y decide qué regla es normativa. No inventes medidas, gestos ni componentes oficiales.

## Fase 2 — Convertir Stitch en fuente de diseño trazable

Para cada pantalla relevante del proyecto Stitch, registra:
- ID y nombre exacto de Stitch; enlace/identificador MCP si está disponible;
- captura/render o descripción del diseño realmente inspeccionado;
- intención principal y resultado esperado;
- estructura visual, componentes, tokens/roles, jerarquía y densidad;
- acciones primarias, secundarias, terciarias y destructivas;
- estados y variantes visibles;
- navegación, entradas/salidas y transiciones mostradas o especificadas;
- gestos: qué los dispara, resultado, alternativa accesible y conflictos con scroll/back del sistema;
- decisiones explícitas frente a inferencias del agente;
- pantalla/código AgendaQr que corresponde;
- divergencias aceptadas, con razón y ADR.

Mantén IDs estables entre Stitch, contrato y pruebas cuando sea viable; si los IDs de Stitch no son apropiados como IDs internos, crea una tabla de correspondencia determinista. No dupliques a mano el contenido completo de Stitch en muchos documentos. El contrato de pantallas estructurado es la fuente ejecutable; la documentación humana se genera o valida a partir de ella. Los artefactos Stitch son la fuente de diseño visual/UX y deben ser referenciados con identificadores verificables.

## Fase 3 — Reconciliar la normativa del repo

Revisa en particular:
- UX/UI V1 congelada que prohíbe cambiar intención, modelo mental, flujos y navegación;
- ADR-0023 que actualmente conserva navegación/back stack y no permite gestos/capacidades nuevos;
- ADR-0015, que restringía Stitch a anatomía financiera Ant Design Mobile;
- ADR-0007, que evita imágenes de referencia versionadas;
- contrato normativo y desviaciones de TDesign;
- `ScreenContractGate.kt` y su documento generado.

La nueva decisión autoriza rediseñar intención, navegación, jerarquía y gestos para seguir el proyecto Stitch TDesign. Enmienda los documentos normativos y ADR que se opongan. No mantengas simultáneamente dos fuentes de verdad. Cada decisión debe declarar: qué fuente prevalece, qué cambia, qué se conserva, evidencia, consecuencias, riesgos, pruebas y cómo migran las pantallas existentes.

Preserva, salvo contradicción de producto que deba elevarse, los límites de dominio y seguridad: modo local-first, identidad local sin cuenta obligatoria, privacidad del PIN, guardado distinto de sincronización, asociación reversible, comprobante sin QR/operación y operación sin contexto. No dejes que un mockup altere datos o reglas de dominio.

## Fase 4 — Implementar cada pantalla desde el diseño base

Trabaja pantalla por pantalla, no solo Inicio:
1. Mapea pantalla Stitch con una superficie real de AgendaQr.
2. Compara composición real y objetivo; detecta diferencias de estructura, contenido, jerarquía, espaciado, iconos, tipografía, estados, interacción y navegación.
3. Reutiliza/crea componentes canónicos en `core/ui`; no crees sistemas visuales paralelos ni estilos locales repetidos.
4. Actualiza navegación y back stack si el diseño y la evidencia lo requieren.
5. Implementa los estados relevantes (loading, vacío, contenido, error, recuperación, selección, confirmación, offline, permiso, locked, saving/saved cuando correspondan).
6. Actualiza strings y semantics, pruebas de interacción y accesibilidad, y el contrato estructurado en el mismo cambio lógico.
7. Retira excepciones/código/tests obsoletos solo tras confirmar que no tienen otra responsabilidad.
8. Añade evidencia de por qué la implementación corresponde al diseño de Stitch y TDesign.

No uses capturas de Stitch como imágenes de fondo para fingir una implementación. Reconstruye las pantallas con componentes reales, adaptables, accesibles y funcionales. No copies HTML/Tailwind de Stitch literalmente a Compose; traduce la intención visual a los tokens/componentes del sistema multiplataforma.

## Fase 5 — Detección automática de divergencias

Extiende `ScreenContractGate.kt` y las tareas Gradle existentes; no crees un segundo validador redundante. Integra las comprobaciones a `verifyAgendaQrArchitecture` y CI.

Detecta automáticamente, como mínimo:
- pantalla/código real sin correspondencia Stitch/contrato;
- pantalla del contrato o Stitch sin mapeo, estado de decisión o explicación de exclusión;
- referencias rotas a rutas, destinos, archivos fuente, componentes, strings o tests;
- navegación/transiciones documentadas que contradicen el grafo real;
- acción primaria/jerarquía o gesto que difiere del contrato;
- estados requeridos sin prueba;
- desviación TDesign usada en código pero no registrada, o excepción muerta;
- literales visuales o componentes prohibidos fuera de `core/ui`;
- captura requerida sin escenario/manifiesto;
- metadatos de captura incoherentes, duplicados y dimensiones que no correspondan al viewport;
- documento generado que diverge de la fuente estructurada;
- cambio de diseño que no actualizó ADR, UX/UI, código y pruebas según la matriz.

Prefiere IDs tipados y datos estructurados a parsers frágiles de prosa. El gate debe contrastar señales reales de código, rutas, fuentes y tests, no limitarse a validar una copia que nadie usa.

Añade pruebas negativas del propio gate: pantalla sin contrato, ruta inválida, test inexistente, gesto no permitido/no documentado, excepción visual sin registro, referencia Stitch inexistente, documento generado desactualizado, manifiesto incompleto y captura duplicada. Cada error debe indicar ID, archivo/ubicación cuando sea posible, causa y corrección. Mantén una allowlist pequeña y justificada.

No prometas detectar “cualquier desfase” sin evidencia. Documenta las clases de divergencia detectadas automáticamente y qué revisión humana sigue siendo necesaria.

## Fase 6 — Capturas de pantalla y galería: máximo de pantallas únicas

Directorio citado por el usuario: `feature/destinations/presentation/build/roborazzi/`, con nombres como `account-dark-320dp-font-200.png`, `account-light.png` y `tablet-s01-light-320dp-font-200.png`. Inspecciona el pipeline real y los archivos que existan en el entorno del agente; no supongas que la lista de ejemplos es completa ni que esos archivos están versionados.

Reglas obligatorias:
- Cero zoom artificial, upscale, crop posterior, transformación de escala o viewport falso.
- Captura toda la ventana en el tamaño/densidad del escenario real. No cortes contenido para aparentar mejor composición.
- No repitas flujos. Prioriza la mayor cantidad posible de pantallas y estados diferentes.
- Cuenta cobertura por ID de pantalla + estado + configuración; dos nombres distintos con el mismo contenido/configuración no son cobertura adicional.
- Incluye todas las pantallas y superficies relevantes: raíz, detalle, edición, revisión, selección, overlays/diálogos y estados funcionales significativos. No generes solo las pantallas fáciles.
- Usa variantes únicamente cuando agreguen evidencia: vacío, datos, loading, error/recuperación, selección, confirmación destructiva, etc.
- La galería de revisión debe maximizar pantallas/estados únicos; la suite automática puede probar combinaciones adicionales para accesibilidad y adaptabilidad.
- Incluye configuraciones requeridas por contrato: claro/oscuro, 320 dp, 360 dp, tablet/NavigationRail donde aplique, fuente al 200 %. No etiquetes tablet si el escenario no lo demuestra.
- Verifica nombres y metadatos; el ejemplo `tablet-s01-light-320dp...` puede ser inconsistente, pero confírmalo antes de renombrar.
- Manifiesto determinista con ID Stitch y AgendaQr, estado, tema, widthDp, density, fontScale, dimensiones del bitmap, test de origen, requerido/opcional y motivo de inclusión.
- Comprueba dimensiones reales contra viewport; falla si hay zoom/crop/upscale o metadatos falsos.
- Detecta duplicados byte-exactos y duplicados de misma pantalla/estado/configuración. No uses hashes como único criterio de calidad estética.
- Publica la galería como artefacto CI o salida bajo demanda, según la estrategia del repo; no contamines Git con imágenes temporales.

ADR-0007 actualmente prohíbe versionar PNG y hashes por variabilidad de Robolectric. No lo ignores: evalúa si las capturas Roborazzi reales son deterministas en el entorno elegido. Si conviene versionar referencias visuales, enmienda ADR-0007 con evidencia y un render estable; si no, genera artefactos CI bajo demanda y conserva aserciones deterministas. En cualquier caso, el objetivo del usuario de tener capturas útiles y no redundantes sigue vigente.

## Fase 7 — Validación

Inspecciona los comandos reales de `docs/QUALITY.md` y los tasks Gradle antes de ejecutarlos. Ejecuta las puertas apropiadas, por ejemplo:
```bash
./gradlew verifyAgendaQrArchitecture -PallowDebugSigningForRc=true
./gradlew :feature:destinations:presentation:testDebugUnitTest -PallowDebugSigningForRc=true
./gradlew :core:ui:testDebugUnitTest -PallowDebugSigningForRc=true
./gradlew :androidApp:testDebugUnitTest -PallowDebugSigningForRc=true
./gradlew :androidApp:assembleDebug -PallowDebugSigningForRc=true
./gradlew :androidApp:lint -PallowDebugSigningForRc=true
```
Usa solo tasks existentes. Ejecuta además pruebas específicas de navegación, contratos, componentes, accesibilidad y capturas. Inspecciona informes y arregla regresiones.

Verifica temas claro/oscuro, 320/360 dp, tablet, font scale 200 %, textos largos, lector de pantalla, targets, reduced motion, teclado/foco, gestos, back, restauración de estado y entradas externas. Distingue unit tests, Robolectric, instrumentados, build, compilación iOS y runtime real. Registra bloqueos de entorno sin fingir éxito.

## Forma de trabajo

- Empieza con el commit/estado de rama actual y un inventario de Stitch y del repo.
- No pares tras entregar un plan; implementa los cambios alcanzables.
- Trabaja en bloques grandes y coherentes, evitando microcambios repetitivos.
- No borres archivos, imágenes o tests antes de averiguar si son fuentes, fixtures o outputs.
- No alteres reglas de dominio por conveniencia visual.
- Cada divergencia de TDesign/Stitch debe tener evidencia, decisión y prueba.
- Actualiza los documentos generados con las tareas oficiales, no manualmente.
- Si un MCP no está accesible, registra qué se intentó y qué queda bloqueado; no inventes resultados.

## Criterios de aceptación

1. Proyecto Stitch `TDesign Screen Templates` inspeccionado y sus artefactos relevantes identificados con evidencia.
2. Inventario de pantallas Stitch ↔ AgendaQr con trazabilidad sin huecos silenciosos.
3. UX/UI, contrato normativo, ADR, contrato estructurado, código y tests reconciliados.
4. Cambios autorizados de intención, navegación, jerarquía y gestos documentados y probados.
5. Validador integrado a CI con pruebas negativas reales.
6. Galería de máxima cobertura única, sin zoom/crop/upscale ni flujos duplicados, con manifiesto verificado.
7. Gates ejecutadas y resultados concretos registrados; no verificado no se presenta como PASS.
8. Documentos históricos contradictores enmendados y enlaces/índice actualizados.
9. Riesgos y deuda restantes declarados sin llamar terminado a lo que no se haya validado.

## Entregables

- Cambios en rama/PR si las herramientas lo permiten.
- Matriz de auditoría con hallazgo, evidencia Stitch/código, decisión, cambios y pruebas.
- Fuente de contrato estructurado y documento humano generado.
- ADR enmendadas (especialmente ADR-0015, ADR-0023 y ADR-0007 según la evidencia).
- Implementación Compose de las pantallas afectadas y componentes canónicos.
- Gate extendido con pruebas negativas y CI.
- Manifiesto e índice de capturas sin redundancia.
- Informe final con commit/PR, comandos ejecutados, resultados reales, limitaciones MCP y deuda ordenada por riesgo.

Comienza ahora por inspeccionar el proyecto Google Stitch `TDesign Screen Templates` mediante el MCP conectado y el estado actual de AgendaQr. No respondas solamente con un plan.
