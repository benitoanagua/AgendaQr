# ADR-0025 — Adopción del UI/UX de Polaris como sistema visual normativo

**Estado:** ACEPTADO — 2026-11-17
**Relaciona:** contrato normativo de diseño, instantánea de tokens,
ADR-0007 (verificación de layout), ADR-0008 (voz), ADR-0016 (identidad local).

## 1. Decisión

AgendaQr adopta una **reimplementación en Kotlin/Compose del sistema visual y
los patrones de interacción de Polaris** como autoridad única para tokens,
componentes, composición y movimiento. Polaris es un sistema web y no puede
consumirse como librería en Compose Multiplatform: se clona su contrato
visual, no se integra su runtime.

La fuente de valores es la documentación oficial vigente
(`polaris.shopify.com`, `shopify.dev`) y, como respaldo verificable y
versionado, `@shopify/polaris-tokens@9.4.2`. La instantánea con origen, fecha,
hash y valores exactos vive en
[`09-instantanea-tokens-polaris-9.4.2.md`](../05-design-system/09-instantanea-tokens-polaris-9.4.2.md).
El contrato único de uso está en
[`00-contrato-normativo-diseno.md`](../05-design-system/00-contrato-normativo-diseno.md).

El nombre del producto se conserva; no hay logo, colores ni recursos de otros
sistemas de diseño en la app ni en la documentación de producto.

## 2. Alcance de la sustitución

Polaris es la única autoridad visual: ninguna otra dirección, paleta, iconografía ni sistema de diseño tiene vigencia en el repositorio.

La especificación UX/UI V1 **permanece congelada en su contrato funcional**:
flujos, reglas de negocio, navegación conceptual, local-first, copy y voice
guide no cambian. Se descongela únicamente su §12 (dirección visual) y
cualquier tabla que fije colores, radios, tipografía o componentes, porque son
exactamente las partes que esta decisión sustituye. Justificación: el producto
y la interacción son competencia del contrato funcional congelado; los valores
visuales son competencia del sistema de diseño, que ahora cambia de autoridad.

## 3. Reglas

1. Los componentes consumen **solo tokens semánticos**. Ningún color,
   espaciado, radio, tipografía o duración se declara fuera de la capa de
   tokens.
2. La capa primitiva reproduce las escalas de la instantánea. La capa
   semántica mapea esos valores a roles (surface, text, border, action,
   info/success/warning/critical).
3. Inter se empaqueta como recurso multiplataforma bajo SIL OFL 1.1.
   `MaterialTheme` es solo un puente interno sin efecto visual propio.
4. Todo objetivo táctil mide al menos 48 dp. La navegación es nativa de
   Android e iOS (barra inferior, gestos). Toda desviación respecto a Polaris
   se registra en el contrato.
5. El movimiento es mínimo, funcional y respeta «reducir movimiento».
6. La UI refleja que «comprobante recibido» **no** significa «dinero
   verificado». No se usan etiquetas como «Verificado» ni «Pagado».

## 4. Extensiones del producto

Polaris no cubre estos casos; se construyen solo con tokens y componentes de
Polaris y se registran como extensiones en el contrato:

- insignias de evidencia con icono y texto («Comprobante recibido»);
- indicador de sincronización (local, pendiente, sincronizado, error con
  reintento), discreto y no bloqueante;
- «Mostrar QR»: QR negro sobre blanco con zona de silencio en cualquier tema,
  subida de brillo y acceso en como máximo dos toques desde cualquier pantalla;
- bottom sheet y barra inferior nativa, adaptación táctil y modo oscuro
  cuando la versión verificada no los define por completo;
- acentos de contexto: la asignación determinista por dominio se conserva,
  pero los pares se toman de la paleta verificada (X-06).

## 5. Valores no verificables y licencias

Si un valor no puede verificarse, se usa el valor verificable más cercano de
la misma versión, se marca como «pendiente de verificar» en el contrato y se
continúa; nunca se estima ni se sustituye por un valor propio.

La licencia de tokens e iconos de Polaris es una licencia custom basada en MIT
que restringe su uso a aplicaciones que integran o interoperan con software o
servicios de Shopify y, para aplicaciones independientes, exige que sean
visualmente distintas. AgendaQr es independiente: se continúa con **riesgo
legal documentado y aceptado explícitamente por el propietario del producto**.
No se usan el nombre, el logo ni la marca de Shopify en la app, la UI ni la
documentación de producto; las referencias técnicas a Polaris aparecen solo
en documentación de ingeniería.

## 6. Consecuencias

- `core/ui` se reimplementa sobre los tokens semánticos de la instantánea.
- Las pantallas migran sin lógica de dominio ni de sincronización nueva.
- El gate de arquitectura se amplía para detectar valores hardcodeados,
  tokens primitivos usados por componentes y referencias a sistemas
  eliminados.
- iOS sigue sin validación en este entorno (sin macOS/Xcode) y se registra
  como no verificado.
