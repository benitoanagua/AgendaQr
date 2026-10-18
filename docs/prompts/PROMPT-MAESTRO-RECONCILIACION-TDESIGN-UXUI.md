# Prompt maestro — Reconciliación UX/UI, TDesign y verificación continua para AgendaQr

Actúa como un agente senior de ingeniería de producto, UX/UI, Android/Kotlin Multiplatform, Compose, diseño de interacción, accesibilidad, arquitectura y QA. Trabaja directamente sobre este repositorio:

https://github.com/benitoanagua/AgendaQr

## Objetivo obligatorio

Audita y reconcilia la especificación UX/UI, los contratos de diseño e interacción, los ADR, la navegación, la implementación de cada pantalla y sus pruebas para que todos describan y ejecuten el mismo producto. Construye controles automáticos para que cualquier desfase futuro sea detectado por CI con un error claro y accionable.

No hagas solo una auditoría ni entregues únicamente recomendaciones: realiza los cambios necesarios en una rama de trabajo, ejecuta las verificaciones disponibles y presenta un resultado verificable. No afirmes que una prueba pasó si no la ejecutaste.

Autorizo expresamente que el agente cambie intención de pantalla, navegación, jerarquía de acciones, back stack conceptual y gestos para que AgendaQr sea fiel a TDesign Mobile oficial, siempre que:
1. investigue la fuente oficial normativa ya fijada por ADR-0023 (tdesign_flutter@0.2.7) y valide si sigue siendo la versión que el repositorio quiere adoptar; no actualice versión por su cuenta sin documentar el impacto;
2. distinga patrones genuinos de TDesign de invenciones propias o diferencias de la plataforma;
3. preserve los límites de producto, privacidad, persistencia, semántica de los datos y reglas de negocio salvo conflicto real y decisión explícita documentada;
4. cualquier cambio de intención, navegación, back, gesto o jerarquía se propague a todos los artefactos normativos, código, accesibilidad y pruebas;
5. elimine normas antiguas que contradigan la nueva decisión en vez de acumular documentos paralelos o dejar afirmaciones obsoletas;
6. registre decisiones y excepciones concretas. No atribuya a TDesign algo que no aparezca en la fuente verificada.

## Contexto del repositorio observado

Es un proyecto Kotlin Multiplatform con Compose; estructura relevante:
- `feature/destinations/presentation`: pantallas y estado de presentación.
- `feature/destinations/domain`: modelo y reglas de negocio.
- `feature/destinations/data`: persistencia, acceso local y sincronización.
- `core/ui`: tokens, tema, componentes e iconografía de AgendaQr.
- `shared`, `androidApp`, `iosApp`: código compartido y hosts.
- `docs/04-ux/01-flujos-confirmados.md`
- `docs/04-ux/02-especificacion-ux-ui-v1.md`
- `docs/04-ux/04-capacidades-y-comportamientos.md`
- `docs/05-design-system/00-contrato-normativo-diseno.md`
- `docs/05-design-system/02-implementacion-diseno.md`
- `docs/07-decisiones/ADR-0007-capturas-de-referencia.md`
- `docs/07-decisiones/ADR-0023-adopcion-fiel-tdesign-mobile.md`
- `docs/STATE.md`, `docs/QUALITY.md`, `docs/ARCHITECTURE.md`
- `.github/workflows/android-validation.yml`

Ya existen gates como `verifyAgendaQrArchitecture`, `verifyDesignSystemCompliance`, `verifyDesignSystemFixtures`, `verifyControlMinSize`, `verifyArchitectureBoundaries`, `verifyDocs`, pruebas de accesibilidad, jerarquía, navegación, restauración de estado, UX, layout, temas y fuente escalada. Inspecciónalas antes de agregar otras para no duplicar mecanismos.

## Fase 1 — Auditoría basada en evidencia

1. Lee el README, el mapa de documentación, la especificación UX/UI completa, el contrato normativo completo, ADR-0023 y los ADR relacionados con navegación, identidad visual, gestos, layout y pantallas. Lee también implementaciones, strings, componentes compartidos, tests y workflows.
2. Construye un inventario real de pantallas, overlays, hojas, diálogos, estados, entradas externas, acciones, gestos, transiciones y superficies raíz. Derívalo del código y compáralo con la documentación: no asumas que el documento está completo.
3. Inspecciona los componentes y patrones de TDesign Mobile oficial para la versión normativa del repo. Para cada pantalla identifica el patrón TDesign aplicable, el comportamiento esperado, evidencia en la fuente oficial, comportamiento actual y diferencia.
4. Distingue diferencias visuales, funcionales, semánticas, de navegación, de accesibilidad y de persistencia. Marca cada hallazgo como contradicción, ausencia, duplicación, desviación justificada o deuda no reproducible.
5. Busca órdenes contradictorias, en especial las que afirman que la intención y navegación están congeladas o que no pueden incorporarse gestos. La autorización nueva permite revisar esas decisiones; no la ignores por obedecer literalmente reglas previas que están siendo revisadas.
6. Crea una matriz de trazabilidad antes de editar: requisito/pantalla/estado → fuente TDesign → decisión UX → contrato/ADR → implementación → prueba → comando de verificación.

No infieras comportamiento de TDesign solamente a partir del nombre de un componente, ejemplos de terceros o memoria. Usa la fuente oficial fijada. Documenta URL, versión, archivo/sección de origen y límites de la evidencia.

## Fase 2 — Establecer una fuente de verdad sin documentos divergentes

Propón e implementa un contrato de interacción único y explícito. Evita duplicar párrafos normativos en muchos documentos. Los demás documentos deben enlazar a la norma canónica o contener las responsabilidades particulares que les correspondan.

El contrato por pantalla debe describir al menos:
- ID estable y nombre de pantalla/superficie;
- intención y resultado esperado;
- punto de entrada, salidas y destinos válidos;
- estructura y componentes TDesign usados;
- acción primaria (si procede), acciones secundarias y destructivas;
- interacciones y gestos: superficie origen, umbral/condición, acción, resultado, accesibilidad alternativa y conflictos con scroll/navegación del sistema;
- modelo de estado: loading, vacío, error, contenido, selección, guardado y recuperación cuando apliquen;
- efectos de back/cancelar y preservación o descarte de drafts;
- semantics/labels, targets accesibles, foco/teclado, lector de pantalla, reduced motion y font scale;
- adaptación teléfono/tablet y limitaciones de plataforma;
- contrato de datos y efectos persistentes, sin duplicar lógica de dominio;
- ADRs de fundamento y pruebas ejecutables que lo verifican.

Los contratos deben ser específicos y falsables. Evita expresiones vagas como “debería verse bien” o “comportamiento estándar”. Define condiciones observables.

Revisa contradicciones entre especificación funcional, contrato de diseño, ADR y producto. Si TDesign motiva un cambio:
- actualiza la especificación UX/UI y los diagramas/listas de flujos;
- enmienda o sustituye ADR obsoletos y deja la relación histórica clara;
- actualiza el contrato normativo y excepciones;
- actualiza la matriz de pantallas, strings y navegación;
- actualiza el código, pruebas, capturas/manifiesto y CI;
- actualiza documentación de estado y calidad sin declarar validado lo no ejecutado.

No cambies reglas de negocio sin justificación y evidencia. En particular, respeta el modelo local-first, el perfil sin cuenta obligatoria, la privacidad del PIN, la distinción guardado/sincronizado, la no ejecución/verificación de pagos, la posibilidad de operación sin contexto, comprobante sin QR y asociación reversible, salvo que una contradicción explícita obligue a elevar una decisión de producto; nunca resuelvas esa contradicción escondiéndola en el código.

## Fase 3 — Fidelidad TDesign por pantalla

Inspecciona una por una todas las pantallas reales, incluidas superficies pequeñas como menús, bottom sheets/dialogs, pickers, estados vacíos, errores y pantallas de retorno. No te detengas en Inicio.

Para cada pantalla:
1. compara jerarquía, densidad, espaciado, tipografía, colores/roles semánticos, forma, iconografía, controles, estados, feedback, transición y gesto con TDesign;
2. comprueba que usa APIs de `core/ui` y tokens canónicos, no literales ni controles Material prefabricados fuera de la capa permitida;
3. si el patrón de navegación actual contradice el modelo de interacción TDesign, rediseña la navegación y ajusta todos sus consumidores;
4. introduce solo gestos demostrablemente apropiados y con affordance, alternativa accesible y sin conflictos con gestos del SO. No agregues gestos ocultos sin descubribilidad;
5. conserva experiencia Android/iOS coherente, con excepciones nativas explícitas;
6. evita mejoras estéticas aisladas que causen diferencias entre pantallas equivalentes;
7. elimina código muerto, implementaciones paralelas, excepciones y tests obsoletos derivados del cambio.

No reemplaces arquitectura de dominio por componentes visuales. Las pantallas deben orquestar casos de uso existentes y no convertirse en nuevas fuentes de reglas de negocio.

## Fase 4 — Contrato ejecutable y detección automática de desfases

Implementa una comprobación ejecutable e integrada a `verifyAgendaQrArchitecture` y al workflow de CI. Extiende los gates existentes siempre que sea más simple y robusto que crear un sistema paralelo.

La verificación debe detectar como mínimo:
- pantalla real sin ID/contrato documental;
- pantalla documentada que ya no existe o no tiene implementación;
- acción/gesto/transición documentados pero ausentes en implementación o pruebas;
- destino de navegación que no existe o transición que contradice el grafo contractual;
- intención o jerarquía de acciones que diverge entre UX/UI, ADR, implementación y pruebas;
- estado observable que no se cubre donde es contractualmente necesario;
- strings de interfaz divergentes de sus contratos o hardcodeados donde el gate exige AppStrings;
- excepción al sistema de diseño usada en código pero no registrada en el contrato, o excepción documentada que ya no se utiliza;
- tokens o controles no permitidos fuera de `core/ui`;
- una pantalla sin al menos una verificación significativa de semántica/accesibilidad;
- un escenario visual requerido que no se ejecutó o no está registrado;
- artefactos de captura sin metadatos, duplicados o generados bajo una configuración distinta de la declarada;
- requisitos modificados sin actualizar la matriz de trazabilidad.

El contrato idealmente debe ser datos estructurados y tipados (por ejemplo YAML/JSON validado o Kotlin DSL con IDs estables); no crees un parser frágil basado únicamente en expresiones regulares de prosa si un contrato estructurado puede resolverlo. Conserva la documentación legible para personas y valida que corresponde a esa fuente estructurada.

Incluye pruebas del propio validador:
- configuración válida pasa;
- pantalla sin contrato falla;
- destino/acción no existente falla;
- referencia de prueba inexistente falla;
- excepción visual sin registro falla;
- escenario requerido de captura ausente falla;
- duplicado de manifiesto se detecta;
- diagnóstico incluye ID, archivo/línea cuando se pueda y acción correctiva.

No aceptes un verificador que siempre pase, solo examine nombres o valide un espejo que nadie consume. Asegura que las pruebas están ligadas a señales reales de código o a IDs tipados usados por implementación/tests. Usa allowlist de excepciones pequeña, explícita, justificada, con owner y fecha de revisión si corresponde. Sin bypass silencioso.

Si la trazabilidad completa no puede automatizarse de inmediato, implementa un MVP útil en fases y declara honestamente qué tipos de desfase detecta y qué queda bajo revisión humana. No lo describas como “cualquier desfase” hasta demostrar esa cobertura.

## Fase 5 — Capturas Robolectric/Robolectric-Rendering: sin zoom, sin repetir flujos

Directorio de interés que reporta el usuario:
`feature/destinations/presentation/build/roborazzi/`
Ejemplos de nombres:
- `account-dark-320dp-font-200.png`
- `account-dark-320dp.png`
- `account-dark-font-200.png`
- `account-dark.png`
- `account-light-320dp-font-200.png`
- `tablet-s01-light-320dp-font-200.png`
- `tablet-s01-light-320dp.png`
- `tablet-s01-light-font-200.png`
- `tablet-s01-light.png`

Primero inspecciona el pipeline real de Roborazzi y el directorio de salida. No supongas que esos ejemplos representan todos los archivos. No muevas ni borres recursos existentes hasta inventariarlos y determinar si son outputs temporales, fixtures o recursos de referencia.

Objetivo de la galería final: la mayor cobertura informativa posible de pantallas y estados distintos con el menor número de capturas visualmente redundantes.

Reglas absolutas:
- No generar imágenes ampliadas por zoom, upscale, crop posterior o transformaciones para fingir un viewport distinto.
- Capturar cada pantalla en tamaño y densidad nativos del escenario configurado, conservando toda su ventana. No recortar contenido para que “entre”.
- No repetir flujos. Dos capturas que muestran la misma pantalla, estado y configuración no se cuentan como cobertura adicional.
- No generar todas las combinaciones indiscriminadamente para la galería resumida; seleccionar combinaciones informativas. Mantener pruebas automatizadas adicionales allí donde sean necesarias para asegurar accesibilidad y adaptabilidad.
- Deben estar representadas todas las pantallas y superficies de interacción relevantes, no solo las principales; cubrir overlays/diálogos si tienen contrato propio.
- Las variantes de estado se incluyen solo cuando demuestran un comportamiento visual o funcional distinto: inicial/vacío, datos, error/recuperación, loading, selección o confirmación destructiva cuando corresponda.
- Incluir al menos las categorías requeridas por contrato: claro/oscuro, dispositivo compacto de 320 dp, ancho estándar de 360 dp (o los escenarios oficiales reales del proyecto), tablet con NavigationRail si el diseño lo contempla y fuente al 200 %.
- No etiquetar una captura “tablet” si el escenario o los metadatos no lo prueban. Verifica nombres extraños como `tablet-s01-light-320dp.png`: pueden estar mal etiquetados, pero confirma antes de cambiar.
- Mantener los nombres coherentes con un esquema documentado y determinista, por ejemplo `<screen-id>--<state>--<device-width>--<theme>--<font-scale>.png`, adaptado a convenciones existentes sin dejar nombres ambiguos.
- Producir un manifiesto de capturas (JSON/YAML) con ID de pantalla, estado, tema, widthDp, density, fontScale, tamaño real del bitmap, identificador del test y motivo de inclusión; declarar obligatorios y opcionales.
- Verificar dimensiones de la imagen contra el escenario de render. La prueba debe fallar si se aplica zoom/upscale/crop/viewport falso en la cadena de generación.
- Medir redundancia de forma reproducible: duplicados byte-exactos y duplicados de mismo ID/configuración; no usar solo hashes como prueba de calidad estética.
- Crear una galería/índice breve que priorice cobertura única. El manifiesto completo puede contener más escenarios de QA que la galería resumida.
- No afirmar que una imagen fue creada o inspeccionada si no existió el archivo y no se verificó.

Como el ADR-0007 actual prohíbe versionar PNG y hashes debido a la variabilidad de Robolectric, no rompas esa decisión sin medir la estabilidad real del entorno. Si se elige versionar imágenes de referencia, justifica el cambio del ADR y asegúrate de tener render determinista; si no, usa artefactos de CI fuera del control de versiones y mantiene verificaciones de layout por aserciones. La galería debe poder generarse bajo demanda y/o publicarse como artefacto CI sin contaminar el repositorio con imágenes temporales.

## Fase 6 — Validación funcional, visual y de accesibilidad

- Ejecuta las gates de arquitectura/diseño y pruebas relacionadas; inspecciona outputs y arregla regresiones.
- Amplía los tests de navegación para verificar el grafo real, back stack, deep/external entry y preservación de drafts.
- Prueba las acciones primarias y secundarias de cada pantalla, los gestos agregados y sus alternativas accesibles.
- Verifica temas claro/oscuro, anchuras compactas, tablet, font scale 200 %, textos largos, RTL si el producto lo soporta, lector de pantalla, teclado/D-pad y reduced motion donde aplique.
- Comprueba target size y contraste con el contrato del proyecto; no rebajes los mínimos por similitud visual.
- Distingue unit tests, Robolectric, instrumentados, build Android, compilación iOS y validación runtime de dispositivo. Reporta el alcance real de cada ejecución.
- No hagas depender un gate estable de CI de Xcode local, secretos remotos opcionales, internet o servicios externos no aislados, salvo cuando sea una prueba diseñada explícitamente para eso.
- No actualices toolchain ni dependencias ajenas al objetivo sin una justificación y pruebas que lo requieran.

Comandos conocidos en README/QUALITY (ajusta según tasks existentes y usa el parámetro que requiere el guard de firma):
```bash
./gradlew verifyAgendaQrArchitecture -PallowDebugSigningForRc=true
./gradlew :feature:destinations:presentation:testDebugUnitTest -PallowDebugSigningForRc=true
./gradlew :core:ui:testDebugUnitTest -PallowDebugSigningForRc=true
./gradlew :androidApp:testDebugUnitTest -PallowDebugSigningForRc=true
./gradlew :androidApp:assembleDebug -PallowDebugSigningForRc=true
./gradlew :androidApp:lint -PallowDebugSigningForRc=true
```
Usa nombres de task que de verdad existan. Si un comando falla por entorno o tiempo, registra el mensaje, no lo camufles como éxito. Ejecuta además los módulos o pruebas específicas que la matriz señale.

## Fase 7 — Forma de trabajo

- Empieza con una rama de trabajo y un resumen corto de los hallazgos de mayor riesgo.
- Trabaja en pasos coherentes, pero no pares tras entregar un plan.
- Haz los cambios estructurales antes de pulir componentes repetidos.
- Aprovecha componentes y gates existentes; evita duplicación.
- No hagas refactors masivos que no contribuyan al objetivo.
- No sobrescribas cambios preexistentes del usuario ni borres artefactos sin revisar su origen.
- Toda desviación respecto de TDesign debe tener motivo, alcance, ubicación canónica y test.
- Todo cambio de intención/navegación/jerarquía debe actualizar contrato, documentación, ADRs, código y pruebas en el mismo cambio lógico.
- Añade una prueba de regresión para cada contradicción corregida.
- Si la especificación existente era incorrecta, corrígela deliberadamente; no modifiques la implementación para que pase un test obsoleto ni el test para esconder una regresión.
- Mantén coherencia de idioma y naming con las convenciones del repo.

## Criterios de aceptación — no declarar listo sin ellos

1. Existe inventario de todas las pantallas/superficies y matriz de trazabilidad sin huecos ocultos.
2. Cada pantalla tiene contrato funcional/interactivo y pruebas trazables.
3. UX/UI, contrato normativo, ADR, código y pruebas no se contradicen en los aspectos incluidos en el contrato.
4. Los cambios autorizados a intención, navegación, jerarquía o gestos se justifican en TDesign y están propagados a todos los artefactos.
5. Las excepciones de TDesign están centralizadas, justificadas y probadas.
6. Un validador probado con casos negativos detecta al menos las clases de divergencia declaradas y se ejecuta automáticamente en CI.
7. La galería de capturas maximiza pantallas/estados únicos, sin zoom, crop, upscale ni repetición de flujos; los escenarios de QA restantes están en el manifiesto.
8. Las pruebas de accesibilidad, layout, tema, escala tipográfica y navegación ejecutadas quedan registradas con resultados reales.
9. La documentación de estado distingue verde/rojo/bloqueado/no ejecutado y no confunde compilación con validación runtime.
10. No se dejan instrucciones normativas viejas que contradigan la decisión nueva.

## Entregables finales

- Implementación actualizada en rama y propuesta de PR si las herramientas lo permiten.
- Auditoría breve de contradicciones iniciales y resolución aplicada.
- Fuente canónica del contrato de pantallas/flows y matriz de trazabilidad.
- ADR nuevo o enmiendas a ADR relevantes, con explicación de la precedencia actualizada.
- Validador de consistencia + pruebas negativas + integración CI.
- Manifiesto e índice de capturas seleccionadas, junto con artefactos generados donde corresponda.
- Resumen de pruebas con comandos ejecutados, resultados concretos y limitaciones.
- Lista de deuda restante ordenada por riesgo, sin llamar “completo” a aquello que no se haya verificado.

Empieza ya explorando el repositorio y ejecutando la auditoría. No respondas solo con una propuesta: realiza todo lo que pueda completarse con evidencia y herramientas disponibles.
