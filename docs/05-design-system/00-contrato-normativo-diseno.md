# Contrato normativo de diseño

**Estado:** Normativo — vigente.
**Autoridad:** ADR-0044 y este contrato. UX/UI V1 conserva el contrato funcional; su dirección visual se rige exclusivamente por Moon.

Moon Design es el único sistema visual de Agenda QR. Color, anatomía, geometría, espaciado, elevación, opacidades, tipografía (salvo la sustitución de licencia), iconos, estados, movimiento y accesibilidad visual corresponden a Moon. Las composiciones específicas de Agenda QR se construyen con componentes Moon; los mecanismos de plataforma no crean una apariencia alternativa.

## 1. Fuente de valores y versión

| Concepto | Valor |
|---|---|
| Sistema | Moon Design (reimplementación en Kotlin/Compose, sin runtime Flutter ni web) |
| Colores | `moon_tokens` 0.0.6 (`MoonColors.light` / `MoonColors.dark`) |
| Geometría, sombras, opacidades, transiciones | `moon_design` 1.1.0 (commit `177adb7`) |
| Iconos | `@heathmont/moon-icons` 10.7.4 (`raw/imported`, 32 × 32, trazo 1) |
| Licencia | MIT © Yolo Group (ver [licencias](06-licencias-terceros.md)) |
| Valores | [`09-tokens-moon.md`](09-tokens-moon.md) |
| Tipografía | Inter (SIL OFL 1.1) con la escala de texto de Moon |

**Decisiones de derivación**

| ID | Tema | Decisión |
|---|---|---|
| PV-01 | Estados hover/pressed | Se portan los efectos definidos por `MoonBaseControl`/`MoonEffectsTheme` (foco, escala y pulso) con sus parámetros de Moon. No se reemplazan por cambios inventados de relleno. Los tokens `brandFillHover` y `brandFillActive` son derivaciones semánticas del producto y no se aplican como estado de `AqButton` salvo que la fuente Moon del componente indique expresamente ese cambio. |
| PV-02 | Contraste del tema oscuro | `piccolo` sobre negro da 2,8:1. Texto, icono, borde y foco de énfasis usan `piccolo` aclarado al 45 % (`#A58FE5`, ≥ 5:1 sobre las superficies oscuras). Rellenos de botón conservan `piccolo`. |
| PV-03 | Bordes funcionales | `beerus` (1,3:1) no sirve como límite de control; el borde funcional usa `trunks` (≥ 3:1) y `beerus` queda para divisores y bordes decorativos. |
| PV-04 | Texto de estado | `roshi`, `krillin` y `dodoria` no alcanzan 4,5:1 como texto sobre claro. El texto de estado usa versiones oscurecidas (`success` `#2C6B34`, `warning` `#805A0C`, `danger` `#BA2A2A`); los rellenos conservan el color Moon. |
| PV-05 | Contenedores tonales | Mezcla del color Moon al 8-12 % sobre la superficie (claro) o al 16-20 % sobre `gohan` oscuro. Cada par contenido/contenedor se prueba a AA. |
| PV-06 | Interlineado de tamaños arbitrarios | Si un componente recibe un `sp` fuera de la escala, se aplica el ratio de `heading-2xl` (1,25) o `body-md` (1,5). Los tamaños canónicos conservan su `lineHeight`. |

## 2. Reglas de uso


1. **Un solo origen por nivel.** La anatomía, dimensiones, variantes,
   estados y métricas internas de cada componente se transcriben de Moon
   versionado y se exponen mediante tokens semánticos de `core/ui`. Los
   espacios de composición de Agenda QR que Moon no especifica como patrón
   o componente se declaran por separado como tokens de layout de producto;
   nunca se presentan como medidas propias de Moon ni alteran la geometría
   interna de sus componentes.
2. **Capas.** La capa primitiva reproduce las escalas de Moon. La
   capa semántica expone roles: `surface`, `text`, `border`, `action` y tonos
   `info`, `success`, `warning`, `critical`. Ningún componente referencia un
   token primitivo.
3. **Sin aproximaciones inventadas.** Cada valor visual debe estar respaldado por la fuente Moon versionada o por una adaptación explícita en este contrato. Si falta la fuente, no se inventa ni se elige un valor «parecido»: se registra la limitación y se resuelve antes de declarar fidelidad.
4. **Puente interno.** `MaterialTheme` solo adapta tipos; no aporta ningún
   valor visual. Está prohibido fuera del adaptador.
5. **Inter se empaqueta** como recurso multiplataforma con su aviso de
   licencia. Los roles tipográficos usan los tamaños y pesos de Moon (cuerpo
   regular, encabezado semibold) y escalan con la fuente del sistema hasta 200 %.
6. **Contraste AA.** Cada par texto/fondo usado por un componente se prueba a
   4.5:1 como mínimo (3:1 para elementos no textuales).
7. **Movimiento.** Se usan las duraciones y curvas de Moon (`MoonTransitions`); «reducir
   movimiento» elimina la transición.
8. **Atribución.** Moon se distribuye bajo MIT; el aviso de licencia va en
   los avisos de terceros del paquete. El producto no usa nombre ni logo
   de Moon en su UI.
9. **Espaciado y geometría.** Las separaciones, rellenos y gaps propios de un
   componente Moon reproducen sus medidas de origen. `AqLayout` centraliza
   la composición de producto (`BetweenOrganisms`, `InGroup`, `CellPadding`,
   `CardPadding`, `LabelToField`, `DockVertical`) y declara cada valor como
   token; una separación de pantalla no se etiqueta como token Moon si Moon
   no define ese patrón. Nunca se usa `AqSize` como escala genérica de huecos
   ni se escriben literales visuales en componentes.


### Roles semánticos de partida (tema claro / oscuro)

| Rol semántico | Token Moon | Claro | Oscuro |
|---|---|---|---|
| Fondo de página | `goku` | `#FFFFFF` | `#000000` |
| Superficie | `gohan` | `#F6F7F9` | `#1F1F1F` |
| Texto primario | `bulma` | `#000000` | `#FFFFFF` |
| Texto secundario | `trunks` | `#687083` | `#94989E` |
| Borde / divisor | `beerus` | `#DCDEE3` | `#292929` |
| Acción rellena, foco | `piccolo` | `#5C33CF` | `#5C33CF` (foco `#A58FE5`) |
| Texto sobre acción | `goten` | `#FFFFFF` | `#FFFFFF` |
| Crítico (relleno) | `dodoria` | `#D33030` | `#D33030` |
| Éxito (relleno) | `roshi` oscurecido | `#337D3C` | `#337D3C` |
| Advertencia (relleno) | `krillin` | `#FFB319` | `#FFB319` |
| Información | `whis` | `#3448F0` | `#8F9AF7` |
| Velo de modal | `zeno` | `#8F000000` | `#8F000000` |

El resto de roles se deriva según PV-01…PV-05 y se valida a AA en
`AqSchemeTest` y `AqContrastPairsTest`.

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
`AqPrimaryButton` > `AqSecondaryButton`/`AqOutlineButton` >
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

Construidas solo con tokens y componentes de Moon; no son valores propios.

| ID | Extensión | Regla |
|---|---|---|
| X-01 | Insignia de evidencia | Icono + texto «Comprobante recibido». Nunca «Verificado» ni «Pagado». No implica dinero verificado. |
| X-02 | Indicador de sincronización | Estados local, pendiente, sincronizado y error con reintento. Discreto y no bloqueante. |
| X-03 | Mostrar QR | Negro sobre blanco con zona de silencio en cualquier tema, subida de brillo y acceso en máximo 2 toques. |
| X-04 | Roles de movimiento derivados | Derivados de la duración de 200 ms y la curva `easeInOutCubic` de Moon (PV-01). |
| X-05 | Tema oscuro | Usa `MoonColors.dark`; los roles que Moon no define se derivan según PV-02. |
| X-06 | Acentos de contexto | La asignación determinista por dominio se conserva (hash FNV-1a); los 11 pares se eligen entre colores Moon (más `roshi` y `raditz` oscurecidos) y se validan a AA. |
| X-07 | Iconografía de producto sobre Moon Icons | El conjunto se fija en `@heathmont/moon-icons@10.7.4`; 35 SVG de `raw/imported` (32 × 32, trazo 1) se conservan como constantes Kotlin. Moon no trae glifos para cada concepto: `Register`→`TravelBill`, `QrCode`→`SecurityQrCode`, `Inbox`→`MailBox`, `ScanLine`→`FilesScan`, `Store`→`ShopBasket`, `Camera`→`MediaPhoto`, `Gallery`→`GenericPicture`, `Share`→`GenericShareIos`, `Trash`→`GenericDelete`, `Maximize`→`ControlsFullScreen`, `Income`/`Expense`→`ArrowsDown`/`ArrowsUp`. `Warning` y `AlertCircle` comparten `NotificationsAlert`. **Gap documentado:** Moon 10.7.4 no trae glifo de «búsqueda sin resultados» (solo `GenericSearch`); no existe `AqIcons.SearchX` y los estados sin resultados usan `AqIcons.Search` + copy, sin inventar paths; `Edit` y `NoteEdit` comparten `GenericEdit`. `Close`→`ControlsClose` (cierre, acción) es distinto de `Cancel`→`NotificationsError` (estado); `Check`→`GenericCheckAlternative` es el indicador de selección de chips; `Erase`→`ControlsClear` borra texto, nunca el glifo de papelera. |

## 4. Desviaciones móviles

| ID | Desviación | Justificación |
|---|---|---|
| D-01 | Unidades en `dp`/`sp` | Moon (Flutter) usa px lógicos; en Compose se transcriben 1:1 a dp. |
| D-02 | Targets táctiles ≥ 48dp | Accesibilidad Android/iOS. `AqTouchTarget` amplía el hit target sin cambiar la caja visual; altura, padding y forma visibles siguen el tamaño Moon. No usar `defaultMinSize(48.dp)` dentro de la superficie visual de un control Moon de menor altura. |
| D-03 | Navegación y gestos de plataforma | Moon no define algunos mecanismos de navegación móvil; se componen con piezas Moon y APIs de plataforma, conservando tokens y apariencia Moon. |
| D-04 | Bottom sheet con arrastre y expansión | Anatomía de `MoonBottomSheet` adaptada al back predictivo. |
| D-05 | Inter empaquetado | Averta Std (fuente de Moon) es comercial; la app necesita una familia redistribuible, multiplataforma y consistente. |
| D-06 | Escalado de fuente del sistema hasta 200 % | Requisito de accesibilidad móvil. La capacidad se conserva en producción; la verificación automática cubre 100 %/150 % y el 200 % queda como revisión manual (ADR-0007). |
| D-07 | Roles semánticos derivados | Solo los roles no definidos directamente por Moon se derivan y registran en PV-01…PV-05; nunca se crea una paleta paralela. |
| D-08 | Proporciones internas de composición | Alfas de selección y fracciones de ancho/glifo/desplazamiento no son escalas de Moon; se concentran en `AqTokens.kt` y se conservan para no alterar el layout verificado. |
| D-10 | Modelo de sombras de Compose | `Modifier.shadow` no expone `offsetY` ni `spread`; `AqShadow` conserva las capas de `MoonShadows` (blur, offset, spread, alfa) y `Modifier.aqShadow` apila una sombra por capa. |
| D-11 | Contenedores tonales del tema oscuro | Se derivan mezclando el color Moon de cada estado al 16-20 % sobre `gohan` oscuro, de modo que el bloque tonal se distinga de `surface` y conserve el par de contenido en AA. |
| D-12 | Composición de página | `AqLayout.BetweenOrganisms` (16 dp) y `DockVertical` (12 dp) son tokens de composición de Agenda QR, no medidas de anatomía Moon. Solo se aplican en el nivel de pantalla; no alteran el padding, gap, tamaño, radio o altura interna de componentes Moon. Si Moon define un patrón equivalente en la versión adoptada, prevalece la medida de Moon. |

## 5. Gobernanza

- Todo cambio visual se implementa en `core/ui`; las pantallas solo componen.
- El gate `verifyAgendaQrArchitecture` falla si aparecen literales visuales
  fuera de tokens, si un componente usa tokens primitivos o si reaparece una
  referencia a un sistema de diseño eliminado.
- Las pruebas de componentes y pantallas cubren claro/oscuro y fuente al 100 %
  y 150 %, contraste AA, tamaño táctil y semántica de accesibilidad. El
  escalado al 200 % se conserva en producción, pero no tiene verificación
  automática (ADR-0007).
- Los valores y su procedencia se consolidan en [`09-tokens-moon.md`](09-tokens-moon.md); el catálogo único está en [`13-catalogo-moon.md`](13-catalogo-moon.md). Los ADR vigentes registran decisiones puntuales, no constituyen especificaciones visuales paralelas.

### Registro de excepciones del gate

Excepciones de implementación vigentes: el gate y esta tabla deben coincidir.
No autorizan valores visuales nuevos: solo permiten invocar la API indicada
en el archivo señalado con valores tomados de tokens semánticos.

| ID | API permitida | Archivo | Motivo |
|---|---|---|---|
| E-01 | `border` | `AqButtons.kt` | Borde de foco y variante contorneada |
| E-04 | `border` | `AqInputs.kt` | Foco y estado de error de campos |
| E-08 | `border` | `AqPinEntry.kt` | Borde de tecla del teclado PIN (contraste en tema oscuro) |
| E-12 | `border` | `AqTextInput.kt` | Borde de reposo, foco y error de campo de texto (MoonTextInput) |
| E-13 | `border` | `AqTextArea.kt` | Borde de reposo, foco y error de área de texto (MoonTextArea) |
| E-14 | `border` | `AqRadio.kt` | Borde de anillo de selección de botón de radio (MoonRadio) |
| E-15 | `border` | `AqAuthCode.kt` | Borde de reposo y error de campos del código de auth (MoonAuthCode) |

E-06 (elevación de botones) está retirada: los botones son planos y un
registro sin uso se detecta como excepción zombie.


## 6. Autoridad normativa única

Moon rige todas las dimensiones visuales y de interacción cubiertas por su sistema: anatomía, variantes, tamaños, color, tipografía, iconografía, radios, squircle, bordes, sombras, opacidades, estados, feedback, movimiento y accesibilidad.

La implementación es un puerto Kotlin/Compose en `core/ui`; no se incorpora runtime Flutter ni paquete Moon de ejecución. Los componentes `Aq*` son la API de Agenda QR que reproduce los componentes Moon. Las pantallas de `feature/*/presentation` componen esa API y no redefinen tokens ni anatomía.

Los patrones de producto que Moon no ofrece como componente se construyen con piezas Moon y mecanismos de plataforma mínimos. No se utilizan componentes Material 2/3 como autoridad visual ni como sustitutos estilísticos. `MaterialTheme` solo puede existir en el adaptador interno de tipos, sin aportar valores visuales.

Fuentes normativas activas:

- Este contrato: reglas y adaptaciones permitidas.
- [Tokens Moon](09-tokens-moon.md): valores exactos y roles semánticos.
- [Catálogo Moon](13-catalogo-moon.md): componentes, equivalencias y geometría.
- [Licencias](06-licencias-terceros.md): atribución y distribución.

No existen matrices de auditoría, fichas duplicadas ni documentos históricos como fuentes normativas. La historia de decisiones permanece en Git; cualquier cambio futuro se realiza en estas fuentes activas y en código, pruebas y gates correspondientes.
