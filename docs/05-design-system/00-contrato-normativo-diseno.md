# Contrato normativo de diseño

**Estado:** Normativo — versión 9.0
**Autoridad:** [ADR-0025](../07-decisiones/ADR-0025-adopcion-ui-ux-polaris.md),
[ADR-0026](../07-decisiones/ADR-0026-cierre-migracion-polaris-core-ui.md),
[ADR-0027](../07-decisiones/ADR-0027-propiedad-espacio-nivel-atomico.md),
[ADR-0028](../07-decisiones/ADR-0028-moon-patrones-moviles-polaris-primario.md),
[ADR-0029](../07-decisiones/ADR-0029-cadena-precedencia-polaris-moon-nativo.md) y este
documento. La especificación UX/UI V1 conserva el contrato funcional
congelado; su dirección visual queda descongelada y se rige por aquí.

## 1. Fuente de valores y versión

| Concepto | Valor |
|---|---|
| Sistema | Polaris (reimplementación en Kotlin/Compose, sin runtime web) |
| Fuente primaria | Documentación oficial vigente (`polaris.shopify.com`, `shopify.dev`) |
| Fuente verificable | `@shopify/polaris-tokens@9.4.2` |
| Fuente iconográfica verificable | `@shopify/polaris-icons@9.3.1` (`dist/svg/*Icon.svg`) |
| Fecha de captura | 2026-11-17 |
| Instantánea | [`09-instantanea-tokens-polaris-9.4.2.md`](09-instantanea-tokens-polaris-9.4.2.md) |
| Temas publicados | `light`, `light-mobile`, `light-high-contrast-experimental`, `dark-experimental` |
| Tipografía | Inter (SIL OFL 1.1) |

La versión 12 citada originalmente corresponde a `@shopify/polaris`
(componentes React), no al paquete de tokens, que llega hasta 9.4.2. Se fija
9.4.2 por ser la versión publicada verificable más reciente.

**Pendiente de verificar**

| ID | Valor | Acción |
|---|---|---|
| PV-01 | Numeración «v12» de la fuente de tokens | Confirmar si existe una distribución interna; mientras tanto usar 9.4.2. |
| PV-02 | Cobertura completa del tema oscuro | `dark-experimental` no completa todos los roles; derivar desde la capa semántica y marcar como extensión. |
| PV-03 | Equivalente móvil oficial de `rem` | Polaris publica px a 16px de root; se adopta 1rem = 16dp como desviación documentada (D-01). |
| PV-04 | Roles de movimiento semánticos | Polaris publica duraciones y easings primitivos; los roles de producto se derivan y se marcan como extensión. |
| PV-05 | Escala táctil | Polaris no especifica objetivos de 48dp; se toma el mínimo accesible de plataforma (D-02). |
| PV-06 | Enlace, foco y énfasis en `dark-experimental` | El tema oscuro no publica `text-link`, `border-focus` ni roles `emphasis`. El valor heredado `#005BD3` no alcanza 3:1 sobre las superficies oscuras; se usa el verificable `#91D0FF` publicado en `:121` (X-05), que sí cumple AA. |
| PV-07 | Texto sobre `avatar-one-bg-fill` | El par publicado `#FDEFFD` sobre `#C530C5` (`:255-256`) queda ≈4.09:1, por debajo de AA. La identidad Magenta conserva el relleno publicado y usa blanco (`#FFFFFF`) para el texto; el par publicado queda documentado pero no se aplica. |
| PV-08 | Interlineado de tamaños arbitrarios | Los roles `text-*` publican pares canónicos. Si un componente recibe un `sp` fuera de esa lista, se aplica el ratio derivado de `heading-2xl` (40/32 = 1,25) o `body-md` (24/16 = 1,5). Ningún tamaño canónico usa el ratio: todos conservan su `lineHeight` publicado. |

## 2. Reglas de uso

1. **Un solo origen.** Todo color, alfa, espaciado, radio, tipografía,
   proporción de composición, elevación y duración se declara en la capa de
   tokens de `core/ui`. Los componentes consumen **únicamente tokens
   semánticos**.
2. **Capas.** La capa primitiva reproduce las escalas de la instantánea. La
   capa semántica expone roles: `surface`, `text`, `border`, `action` y tonos
   `info`, `success`, `warning`, `critical`. Ningún componente referencia un
   token primitivo.
3. **Sin valores propios.** Si un valor no es verificable, se usa el más
   cercano de la misma versión, se registra en la tabla anterior y continúa.
   No se estima ni se inventa.
4. **Puente interno.** `MaterialTheme` solo adapta tipos; no aporta ningún
   valor visual. Está prohibido fuera del adaptador.
5. **Inter se empaqueta** como recurso multiplataforma con su aviso de
   licencia. Los roles tipográficos usan los tamaños y pesos de Polaris y
   escalan con la fuente del sistema hasta 200 %.
6. **Contraste AA.** Cada par texto/fondo usado por un componente se prueba a
   4.5:1 como mínimo (3:1 para elementos no textuales).
7. **Movimiento.** Se usan las duraciones y curvas de la instantánea; «reducir
   movimiento» elimina la transición.
8. **Sin marca Shopify.** Ni nombre, logo ni marca de Shopify en app, UI o
   documentación de producto. Las referencias técnicas solo aparecen en
   documentación de ingeniería.

### Roles semánticos de partida (valores verificados)

| Rol semántico | Token Polaris | Valor (tema claro) |
|---|---|---|
| Fondo de página | `--p-color-bg` | `rgba(241, 241, 241, 1)` |
| Superficie | `--p-color-bg-surface` | `rgba(255, 255, 255, 1)` |
| Texto primario | `--p-color-text` | `rgba(48, 48, 48, 1)` |
| Texto secundario | `--p-color-text-secondary` | `rgba(97, 97, 97, 1)` |
| Borde | `--p-color-border` | `rgba(227, 227, 227, 1)` |
| Foco | `--p-color-border-focus` | `rgba(0, 91, 211, 1)` |
| Acción rellena | `--p-color-bg-fill-brand` | `rgba(48, 48, 48, 1)` |
| Texto sobre acción | `--p-color-text-brand-on-bg-fill` | `rgba(255, 255, 255, 1)` |
| Crítico | `--p-color-bg-fill-critical` | `rgba(199, 10, 36, 1)` |
| Éxito | `--p-color-bg-fill-success` | `rgba(4, 123, 93, 1)` |
| Advertencia | `--p-color-bg-fill-warning` | `rgba(255, 184, 0, 1)` |
| Información | `--p-color-bg-fill-info` | `rgba(145, 208, 255, 1)` |

Tema oscuro publicado (`dark-experimental`): fondo
`rgba(26, 26, 26, 1)`, superficie `rgba(48, 48, 48, 1)` y texto
`rgba(227, 227, 227, 1)`. El resto se deriva según PV-02.

### Jerarquía de selección, alertas, formularios y acciones (8.4)

Reglas únicas del sistema; una pantalla nunca crea un patrón paralelo.

**Selección — tabs vs segmentado vs chips.** Un solo lenguaje visual de
selección: pista `surfaceSecondary` + píldora activa + badge de conteo.
- `AqTabs` = navegación entre vistas hermanas de una misma superficie
  (QR · Actividades · Comprobantes). Semántica `Role.Tab` + `selected`,
  fila desplazable, objetivo ≥48 dp. El conteo vive en `AqTab.count`,
  nunca como texto suelto bajo la barra.
- `AqSegmentedControl` = elección excluyente dentro de un formulario o
  filtro (Pago/Cobro, Todos/Favoritos). Semántica de radio.
- `AqChipGroup` = conjunto compacto de opciones o atajos (moneda, Hoy/Ayer,
  sugerencias). La selección no depende solo del color: check + relleno de
  acento + borde reforzado.

**Alertas.** `AqStatusBanner` = estado persistente/accionable bajo la barra
superior (vía `AppStatusHost` o local). `AqSnackbarHost`/`AqFeedbackEvent` =
confirmaciones transitorias. Los indicadores offline/pendientes compactos
usan badge/píldora, no banner completo. En el banner, el icono leading es
la semántica del tono (`AqTone.iconVector`, distinto por variante) y el
cierre es `AqIcons.Close` (X simple, acción ≥48 dp con descripción), nunca
el mismo glifo del estado.

**Formularios.** Jerarquía fija: 1) campo hero (`AqAmountField`, tipografía
`Amount` + símbolo de moneda); 2) secundarios en `AqFormSection` con
encabezado ÚNICO — el título nombra el grupo y nunca se repite como label
de campo; 3) opcionales con UN marcador discreto (`optionalLabel`, p. ej.
"Opcional"), prohibido "(opcional)" dentro de la etiqueta; 4) acción
primaria fija y dominante (`AqBottomAction`). Campos compuestos usan
`AqFormField` (label + opcional + ayuda/error). Los atajos de fecha
(Hoy/Ayer) son `AqChipGroup` acoplados al campo y reflejan el día elegido.
"Otra…" muestra el valor elegido en la etiqueta del chip, sin duplicar el
label del grupo.

**Acciones.** Una acción primaria por pantalla. Jerarquía:
`AqPrimaryButton` > `AqSecondaryButton`/`AqLightButton`/`AqOutlineButton` >
`AqTextAction` > destructiva (`AqDangerButton` o `AqTextActionVariant.Danger`)
que exige `AqDialog(destructive = true)` cuando es irreversible. En la barra
superior caben máximo 2 acciones visibles + overflow: visibles las
contextuales, en overflow las destructivas o infrecuentes, y nunca la misma
acción en ambos sitios. En el detalle, la acción secundaria vive dentro de
su sección y la destructiva va separada al final (o en overflow según el
modelo de acciones de la pantalla), con una sola vía de ejecución.

**Filas clave-valor.** Los metadatos de detalle usan `AqMetaRow`
(etiqueta/valor con icono opcional); el monto y el tipo conservan la
jerarquía hero (cifra grande + `AqTonalTag`).

## 3. Extensiones del producto

Construidas solo con tokens y componentes de Polaris; no son valores propios.

| ID | Extensión | Regla |
|---|---|---|
| X-01 | Insignia de evidencia | Icono + texto «Comprobante recibido». Nunca «Verificado» ni «Pagado». No implica dinero verificado. |
| X-02 | Indicador de sincronización | Estados local, pendiente, sincronizado y error con reintento. Discreto y no bloqueante. |
| X-03 | Mostrar QR | Negro sobre blanco con zona de silencio en cualquier tema, subida de brillo y acceso en máximo 2 toques. |
| X-04 | Roles de movimiento derivados | Derivados de las duraciones primitivas publicadas (PV-04). |
| X-05 | Tema oscuro completado | Roles no publicados derivados de la capa semántica oficial (PV-02). |
| X-06 | Acentos de contexto | La asignación determinista por dominio se conserva; los pares se seleccionan entre los colores verificados de la instantánea y se validan a AA. No son valores visuales propios. |
| X-07 | Iconografía de producto sobre Polaris Icons | El conjunto se fija en `@shopify/polaris-icons@9.3.1`; 37 SVGs de `dist/svg` (20×20) se conservan como constantes Kotlin y se exponen mediante 41 nombres de producto. Los conceptos sin glifo oficial usan el verificable más cercano: `QrCode`→`ShopcodesIcon`, `SearchX`/`AlertCircle`→`AlertCircleIcon`, `Inbox`→`ArchiveIcon`, `ImageOff`→`ImageAltIcon`, `ScanLine`→`BarcodeIcon`, `Income`→`ArrowDownIcon` y `Expense`→`ArrowUpIcon`. `Edit` y `Trash` son alias de `EditIcon` y `DeleteIcon`, no glifos nuevos. `Close`→`XIcon` (cierre, acción) es distinto de `Cancel`→`XCircleIcon` (estado); `Check`→`CheckIcon` es el indicador de selección de chips; `Erase`→`BackspaceIcon` borra texto, nunca el glifo de papelera. |

## 4. Desviaciones móviles

| ID | Desviación | Justificación |
|---|---|---|
| D-01 | `1rem` se interpreta como `16dp` | Polaris expresa espaciado en rem para navegador; Compose usa dp. |
| D-02 | Targets táctiles ≥ 48dp | Polaris es un sistema web sin requisito táctil; accesibilidad de Android/iOS. |
| D-03 | Barra inferior y gestos nativos | Navegación del sistema, no componente web. |
| D-04 | Bottom sheet nativo | Polaris web no define el patrón táctil equivalente. |
| D-05 | Inter empaquetado | El sistema web usa fallback de fuentes; la app debe ser multiplataforma y consistente. |
| D-06 | Escalado de fuente del sistema hasta 200 % | Requisito de accesibilidad móvil. La capacidad se conserva en producción; la verificación automática cubre 100 %/150 % y el 200 % queda como revisión manual (ADR-0007). |
| D-07 | Derivación del tema oscuro | `dark-experimental` no cubre todos los roles (PV-02). |
| D-08 | Proporciones internas de composición | Alfas de selección y fracciones de ancho/glifo/desplazamiento no son escalas publicadas; se concentran en `AqTokens.kt` y se conservan para no alterar el layout verificado. |
| D-10 | Modelo de sombras de Compose | `Modifier.shadow` expone elevación y color, pero no `offsetY` ni `spread`. `AqElevation` conserva la geometría publicada (`:363-368`) como metadato y aplica el radio de desenfoque; `shadow-100` se anula en `light-mobile` (`:497`). |
| D-11 | Contenedores tonales del tema oscuro | `dark-experimental` no publica contenedores de estado. Se derivan mezclando cada `*Fill` publicado al 16 % sobre `gray800` (la superficie oscura publicada), de modo que el bloque tonal se distinga de `surface` y conserve el par de contenido en AA. |
| D-12 | Ritmo de 16 dp entre tarjetas de página | `light-mobile` publica `space-card-gap` de 8 dp (`:511`). Con 16 dp de relleno interno, un hueco de 8 dp entre tarjetas lee como un solo bloque; se usa 16 dp para conservar la separación de secciones a 360 dp. `AqScreenScaffold` aplica ese mismo paso de 16 dp por defecto entre organismos. |

## 5. Gobernanza

- Todo cambio visual se implementa en `core/ui`; las pantallas solo componen.
- El gate `verifyAgendaQrArchitecture` falla si aparecen literales visuales
  fuera de tokens, si un componente usa tokens primitivos o si reaparece una
  referencia a un sistema de diseño eliminado.
- Las pruebas de componentes y pantallas cubren claro/oscuro y fuente al 100 %
  y 150 %, contraste AA, tamaño táctil y semántica de accesibilidad. El
  escalado al 200 % se conserva en producción, pero no tiene verificación
  automática (ADR-0007).
- Los valores y su procedencia se consolidan en la instantánea; la historia
  de decisiones vive en los ADR.

### Registro de excepciones del gate

Excepciones de implementación vigentes: el gate y esta tabla deben coincidir.
No autorizan valores visuales nuevos: solo permiten invocar la API indicada
en el archivo señalado con valores tomados de tokens semánticos.

| ID | API permitida | Archivo | Motivo |
|---|---|---|---|
| E-01 | `border` | `AqButtons.kt` | Borde de foco y variante contorneada |
| E-02 | `border` | `AqLists.kt` | Borde de tarjetas y filas |
| E-03 | `border` | `AqLists.kt` | Separador interno de tarjetas |
| E-04 | `border` | `AqInputs.kt` | Foco y estado de error de campos |
| E-05 | `shadow` | `AqLists.kt` | Elevación de tarjetas mediante `aqElevation` |
| E-07 | `border` | `AqForms.kt` | Caja del campo select (reposo, foco y error) |
| E-08 | `border` | `AqPinEntry.kt` | Borde de tecla del teclado PIN (contraste en tema oscuro) |
| E-09 | `border` | `AqTabs.kt` | Borde de la píldora activa (mismo lenguaje que el segmentado) |

E-06 (elevación de botones) está retirada: los botones son planos y un
registro sin uso se detecta como excepción zombie.

## 6. Referencia de patrones móviles (Moon)

[ADR-0028](../07-decisiones/ADR-0028-moon-patrones-moviles-polaris-primario.md)
fija que **Polaris es la única autoridad visual** de Agenda QR (tokens,
tipografía, apariencia) y que Moon Design System se usa **solo como
referencia de patrones de interacción móvil**: accordion en S07
(`AqExpansionPanel`), chips de filtro por tipo en S04 y S12
(`AqChipGroup`), feedback de error en `AqPinDots`. No hay dependencia
Flutter ni `moon_design`, ni tokens Moon; la API pública es `Aq*`.
La clasificación completa patrón a patrón vive en
[`11-matriz-gaps-mobile.md`](11-matriz-gaps-mobile.md).

### Cadena de precedencia para patrones sin equivalente Polaris (9.0, ADR-0029)

Polaris sigue siendo la única autoridad visual. Cuando un patrón móvil no
tiene respuesta en Polaris, la decisión baja por esta cadena y solo un
nivel si el anterior está descartado por escrito:

1. **Polaris** — convenciones, tokens, componentes y patrones. Si solo hay
   guía de escritorio, se anota «Polaris sin guía mobile» y se adapta con
   sus tokens, densidad y jerarquía.
2. **Moon Design** (`moon_design` 1.1.0) — solo como referencia de
   anatomía, estados, tamaños y gestos. El estilo se reemplaza al 100 %
   por tokens Polaris; no hay dependencia Flutter ni `moon_design`.
3. **Nativo** Android (Material 3) / iOS (HIG) — solo si los niveles 1 y 2
   están descartados; se estiliza con tokens Polaris.

La navegación raíz es nativa por esta cadena (Polaris se consulta primero y no define navegación
táctil móvil; Moon se consulta segundo y no trae bottom navigation/FAB/search/swipe/refresh/
pickers/skeleton/empty state/list cell), nunca por omisión directa. La evidencia
patrón a patrón está en
[`11-matriz-gaps-mobile.md`](11-matriz-gaps-mobile.md) y la fidelidad de la
capa visual en
[`10-auditoria-fidelidad.md`](10-auditoria-fidelidad.md).

## 7. Registro de versiones

| Versión | Cambio |
|---|---|
| 7.0 | Se fija la instantánea de tokens 9.4.2 y la iconografía 9.3.1; se incorporan PV-06, PV-07, PV-08, D-10 y X-07. |
| 8.0 | Se cierra la migración de `core/ui`: vocabulario `Aq*` sin identificadores heredados, esquema por defecto claro con seguimiento del sistema, retirada de menciones activas a sistemas anteriores y verificación final. Se mantiene el resto de extensiones, desviaciones y excepciones. |
| 8.1 | Se añade el rol `inputSurface`, los pasos 12/20 dp de espaciado y la derivación D-11 de contenedores tonales oscuros; la caja de campo de `AqForms.kt` se registra como E-07. |
| 8.2 | Se adopta la regla de propiedad del espacio por nivel atómico (ADR-0027), se registra la derivación D-12 y se corrige el espaciado de `core/ui` y de las páginas: margen de pantalla de un solo dueño, relleno de tarjeta de 16 dp, densidad de fila de 12 dp, mensajes dentro del campo y opciones sin margen externo. |
| 8.3 | Segunda pasada de fidelidad visual: radios publicados (`radius-200` en botones y etiquetas, `radius-100` en contadores de filtro, `radius-300` en tarjetas y cuerpos de lista, `radius-400` en diálogos), borde fino del botón secundario, borde hairline de `AqListBody`, paso por defecto de 16 dp entre organismos y contenido expandido limitado a `ContentMaxWidth` y centrado. |
| 8.4 | Jerarquía unificada: `AqTabs` comparte pista/píldora/badge con `AqSegmentedControl` y gana `AqTab.count`, scroll y estado deshabilitado; `AqFilterChip` se sustituye por `AqChipGroup` (selección inequívoca: check + relleno + borde); `AqFormSection`/`AqFormField` y el marcador `optionalLabel` fijan la jerarquía de formulario; el banner separa icono de estado (`AqTone.iconVector`) del cierre (`AqIcons.Close`); `AqIcons.Erase` pasa a `BackspaceIcon` y se añaden `Close`, `Check` y `AlertCircle`. Se documenta la regla "un patrón = un componente" y los criterios de tabs vs segmentado, tipos de botón, formularios y alertas. |
| 8.5 | Se registra la referencia móvil Moon (ADR-0028): Polaris sigue como autoridad visual; Moon solo informa patrones de interacción (accordion S07, chips de filtro S04/S12, error de PIN S00). `AqExpansionPanel` limita el toggle al encabezado y expone semántica `expanded` + tags; `AqPinDots` acepta `error` sin contenido accesible propio. |
| 9.0 | Se formaliza la cadena de precedencia Polaris → Moon → nativo (ADR-0029) para patrones sin equivalente Polaris, con la matriz de gaps `14-matriz-gaps-mobile.md` y la auditoría de fidelidad móvil `13-auditoria-fidelidad-mobile.md`. Corrección tipográfica a la escala `light-mobile`: `Amount` 32 sp (`heading-2xl` móvil), `CaptionSmall`/`Tag` 12 sp (`body-xs` móvil) y título de `AqTopBar` 18 sp semibold (`heading-lg` móvil). |

La historia detallada de la revisión y el estado final, incluido lo que no se
puede verificar en este entorno, está en
[`10-auditoria-fidelidad-polaris.md`](historico/10-auditoria-fidelidad-polaris.md).
