# ADR-0017 — Alineación fiel con Ant Design Mobile 5.43.0

**Estado:** SUPLANTADA por ADR-0020 y ADR-0021 (historia conservada). Fue APROBADA — 2026-10-16
**Enmienda:** ADR-0014 (tabla de correspondencia y valores erróneos).
**Relaciona:** ADR-0013, ADR-0014, contrato normativo (`docs/05-design-system/00-contrato-normativo-diseno.md`), especificación UX/UI V1.7 (`docs/04-ux/02-especificacion-ux-ui-v1.md` §12).

## Contexto

Una auditoría de la capa visual (`core/ui`) contra los valores oficiales de `antd-mobile@5.43.0` (variables `theme-default`/`theme-dark` y CSS por componente) encontró que el repositorio se declaraba fiel a Ant Design Mobile (ADR-0014) pero aplicaba una interpretación intermedia: radios, tipografía y paleta oscura de una referencia visual anterior no normativa sobre Material 3. Las desviaciones no estaban documentadas y los documentos se contradecían entre sí y con el código:

- F-01: escala tipográfica 14/13/12/11 frente a la 17/15/13 declarada en ADR-0014.
- F-02…F-08: paleta clara/oscura distinta de la oficial (hairlines `E5E5E5`/`F0F0F0`, aviso claro `8A5300`, oscuros pastel `FF9C96`/`6BD69E`/`F8C96A`/`6FA5FF`, bordes oscuros `3A3A3A`/`4A4A4A`, `surfaceSecondary` `141414`, terciario `9E9E9E`, `onDanger` `3A1512`).
- F-10…F-20: radios 8/12/píldora donde la referencia fija 4/6/8/2.
- F-30…F-37: `AmDialog` como `AlertDialog` con botonera lateral en vez de fila hairline; buscador con indicador M3; chip de filtro con borde de reposo; encabezado de sección 18 SemiBold en vez de List header.
- F-40…F-50: contradicciones documentales (referencia única frente a adopción inconsistente de tokens; radios §1.7 frente a §6; superficies y divisores oscuros; barra inferior 64 frente a 56 dp; secundario "outline de acento" frente a tonal + hairline; fila "Estados" duplicada; comentarios atribuyendo valores a clases Tailwind).

## Decisión

D1. **Jerarquía de referencias:** (1) Ant Design Mobile oficial 5.43.0; (2) convenciones Android/iOS; (3) accesibilidad con recalibración mínima; (4) la especificación UX/UI de AgendaQr para composición. Las referencias históricas no definen tokens.

D2. **Radios:** botón 4 (`Small`); Tag/Selector/Segmented 2 (`ExtraSmall`, con `round` opt-in en tags); buscador 6 (`Medium`); tarjeta de lista/tappable/media/diálogo/banner 8 (`Large`); chips de filtro estilo CapsuleTabs en píldora; hoja inferior 12 (`ExtraLarge`, hoy sin uso); FAB y `AmStatusPill` en píldora (convención Android; variante `rounded` de NoticeBar) — ambos documentados.

D3. **Tipografía** = escala 17/15/13: título 18; cuerpo/botón/título de fila/entrada/pestañas 17; etiqueta/contenido de modal/cápsula/extra 15; descripción 13; ayuda/error 12; barra inferior y tag 11 (la referencia usa 10 en TabBar: se conserva 11 por legibilidad, documentado). Pesos: normal salvo título de diálogo (bold) y etiqueta de botón (Medium, documentado). Importe 26 SemiBold, Display 32 y Headline 24 se conservan como extensiones financieras sin equivalente en la referencia.

D4. **Paleta oscura = valores oficiales**, con recalibración mínima solo donde falla AA: `accent` `#3086FF` con tinta `#0A1120`; `accentText` `#4F96FF`; `danger` `#FF4A58` con tinta `#1A1A1A`; `success` `#34B368`; `warning` `#FFA930`; terciario `#8C8C8C`. El `wathet` oficial `#0D2543` se adopta sin cambios; el acento puro sobre él queda en 4.39:1 (no apto como texto), por lo que el contenido del tono informativo usa `accentText` `#4F96FF` (5.24:1) y el acento puro sobre wathet se reserva a glifos y bordes.

D5. **Paleta clara:** `border` y `divider` → `#EEEEEE`; `warning` `#8A5300` → `#A85A00` (naranja más cercano a la referencia que cumple AA sobre `#FFFFFF`, `#F5F5F5` y `#FDF3DC`); el resto se conserva (`1668DC`, `0A7A44`, `D21F2E`, `6E6E6E`).

D6. **Superficie oscura elevada** `#1F1F1F` sobre página `#1A1A1A` (la referencia no define elevación): desviación documentada; `surfaceSecondary` oscuro = `#0A0A0A` (box oficial).

D7. **Encabezado de sección** = List header (15, texto secundario, peso normal, `heading()` intacto).

D8. **`AmDialog`** replica el Dialog oficial: popup radio 8, título 18 bold centrado, contenido 15, fila de acciones a ancho completo con hairlines, confirmar en negrita (acento; peligro si destructivo), cancelar en texto normal. Firma pública, semántica y foco conservador intactos. Verificado bajo Robolectric (sin fallback).

D9. **Botón secundario** = botón `default` oficial (fondo claro + hairline + texto primario); NO es "outline de acento". Se corrigió la documentación, no el código; solo cambió su radio a 4.

D10. **`AmFilterChip`** = CapsuleTabs: reposo `SurfaceSecondary` sin borde, activo acento con `OnAccent`, píldora, etiqueta 15.

D11. **`AmTextField`**: se conserva `TextField` M3 con etiqueta flotante y guía hairline en reposo (etiqueta persistente = accesibilidad). Desviación documentada frente a Form/Input oficial (sin subrayado).

D12. **Buscador** = SearchBar oficial: fondo `SurfaceSecondary`, radio 6, sin etiqueta flotante visible (nombre accesible por semántica), borde de acento 1 dp en foco (excepción E-04), altura mínima 48 dp (oficial 32; desviación documentada).

D13. **Sin cambios:** barra superior 56 dp con título a la izquierda y barra inferior 56 dp (convención de plataforma, documentadas); Snackbar inferior en vez de Toast; `AmExtendedFab`. Solo se corrigieron los documentos ("64 dp" → 56).

## Tabla de correspondencia adm → `Am*` (corregida)

| adm | Implementación | Estado |
|---|---|---|
| Variables de tema | `AmColorScheme` | Fiel salvo recalibraciones AA y superficie oscura elevada (documentadas) |
| Button solid/primary/danger | `AmPrimaryButton`/`AmDangerButton` | Radio 4; 48 dp ≈ `size=large` |
| Button `default` | `AmSecondaryButton` (fondo claro + hairline + texto primario) | Radio 4; compacto 40 visual / 48 táctil |
| Button `none` | `AmTextAction` | Radio 4 |
| Input + Form/List | `AmTextField` | Desviación: `TextField` M3 con etiqueta flotante y guía hairline |
| SearchBar | `AmSearchField`/`AmSearchTrigger` | Fiel (box, radio 6, borde activo, E-04); 48 dp (oficial 32) |
| List / List.Item | `AmListBody`/`AmListRow` | Card mode radio 8; chevron opcional |
| Tag (outline/round) | `AmBadge`/`AmTonalTag` | Radio 2, `round` opt-in |
| CapsuleTabs | `AmFilterChip` | Reposo box sin borde, activo acento |
| Selector | — (no usado) | — |
| Tabs | `AmTabs` | Indicador de acento, desplazable |
| TabBar | `AmNavBar`/`AmNavRail` | 56 dp, etiqueta 11 (desviación) |
| NavBar | `AmTopBar` | 56 dp, título a la izquierda (plataforma) |
| Dialog | `AmDialog` | Fila de acciones hairline |
| Toast | `AmFeedbackEvent` + Snackbar | Inferior (plataforma) |
| NoticeBar | `AmStatusBanner`/`AmStatusPill` | Banner radio 8; pill = `rounded` |
| Segmented | `AmSegmentedControl` | Radio 2; sin sombra (gate) |
| Empty | `AmEmptyState` | Glifo 64 |
| Card | `AmTappableCard`/`AmMediaFrame` | Radio 8 |
| (sin equivalente) | `AmExtendedFab`, `AmQuickAction`, `AmMetaRow`, `AmIconBadge` | Extensiones: plataforma Android / anatomía financiera |

## Desviaciones deliberadas (motivo)

Plataforma: título a la izquierda, barra inferior 56 dp/etiqueta 11, buscador 48 dp, Snackbar inferior, FAB, objetivo 48 dp, superficie oscura elevada. Accesibilidad: recalibraciones AA de la tabla del contrato §4.4, placeholder en texto secundario (el `light` oficial = 1.61:1), peso Medium de botón, etiqueta flotante persistente. Gate: segmentado sin sombra, hoja 12 sin uso.

## Consecuencias

- Tokens, pines (`AmSchemeTest`, `AmTypographyTest`, `S02UiPolishTest`) y contrato §4–§6/§9/§12 actualizados en el mismo cambio; hashes visuales regenerados.
- Excepción E-04 (`border:AmInputs.kt`) registrada en contrato §12 y en el gate en el mismo cambio; E-01 corregida a botón `default`.
- Ningún cambio funcional: dominio, datos, sync, navegación, ViewModels y copy intactos.

## Riesgos

- Textos a 17 sp pueden desbordar a 320 dp y 200 % de fuente: se resuelve con layout, nunca bajando la escala; la suite de layout lo verifica.
- Separadores oscuros `#2B2B2B` sobre `#1F1F1F`: si resultan imperceptibles en revisión humana, es decisión pendiente del propietario (alternativa: `#3A3A3A`), no cambio silencioso.
- Hashes visuales sensibles al entorno (ADR-0007): regenerar en CI antes de fusionar si el entorno difiere.
- iOS: solo compilación en este entorno; runtime sin validar.
