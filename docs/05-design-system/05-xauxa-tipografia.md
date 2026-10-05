# Xauxa — Tipografía (T9)

**Contrato (spec congelada §12, V1.0):** "Archivo para display/encabezados;
Roboto/San Francisco para UI/cuerpo."

**Enmienda V1.1 (ADR-0005, 2026-10-05):** los títulos de página usan
**Archivo peso Light (300), ≥ 40 sp**, en lugar de negrita; los
encabezados de sección pasan a 14–16 sp en color de acento. El cuerpo
sigue en Roboto/San Francisco. Capitalización de oración (sin
mayúsculas forzadas). El escalado de fuente del sistema se mantiene.

**Tarea de implementación PENDIENTE (no hecha en esta pasada
documental):** empaquetar el peso Light (300) de Archivo (OFL 1.1,
misma fuente y licencia ya registradas abajo y en
`06-licencias-terceros.md`) y ajustar la escala display de `XauxaType`.
Hasta entonces los títulos siguen usando los pesos actuales
400/500/600/700.

## Familias expuestas por `XauxaType`

| Token | Familia | Origen | Dónde se usa |
|---|---|---|---|
| `FamilyDisplay` | **Archivo** | Empaquetada en `core/ui/src/commonMain/composeResources/font/` | `XauxaHeading` (todos los títulos con semántica `heading()`) |
| `FamilyUi` | Sans del sistema | No se empaqueta | Cuerpo/UI: **Roboto** en Android, **San Francisco** en iOS (fuente por defecto de cada plataforma) |
| `FamilyMono` | Monospace del sistema | No se empaqueta | Números/identificadores (hero cards, stats) |

## Por qué Archivo se empaqueta y Roboto no

- Archivo no es fuente del sistema en ninguna plataforma objetivo: se
  empaquetan 4 pesos estáticos (400/500/600/700) instanciados de la
  familia variable oficial, subset latin, ~40 KB cada uno. V1.1 añade
  el peso 300 (Light) a ese conjunto — pendiente de implementación.
- Roboto ES la fuente por defecto de Android y SF la de iOS: empaquetar
  una copia de Roboto añadiría peso al APK sin ningún cambio visual ni de
  contrato (§12 pide literalmente "Roboto/San Francisco"). `FamilyUi =
  FontFamily.Default` cumple la regla y respeta además las preferencias
  del sistema.

## Escalado de fuente del sistema

Todos los tamaños de `XauxaType` están en `sp` y los `XauxaHeading`
heredan ese comportamiento: el escalado de fuente del usuario se aplica
sin configuración adicional (§11).

## Origen y licencia

- Familia: [Archivo](https://fonts.google.com/specimen/Archivo), de
  Omnibus-Type (The Archivo Project Authors).
- Licencia: **SIL Open Font License 1.1** (permitido usar, empaquetar y
  redistribuir; la propia licencia acompaña al repo:
  <http://scripts.sil.org/OFL>).
- Instancias estáticas obtenidas de la distribución oficial de la familia
  (fontsource, subset latin, mismos glifos que la variable upstream).
