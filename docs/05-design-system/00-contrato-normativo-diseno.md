# Contrato normativo de diseño

**Estado:** Normativo — versión 7.0
**Autoridad:** [ADR-0025](../07-decisiones/ADR-0025-adopcion-ui-ux-polaris.md)
y este documento. La especificación UX/UI V1 conserva el contrato funcional
congelado; su dirección visual queda descongelada y se rige por aquí.

## 1. Fuente de valores y versión

| Concepto | Valor |
|---|---|
| Sistema | Polaris (reimplementación en Kotlin/Compose, sin runtime web) |
| Fuente primaria | Documentación oficial vigente (`polaris.shopify.com`, `shopify.dev`) |
| Fuente verificable | `@shopify/polaris-tokens@9.4.2` |
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

E-06 (elevación de botones) está retirada: los botones son planos y un
registro sin uso se detecta como excepción zombie.
