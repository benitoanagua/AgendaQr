# ADR-0014 — Referencia visual Ant Design Mobile y navegación convencional Android/iOS

**Estado:** SUPLANTADA por ADR-0020 y ADR-0021 (historia conservada). Fue APROBADA — 2026-10-13 — **Enmendada por ADR-0017** (2026-10-16: corrige la tabla de correspondencia y los valores — escala 17/15/13, radios oficiales, paleta oscura oficial, secundario = botón `default`, barra inferior 56 dp; la historia de esta decisión se conserva)
**Suplanta:** la autoría visual de ADR-0013 (AgendaMobile como referencia). Conserva de ADR-0013 la arquitectura de capas, la implementación en `core/ui` y el prefijo `Am*` como convención de código (sin valor de marca).

## Contexto

La pasada anterior dotó al producto de un sistema de diseño propio (AgendaMobile, ADR-0013) con identidad cromática y componentes inventados. La segunda pasada exige una referencia externa verificable y convenciones de plataforma reconocibles:

- Referencia visual única y auditable: **Ant Design Mobile** (variables de tema, componentes y patrones oficiales).
- Navegación y comportamiento según **convenciones de Android e iOS**, no según un lenguaje propietario.
- Vant se mencionó como referencia en ADR-0013 y en el contrato; **Vant no es ni ha sido una referencia activa ni una dependencia**: se retira de toda fuente normativa.

## Decisión

1. **Ant Design Mobile es la única referencia visual activa.** Define paleta clara/oscura, superficies, texto, estados, radios, densidad de listas y formularios, controles, iconografía funcional y feedback. No es una dependencia: no se incorpora React, JavaScript ni CSS; se reproduce en Kotlin con Compose Multiplatform.
2. **Android e iOS definen la navegación y el comportamiento de plataforma**: destinos raíz con barra inferior en compacto, barra superior jerárquica con Atrás, back stack con restauración, back del sistema y predictive back en Android, gestos de retroceso en iOS, áreas seguras, teclado e IME. El chrome de navegación sigue las convenciones de la plataforma aunque la referencia web centre títulos o use otras densidades.
3. Donde Ant Design Mobile no alcanza WCAG AA (primario `#1677ff` con blanco 4.1:1, éxito `#00b578`, peligro `#ff3141`, tercer nivel `#999999`), **la accesibilidad prevalece**: los roles se recalibran mínimamente (acento `#1668DC`, éxito `#0A7A44`, peligro `#D21F2E`, terciario `#6E6E6E`/`#8C8C8C`) y cada desviación queda fijada por prueba. Ant Design Mobile no define tono informativo: lo informativo usa el acento (rol `info` = acento/wathet).
4. La identidad de marca se limita a nombre, datos y acento discreto; **no hay componentes inventados**: botón sólido/delineado/texto (fills de adm), campos sin borde con guía inferior (patrón Form/List de adm), celdas con separadores hairline, insignias estilo Tag (outline/round), chips estilo Selector/CapsuleTabs, pestañas con indicador, diálogo centrado, Toast/Snackbar según plataforma.
5. El prefijo `Am*` y el paquete `com.agendaqr.core.ui` se conservan como convención interna de código: renombrar ~100 símbolos en ~45 archivos no aporta ganancia funcional (criterio §9.8). Lo que se retira es la **autoría**: ningún documento normativo declara ya un sistema propio como referencia.
6. El patrón de hoja inferior para selectores sigue fuera del sistema vigente (bloqueo Robolectric documentado en ADR-0013, sin cambios).

## Correspondencia con la referencia oficial

| Familia adm | Implementación | Notas |
|---|---|---|
| Variables de tema (`theme-default`/`theme-dark`) | `AmColorScheme` claro/oscuro | Valores adoptados; recalibraciones AA documentadas en el contrato §4 |
| Button (solid/outline/none, radius 4, disabled opacity) | `AmPrimaryButton`/`AmSecondaryButton`/`AmTextAction`/`AmDangerButton` | Secundario = outline de acento; deshabilitado con opacidad .4 |
| Input (sin borde, placeholder light, clear) + Form/List | `AmTextField` (guía hairline en foco/error) | Sin borde de reposo; error con icono+texto |
| SearchBar (32px, radio 6, fill box, borde de acento activo) | `AmSearchField`/`AmSearchTrigger` | Altura mínima 48 dp por accesibilidad (desviación documentada) |
| List/Item (hairlines) | `AmListRow` (+ `showDivider`) | Título 14, descripción 12 (escala compacta); filas separadas en listas no contiguas (desviación documentada) |
| Tag (outline/round) | `AmBadge` | Etiqueta de estado con texto, nunca solo color |
| Selector/CapsuleTabs | `AmFilterChip` | Seleccionado: borde de acento + fondo wathet |
| Tabs (indicador, pestaña activa de acento) | `AmTabs` | Indicador de acento, desplazable |
| TabBar (48px, icono 24, etiqueta 10, activo de acento, sin píldora) | `AmNavBar` (64 dp propio + hairline, sin píldora M3) / `AmNavRail` M3 | Etiqueta 11 sp; objetivo 64 dp; selección con semántica |
| NavBar (atrás + título + acciones) | `AmTopBar` propia de 56 dp | Título 18 sp Medium a la izquierda por convención Android (desviación documentada); acciones de 48 dp |
| Modal (título centrado 18, contenido 15) | `AmDialog` | Botonera de plataforma; foco conservativo en destructivas |
| Toast (centrado, oscuro) / plataforma | `AmFeedbackEvent` + Snackbar | Snackbar inferior en Android (convención de plataforma) |
| Empty | `AmEmptyState` | Glifo + título + acción; glifo decorativo no anunciado |

## Cambios

- Contrato normativo reescrito (`00-contrato-normativo-diseno.md`); guía de implementación actualizada.
- Tokens recalibrados a la referencia (paleta, radios 2/4/6/8/12, escala tipográfica 17/15/13 en cuerpo/etiqueta/apoyo).
- Componentes alineados (punto 4); pantallas recompiladas sin cambios funcionales.
- Spec UX/UI enmendada a V1.4 (§12; intención y flujos intactos); D-18 actualizado.
- ADR-0013 marcado como SUPLANTADO en autoría visual (historial conservado).

## Riesgos

- **Densidad y tamaños**: la escala 17/15/13 crece frente a 16/14/12; la suite de layout a 320/360 dp y 200 % verifica no-desbordamiento.
- **Validación visual**: verificación de layout por aserciones (ADR-0007); revisión humana en dispositivo pendiente.
- **iOS runtime**: solo compilación en este entorno (checklist existente).
