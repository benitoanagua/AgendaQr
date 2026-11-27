# ADR-0054 — Switch con geometría rectangular redondeada

**Estado:** ACEPTADO — 2026-11-27  
**Relacionado:** ADR-0044, ADR-0046, ADR-0050, ADR-0051, ADR-0053.
**Supersede:** la geometría de pista y pulgar de `AqSwitch` documentada en
ADR-0051 (que partía de la cápsula `radius=999` del spec Moon v1).

## 1. Contexto

Las capturas del editor de destinos (fila «Marcar como favorito») mostraban
el switch como una **cápsula ovalada** en ambos temas. La causa es que
`AqSwitch` usaba `AqSquircles.Circle` —equivalente a radio = mitad del alto,
o sea el estadio `pill` del spec Moon (`moon-v1-tokens-y-componentes.json`,
`Switch radius=999`)— tanto para la pista como para el pulgar.

La corrección de ADR-0051 (recorte de la sombra del pulgar al contorno de
la pista) sigue siendo válida, pero se aplicaba sobre una forma que el
diseño de producto ya no quiere: el control debe leerse como un
**rectángulo horizontal con esquinas redondeadas**, no como una píldora.

## 2. Decisión

### 2.1 Geometría

- Pista y pulgar usan `AqSquircleShape` con un radio de esquina por tamaño
  (`AqLayout.SwitchCornerRadiusX2s/Xs/Sm` = **4 / 6 / 8 dp**), es decir,
  **un cuarto del alto visual** (16 / 24 / 32 dp). La mitad del alto —la
  cápsula— queda descartada de forma explícita.
- El pulgar pasa de disco (12 / 16 / 24 dp de diámetro) a **cuadrado
  redondeado** del mismo lado, con el mismo radio que la pista: su esquina
  casa con el contorno interior de la pista y no vuelve a «cerrar» el
  conjunto en cápsula.
- La misma instancia de forma alimenta `background`, `clip` del contenido
  de la pista y `aqShadow` del pulgar, de modo que sombra, recorte y
  relleno comparten geometría en las tres plataformas.

### 2.2 Tokens

Los radios son tokens nuevos en `AqLayout` (`SwitchCornerRadius*`), junto al
resto de medidas `Switch*`. No se reutilizan `AqCorner.Interactive*` porque
la proporción «un cuarto del alto» es propia del switch y no coincide con
la escala de controles interactivos genérica; el valor `Sm` coincide con
`InteractiveSm` (8 dp) pero X2s/Xs (4/6 dp) no tienen equivalente.

### 2.3 Colores y estados

Sin cambios: pista activa `AqColor.BrandFill`, inactiva
`AqColor.FunctionalBorder` (excepción D-02 de ADR-0051), pulgar
`AqColor.OnBrandFill`, glifos Phosphor Check/X en `AqColor.Popo`, disabled
con `AqStateAlpha.Disabled`. Todo sigue saliendo de `LocalAqColorScheme`,
por lo que light/dark se recomponen solos.

### 2.4 Recorte de sombra y área táctil

El `.clip(shape)` del contenido interior de la pista usa ahora el squircle
de radio 4/6/8 en lugar del estadio: la sombra `shadows.sm` del pulgar
sigue confinada al contorno interior en Android (`BlurMaskFilter`), iOS y
wasmJs (`Modifier.shadow`), sin halo. El objetivo táctil ≥ 48 dp
(`AqMetrics.ControlMinSize`) y la semántica (`Role.Switch`, `selected`,
nombre accesible, `Disabled`) no cambian.

## 3. Desviación del spec Moon v1

El fixture de fidelidad (`MoonSpecFixtures`, generado desde el JSON del
Figma) declara `radius=999` para `Switch`. Como esta ADR fija una geometría
distinta de forma deliberada, `MoonSpecFidelityTest` registra `radius`
como **desviación documentada** para `AqSwitch` (mismo mecanismo que las
excepciones E-nn del contrato normativo, D-13). Las demás métricas de la
fila (ancho, alto, padding, gap) se siguen verificando contra el spec.

La geometría real queda cubierta por `AqSwitchRenderTest`, que mide
píxeles en las cuatro combinaciones tema × estado: esquina de la pista
pintada en (3,3) pero no en (1,1), corrida recta ≥ 40 px en el borde
superior, esquina del pulgar que cubre (3,2)/(2,3) pero no (1,1), y
pulgar ausente en la posición opuesta al estado.

## 4. Consecuencias

- `DestinationEditorScreen` y cualquier consumidor de `AqSwitch` reciben
  la nueva geometría sin overrides (ADR-0053: configuración única).
- Component Lab y la galería muestran la misma pieza (`AqComponentRegistry`
  usa el componente real).
- La tabla «Medidas de referencia Moon» del catálogo sigue describiendo el
  spec (`píldora`); la geometría implementada es la de esta ADR.
