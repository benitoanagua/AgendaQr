# Xauxa Design System — contrato normativo

**Versión:** 2.0  
**Estado:** Normativo; expresa el diseño convencional sobre Material 3 aprobado por ADR-0012 (enmienda de ADR-0005/ADR-0011)  
**Ámbito:** Android e iOS; Compose Multiplatform. El laboratorio es un entorno de validación, no una fuente alternativa de diseño.

Este documento es la autoridad normativa para la expresión visual y el comportamiento transversal de Xauxa. No reemplaza la especificación funcional de Agenda QR: la intención, los flujos, las reglas de negocio y la recuperación de errores siguen gobernados por los documentos de producto. Xauxa define cómo se representan y operan visualmente.

## Estado de conformidad

La cobertura de contrato incluye campos deshabilitados, de solo lectura y actualización dinámica de errores.

La conformidad del sistema se registra por capas: (1) contrato/documentación, (2) gate estático, (3) pruebas automatizadas, (4) regresión visual y (5) validación en dispositivos. Un PASS de una capa no implica PASS de las siguientes. El estado de ejecución y los bloqueos vigentes se mantienen en los checklists de `docs/08-validacion/`.

## 1. Objetivo y principios no negociables

Xauxa es la capa de diseño de Agenda QR sobre componentes estándar de
Material 3, con el mismo diseño en Android e iOS. Debe leerse como una app
convencional y poco disruptiva; la identidad de marca vive en el color de
acento y poco más.

1. **Contenido antes que cromo.** Eliminar decoración que no comunique jerarquía, estado, agrupación o interacción.
2. **Componentes estándar.** Todo control interactivo es Material 3 o un envoltorio fino Xauxa con color, forma y tipografía del tema; no se mantienen mecánicas de interacción paralelas propias.
3. **El color tiene semántica.** Acento, estado y superficie son funciones distintas. El color nunca es la única señal de estado.
4. **Sin deriva de plataforma.** Composición, chrome y componentes idénticos en Android e iOS; solo la adquisición nativa (selectores, share) y las convenciones obligadas del sistema difieren.
5. **Una regla global, una implementación.** Un patrón visual compartido se expresa mediante tokens y componentes Xauxa, no mediante estilos ad hoc por pantalla.
6. **Accesibilidad como contrato.** Contraste, escalado, foco, semántica, targets y movimiento reducido son requisitos de aceptación, no refinamientos posteriores.

## 2. Jerarquía normativa y precedencia

En caso de conflicto, aplicar este orden:

1. Requisitos legales, seguridad y accesibilidad aplicable.
2. Reglas funcionales y de dominio de Agenda QR.
3. ADR aprobados.
4. Este contrato normativo Xauxa.
5. Especificación UX/UI de Agenda QR para intención, composición funcional y flujos.
6. Implementación actual y capturas históricas.

La implementación nunca justifica una excepción por sí misma. Una excepción requiere ADR con motivación, alcance, impacto, alternativa descartada, pruebas y criterio de retirada. Si dos documentos normativos discrepan, no se elige silenciosamente uno: se corrige la fuente menos específica y se registra el cambio.

## 3. Arquitectura del sistema

### 3.1 Capas

- **Primitivos privados:** valores físicos de color, dimensión, tipografía y tiempos.
- **Tokens semánticos:** roles como `background`, `surface`, `textPrimary`, `accent`, `danger`, `focusRing`, y el conjunto de formas `XauxaShapes`.
- **Componentes Xauxa:** envoltorios finos de los componentes M3; API pública, estados, semántica accesible y presentación.
- **Patrones:** composición reutilizable de componentes para tareas comunes.
- **Features:** composición de patrones y componentes; no inventan estilos.
- **Lab y tests:** catálogo ejecutable, pruebas de contrato, accesibilidad y regresión visual.

### 3.2 Fuentes únicas

`XauxaTokens.kt` es la fuente de verdad de tokens: color, espaciado,
métricas, tipografía, movimiento y el conjunto de formas
(`XauxaShapes`: extraSmall 4, small 8, medium 12, large 16, extraLarge 28 dp)
que `XauxaTheme.kt` entrega a `MaterialTheme.shapes`.
`XauxaTheme.kt` es la única capa que adapta dichos tokens a MaterialTheme.
Los componentes compartidos viven en `core:ui`; los features no definen
colores, radios, elevaciones, tipografía, alturas ni animaciones de forma
local. Los roles de compatibilidad Material 3 `secondary` y `tertiary`
reutilizan el único acento de marca Xauxa; sus contenedores son neutrales
y cambian con el tema. Los roles inversos usan neutros monocromos y un
acento legible, sin tonos morados heredados de la paleta predeterminada de
Material.

Los nombres exactos de tokens pueden evolucionar, pero cada rol visual debe tener una única definición semántica. No se permite mantener alias divergentes con valores distintos.

### 3.3 Prohibiciones

- No usar `MaterialTheme.colorScheme` como autoridad visual fuera de la adaptación Xauxa.
- No llamar componentes Material visuales directamente desde features cuando exista equivalente Xauxa.
- No introducir `RoundedCornerShape`, `CircleShape`, `RectangleShape`, colores hexadecimales, `dp`, `sp`, duraciones o curvas literales en features para resolver estilos. Las excepciones geométricas (círculos reales, recorte de medios) deben estar encapsuladas y justificadas.
- No añadir sets de iconos, fuentes ni dependencias visuales sin revisión de licencia y ADR.
- No cambiar la misma regla de forma distinta en Android e iOS salvo diferencia de plataforma documentada y probada.

La elevación decorativa no es un lenguaje; el uso de sombras es el que
Material 3 aplica por defecto en sus componentes (menús, diálogos), nunca
añadido ad hoc.

## 4. Fundamentos visuales

### 4.1 Geometría y formas

El conjunto de formas vive en tokens (`XauxaShapes`) y se consume a través
de `MaterialTheme.shapes`: ningún componente fija radios propios. Los
círculos reales (indicadores, avatares circulares) siguen siendo círculos
y son la excepción geométrica encapsulada.

### 4.2 Superficies, bordes y elevación

- Fondo de pantalla y superficies de contenido se diferencian por valores tonales y por los propios componentes M3.
- Los controles llevan el borde/indicador estándar de su componente M3; el reposo de un campo debe cumplir contraste no textual ≥ 3:1 (token `borderControl` en `OutlinedTextField`).
- Los separadores (`HorizontalDivider` o espacio) se usan cuando ayudan a interpretar grupos o filas.
- Diálogos y overlays usan los componentes M3 estándar con su manejo de foco y elevación.

### 4.3 Espaciado y densidad

La unidad base es **4 dp**. Tokens de espaciado y tamaño deben derivarse de esta escala salvo necesidades de plataforma o accesibilidad justificadas. Las reglas de densidad especifican mínimos, no alturas rígidas que recorten contenido. El layout debe poder crecer con texto, localización y escala de fuente.

### 4.4 Tipografía

- Familia del sistema en todos los estilos (Roboto en Android, San Francisco en iOS): sin fuente empaquetada propia.
- Los roles tipográficos semánticos se mapean a los estilos de Material 3 (`titleLarge`, `bodyLarge`, `labelLarge`, …) desde `XauxaTextStyles`/`XauxaType`, en `sp`.
- Se preserva la capitalización editorial de los textos. No transformar todas las etiquetas a mayúsculas desde un componente.
- El escalado de fuente del sistema debe funcionar al 100 %, 150 % y 200 % sin recortes, solapamientos ni pérdida de acciones esenciales. El mínimo canónico de target interactivo es `XauxaMetrics.ControlMinSize = 48.dp`; cualquier excepción debe justificarse y probarse por plataforma.

### 4.5 Iconografía

Lucide es el único set permitido, encapsulado por `XauxaIcon`. Los tamaños y tintes salen de tokens. Iconos decorativos se excluyen del árbol semántico; los iconos que realizan acciones siempre llevan descripción accesible. No se permite depender solo del glifo para comunicar un estado.

## 5. Color y contraste

### 5.1 Roles

El sistema separa:

- **Acento de marca/sistema:** acciones primarias, foco y énfasis de navegación.
- **Acento de contexto:** identidad visual derivada determinísticamente del ID del contexto; no se persiste en dominio.
- **Colores de estado:** error, advertencia, éxito e información.
- **Superficies:** fondo, superficie, contenedores tonales y superficies inversas.
- **Contenido:** texto primario/secundario, iconografía y contenido sobre acento.

Un color no se reutiliza con un significado diferente en un mismo contexto visual. Los acentos de contexto no sustituyen a los colores de estado.

### 5.2 Umbrales de aceptación

- Texto normal: contraste mínimo **4.5:1**.
- Texto grande: mínimo **3:1**.
- Límites de controles, indicadores de foco y gráficos necesarios para entender la interfaz: mínimo **3:1** respecto a colores adyacentes relevantes.
- Los estados deben incluir una señal adicional al color: texto, icono, forma, posición o semántica accesible.
- Todos los pares se calculan para tema claro y oscuro. No basta con probar el color contra blanco si también aparece sobre contenedores, overlays o estados deshabilitados.

El test automatizado debe cubrir pares reales de foreground/background, incluidos estados, botones, chips, banners, snackbar, inputs, foco y texto secundario. Un valor de contraste que pasa en un único componente no autoriza su uso universal.

**Acentos de contexto.** El par acento/texto de la paleta de contextos
cumple ≥ 4.5:1 (tabla de la spec §12). Los acentos se usan como bloques o
elementos de identidad (encabezados de sección en S06, elemento de
identidad en filas de contexto), siempre como texto legible (`asTextOn`)
o con su par de contenido, y nunca como única señal de estado.

### 5.3 Temas

Tema claro y oscuro son implementaciones del mismo contrato semántico. No se permite invertir colores mecánicamente si el resultado rompe contraste o jerarquía. El modo oscuro usa negro puro como fondo normativo; las superficies se distinguen por valores tonales y por los propios componentes M3.

## 6. Componentes y estados

Cada componente público debe documentar: propósito, cuándo usarlo, cuándo no usarlo, API/slots, tokens consumidos, estados, semántica, comportamiento de teclado/lector de pantalla, reglas responsive, ejemplos y pruebas requeridas.

Estados mínimos que correspondan a la función del componente: **reposo, presionado, foco, seleccionado, deshabilitado, cargando, éxito y error**. Los que el componente M3 subyacente ya expone se heredan de él; los no aplicables deben declararse, no omitirse por accidente.

### 6.1 Acciones

- **Primaria:** `Button`; una acción primaria dominante por superficie de intención.
- **Secundaria:** `OutlinedButton` o `TextButton`, con icono solo si aporta información.
- **Peligro:** tokens semánticos de danger y texto explícito; no se identifica solo por rojo.
- **Icon button:** `IconButton`/`IconToggleButton` con descripción accesible; el target táctil cumple el mínimo de plataforma.
- Los estados deshabilitado/ocupado se expresan también con etiqueta de texto.

### 6.2 Entradas

- `OutlinedTextField` con etiqueta, texto de apoyo y error contextual (icono + texto). El borde en reposo cumple ≥ 3:1 (`borderControl`).
- Teclado e IME según el tipo; autofill de credenciales; la obligatoriedad se expresa con texto/semántica accesible, no con asterisco decorativo.
- La ayuda, contador y error tienen orden semántico estable. Error no se comunica solo mediante borde; se expone también en la semántica del nodo editable (`SemanticsProperties.Error`).
- La búsqueda comparte el contrato de entrada y puede añadir affordances etiquetadas de borrar/filtrar.

### 6.3 Filas y datos

- Las filas usan `ListItem` (o el equivalente M3) con contenido que crece con texto y font scale; se prohíben alturas fijas que recorten etiquetas o targets.
- El acento de contexto puede aparecer como elemento de identidad del dato (p. ej. punto de acento en filas de contexto), nunca como barra ornamental; el estado añade texto/glifo semántico.
- Los datos destacados usan jerarquía tipográfica y alineación.

### 6.4 Feedback

- **Persistente:** banners tonales (`XauxaStatusBanner`) con liveRegion e icono por tono, dentro del Scaffold; los estados "Sin conexión"/"N pendientes" usan un indicador compacto, y el banner a ancho completo con acción "Reintentar ahora" solo con mutaciones FAILED.
- **Transitorio:** `Snackbar` del `SnackbarHostState` con acción opcional; conserva háptica y anuncio accesible por evento. Un error crítico o una decisión persistente no pueden depender solo del snackbar.
- Carga: indicadores estándar de M3 (`CircularProgressIndicator`/`LinearProgressIndicator`) o skeleton según la tarea.
- Reduced motion debe tener una representación estática informativa equivalente.
- Empty states y errores deben explicar qué ocurrió y la siguiente acción viable.

### 6.5 Diálogos y menús

Diálogos y hojas usan `AlertDialog`/`ModalBottomSheet` estándar. Se reservan para decisiones que necesitan interrupción o foco modal; no sustituyen banners/feedback inline. El foco se confina y restaura correctamente; la acción destructiva se identifica de forma explícita. Menús y selectores siguen los mismos tokens, tipografía, foco y contraste que el resto del sistema.

## 7. Interacción, foco y movimiento

### Indicación global

La indicación de pulsación es la **estándar de Material 3** (ripple del
tema). `XauxaTheme` NO desactiva el ripple; ningún componente usa
`clickable(indication = null)` ni overlays de pulsación propios.

- Cada control interactivo ofrece respuesta perceptible.
- El foco de teclado/lector de pantalla es visible y no se reemplaza por feedback de pulsación.
- Transición entre superficies: fundido con desplazamiento horizontal pequeño, corto, con duraciones/easing de `XauxaMotion`; instantánea con reduced motion (duración 0). El predictive back de Android 14+ usa el progreso del gesto.
- No se admiten loops decorativos.
- La duración, easing y distancia provienen de tokens; no hay números de animación locales en features.

## 8. Accesibilidad y adaptación

- Targets táctiles: mínimo 44 dp y preferencia por 48 dp donde la plataforma y la densidad lo permitan; si la geometría visual es menor, ampliar hit area sin solapamiento.
- Todo elemento tiene orden de lectura y foco lógico. No duplicar contenido decorativo en TalkBack/VoiceOver.
- Todos los campos tienen etiqueta; los errores se muestran de forma visible y se exponen también en la semántica del nodo editable. Requisitos y estados deben quedar identificables sin depender solo del color.
- Soportar localización, cadenas largas, texto dinámico, orientación y tamaños de pantalla; no truncar instrucciones, errores o acciones esenciales.
- Pruebas de layout: anchos compactos (320 y 360 dp) y font scale 100/150/200 %. Verificar ausencia de recorte/solapamiento y acceso a acciones.
- Validación manual en dispositivos: TalkBack, VoiceOver, navegación por teclado, contraste en exteriores/alto contraste cuando esté disponible y reduced motion.
- No usar color como único canal. Los destinos de navegación llevan siempre etiqueta visible.

## 9. Plataforma y comportamiento adaptable

Xauxa establece el contrato visual compartido; las diferencias de plataforma se limitan a convenciones necesarias de navegación, permisos, foco, sistema operativo y adquisición nativa (selectores, share). Una adaptación de plataforma debe mantener roles semánticos, estados, contraste, densidad y jerarquía. No se permite bifurcar la identidad completa por plataforma.

Los insets del sistema los gestiona el Scaffold (`contentWindowInsets`): la UI no se dibuja bajo barras de estado/navegación sin consumir los insets correctamente, y edge-to-edge se activa en Android con el estilo de barras acorde al tema. La adaptación de ancho usa `WindowSizeClass`: compacto usa `NavigationBar`; medium/expanded limita el contenido con `ContentMaxWidth` y usa `NavigationRail`. La navegación atrás y restauración de estado son contratos funcionales, no detalles cosméticos.

## 10. Pruebas y puertas de calidad

Un cambio de Xauxa no se considera terminado hasta cumplir las puertas aplicables:

1. **Compilación:** módulos afectados y targets soportados.
2. **Tests de tokens/contraste:** todos los pares reales y ambos temas.
3. **Tests de componente:** estados, semántica, acciones y reglas de API.
4. **Integridad del laboratorio:** catálogo completo y sin duplicados.
5. **Layout/accessibility:** font scale, tamaños compactos, targets, foco y semántica.
6. **Regresión visual:** capturas/hash manifest de referencias actualizadas deliberadamente; revisar claro/oscuro y estados principales.
7. **Gate de repositorio:** `verifyDesignSystemCompliance` y CI correspondiente.
8. **Revisión en dispositivo:** cuando cambian insets, cámara, foco nativo, navegación, gestos, tipografía o motion.

Si una puerta no puede ejecutarse por falta de red, SDK o dispositivo, el estado se registra como **NO VERIFICADO**, nunca como PASS. Un gate estático no sustituye compilación, tests ni revisión visual.

## 11. Gobierno del sistema y proceso de cambio

Todo cambio transversal sigue este orden:

1. Identificar regla existente y contradicciones en spec, tokens, componentes, laboratorio y consumidores.
2. Definir el principio y la regla normativa antes de editar componentes.
3. Actualizar este contrato o el documento de fundamento correspondiente y registrar ADR cuando cambie el comportamiento visual aprobado.
4. Cambiar tokens y componentes base antes que las pantallas.
5. Migrar todos los consumidores afectados; no dejar variantes temporales ni aplicar el cambio solo al caso que motivó la tarea.
6. Añadir tests/gates que detecten la regresión específica.
7. Actualizar catálogo, documentación de componentes, capturas y manifiestos.
8. Ejecutar las puertas de calidad y reportar resultados reales, incluidos los no ejecutados.
9. Mantener una tabla de excepciones con propietario, razón, alcance y condición de retirada. Si no existe entrada, no existe excepción válida.

### Criterio de finalización

El sistema está alineado cuando el mismo rol visual tiene la misma apariencia y estados en todas las superficies, los features no contienen estilos paralelos, los tests protegen los contratos y la documentación refleja el comportamiento implementado.

## 12. Registro de excepciones

Toda excepción visual tiene entrada en esta tabla con propietario, razón,
alcance, condición de retirada y prueba. Sin entrada, no existe excepción
válida. La lista de excepciones del gate (`verifyDesignSystemCompliance`)
DEBE coincidir con esta tabla.

| ID | Componente | Tipo | Token | Razón | Retirada | Prueba |
|---|---|---|---|---|---|---|
| E-01 | Menú "…" de la barra superior (`XauxaAppBar` hoy, `XauxaTopBar` tras la migración ADR-0012) | Borde funcional | `borderControl` | Límite del overlay del menú; el contenido flotante necesita límite visual ≥3:1 | Menú M3 sustituido por componente propio | XauxaSchemeTest: borderControl ≥3:1 |
| E-02 | `XauxaFavoriteIndicator` | Borde de control | `borderControl` | Límite del indicador de favorito (control visible ≥3:1) | Migración ADR-0012 (el indicador pasa a `IconToggleButton` estándar) | XauxaSchemeTest |
| E-03 | `XauxaScannerViewport` (y su encuadre `XauxaScannerFrame`) | Marco funcional | `brand` (BorderStrong) | Encuadre del scanner (S03 congelado: "marco con borde de marca"); guía visual activa de captura | S03 rediseñado | XauxaSchemeTest: brand ≥4,5:1 |
| E-05 | Identidad de contexto en filas | Elemento de identidad | acento de contexto | Identidad visual real del contexto derivada del ID (M4/M5 conservado); no es estado ni decoración; el nombre siempre es texto | Producto retira la identidad por contexto | Robolectric: fila con nombre accesible + contraste del par acento/texto |
| E-06 | `XauxaTextInput` / `XauxaSearchBar` | Borde de foco/error | `brand`/`danger` | Borde de 2 dp solo en foco/error del campo plano actual | Migración ADR-0012 (`OutlinedTextField` con indicador estándar ≥3:1) | XauxaAccessibilityContractTest |
| E-07 | AuthScreen sin barra de navegación | Excepción de chrome | — | La autenticación no es un destino raíz de sesión y su única acción legítima es iniciar/crear: el botón principal del cuerpo es la acción principal | Producto define navegación o acciones secundarias en Auth (nuevo ADR) | Round2ScreenshotTest login claro/oscuro (capturas de referencia) |


## Cobertura de auditoría

### Cobertura estática y responsive

El gate `verifyDesignSystemCompliance` recorre los source sets de producción de `core:ui` y mantiene la prohibición de Material 3 directo en `feature`, `shared/src` y `androidApp/src` (los componentes se adaptan en `core:ui` con tokens). El test responsive de 360 dp complementa la cobertura de 320 dp. El gate estático no acredita compilación ni ejecución de pruebas: la suite de CI las ejecuta en cada push.
