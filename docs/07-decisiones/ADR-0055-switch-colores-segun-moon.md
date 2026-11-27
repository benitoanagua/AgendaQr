# ADR-0055 — Colores de `AqSwitch` según Moon Design

**Estado:** ACEPTADO — 2026-11-28  
**Relacionado:** ADR-0044, ADR-0046, ADR-0050, ADR-0051, ADR-0054.
**Supersede:** la excepción de color de ADR-0051 §2.2 (pista inactiva con
`AqColor.FunctionalBorder`/`trunks`). El resto de ADR-0051 (recorte de
sombra, iconografía Phosphor) sigue vigente.

## 1. Contexto

Las capturas del editor de destinos (fila «Marcar como favorito») mostraban
la pista del estado OFF en `trunks` (`#687083` light / `#94989E` dark), un
gris mucho más marcado que la referencia Moon. La causa era la excepción
documentada en ADR-0051 §2.2: `AqSwitch` usaba `AqColor.FunctionalBorder`
para la pista apagada argumentando que `beerus` quedaba a ~1,3:1 de la
superficie, bajo el mínimo de 3:1 que el proyecto aplica a componentes
(D-02).

La referencia visual de Moon (`switch_theme.dart` y las capturas
«Custom MoonSwitch» OFF/ON en light y dark) fija la asignación:

- pista inactiva: `beerus` — apenas distinguible de la superficie, la
  pista «desaparece» y el estado lo carga el pulgar;
- pista activa: `piccolo`;
- pulgar: `goten` (blanco) con glifo `popo` (negro).

Moon Design es la única autoridad visual (ADR-0044) y la fidelidad v1 se
genera desde el spec (ADR-0050): la asignación de colores del spec no es
opcional.

## 2. Decisión

`AqSwitch` asigna sus colores exactamente como Moon:

| Rol | Token Moon | `AqColor` | Light | Dark |
| --- | --- | --- | --- | --- |
| Pista ON | `piccolo` | `BrandFill` | `#5C33CF` | `#5C33CF` |
| Pista OFF | `beerus` | `Border` | `#DCDEE3` | `#292929` |
| Pulgar | `goten` | `OnBrandFill` | blanco | blanco |
| Icono pulgar | `popo` | `Popo` | negro | negro |

Todos los colores se resuelven desde `LocalAqColorScheme` vía los getters
composables de `AqColor`, por lo que light/dark se recomponen solos. No se
tocan los tokens globales ni otros componentes: `FunctionalBorder` sigue
existiendo para los usos que sí lo justifican (p. ej. bordes funcionales de
`AqButtons`).

### 2.1 Desviación frente al contraste D-02, documentada

Los colores de pista del spec quedan por debajo del 3:1 que el proyecto
exige a componentes: la pista OFF en `beerus` queda a ~1,3:1 sobre
`surface`, y la pista ON en `piccolo` (`brandFill`) queda a ~2,2:1 sobre
`background` oscuro. Ambas se aceptan como **desviación consciente** por
estas razones:

1. **Autoridad visual:** la asignación `beerus`/`piccolo` es la del spec
   Moon (`switch_theme.dart`); reproducirla es el objetivo de ADR-0044 y
   ADR-0050. Sustituir `beerus` por `trunks` (la excepción de ADR-0051)
   reproduce el defecto visual observado en las capturas, y oscurecer
   `piccolo` rompería el color de marca (ADR-0044).
2. **El estado no depende del color de la pista:** el switch es un control
   binario cuyo estado ON/OFF lo codifican la posición del pulgar (con
   recorrido animado), la presencia del glifo Check/X y la sombra del
   pulgar recortada a la pista (ADR-0051). La pista OFF actúa como fondo
   casi neutro y la ON como campo de marca, igual que en la referencia
   Moon.
3. **Accesibilidad no depende del contraste de pista:** el nodo expone
   `Role.Switch`, `selected` y nombre accesible; TalkBack anuncia el
   estado con semántica, no con color. El glifo del pulgar (`popo` sobre
   `goten`) sí mantiene contraste AA en ambos temas — lo verifica
   `AqSwitchRenderTest`. El objetivo táctil ≥ 48 dp se mantiene
   (ADR-0046).
4. **Alcance acotado:** la excepción son solo los colores de pista de
   `AqSwitch`. El resto de superficies que sí delimitan contenido o
   acciones conservan `FunctionalBorder`/`trunks` ≥ 3:1 (ver
   `AqContrastPairsTest`).

Si una futura versión de Moon elevara el contraste de la pista inactiva,
esta ADR se revisa junto al spec.

## 3. Consecuencias

- La fila «Marcar como favorito» y cualquier otro `AqSwitch` muestran la
  pista OFF en `beerus` y ON en `piccolo`, idéntico a la referencia Moon
  en ambos temas.
- `AqSwitchRenderTest` fija por píxeles la asignación real contra los
  tokens del esquema activo (`border`, `brandFill`, `onBrandFill`) en las
  cuatro combinaciones tema/estado, y mantiene las pruebas de geometría
  (ADR-0054), ausencia de halo y contraste de la pista activa.
- ADR-0051 queda como histórico: su excepción de color está supersedida;
  su recorte de sombra e iconografía siguen vigentes.
