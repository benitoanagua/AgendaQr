# ADR-0013 — Sistema de diseño propio: AgendaMobile

**Estado:** APROBADA — 2026-10-12
**Suplanta:** ADR-0005 (lenguaje visual Metro), ADR-0011 (contrato visual Xauxa) y la autoría visual de ADR-0012; conserva de ADR-0012 la implementación sobre Material 3 y la tipografía de sistema.

## Contexto

Agenda QR heredó de su referencia técnica un sistema de diseño prestado (Xauxa) con un laboratorio de componentes en WebAssembly como entorno de validación. Tras V1.2 (ADR-0012) la interfaz ya era convencional sobre Material 3, pero la autoridad visual seguía siendo Xauxa: nombres, contrato normativo, gates y documentación hablaban de un lenguaje (Metro) que el producto ya no usaba, y el laboratorio wasm era la única razón de existir de un target completo de compilación.

El producto necesita un sistema de diseño propio, mobile-first, con nombre neutral y duradero, inspirado en los patrones de interacción de las apps móviles contemporáneas (Ant Design Mobile, Vant como referencia), implementado íntegramente en Kotlin con Compose Multiplatform.

## Decisión

1. **AgendaMobile** es el único sistema de diseño de Agenda QR. Implementación: `core/ui` (`com.agendaqr.core.ui`), prefijo `Am*`. Sin fachadas, alias ni capas de compatibilidad con Xauxa.
2. Arquitectura de capas: tokens privados (`AmPalette`) → tokens semánticos (`AmColorScheme`, `AmColor`, `AmSpacing`, `AmRadius`/`AmShapes`, `AmMetrics`, `AmType`/`AmTextStyles`, `AmMotion`) → tema claro/oscuro (`AmTheme`) → componentes compartidos → patrones → pantallas → pruebas de contrato.
3. Identidad visual nueva, verificada por contraste: acento del linaje del logo recalibrado (claro `#0059D6`, oscuro `#6FA5FF` con tinta), superficies neutras frías (no blanco/negro puro), tema oscuro de tinta elevada, 12 tonos de identidad de contexto nuevos (etiqueta contemporánea), umbrales AA fijados en `AmSchemeTest` (contenido ≥ 4.5:1, bordes/foco ≥ 3:1, suelo deshabilitado ≥ 3:1).
4. Interacción mobile-first: botón secundario tonal (sin borde de reposo), campos tonales con guía inferior solo en foco/error, celdas con divisor opt-in, alertas tonales redondeadas, estado vacío con glifo decorativo, chips píldora, encuadre de scanner con esquinas de acento en canvas, radios moderados 4/8/12/16/24.
5. Eliminación total de Xauxa, Metro y del laboratorio de componentes: código, tokens, gates, target wasm (única razón: el lab), tareas `componentLabWeb`/`syncComponentLabWeb`, paso de CI, documentación normativa anterior y capturas del laboratorio. El nuevo gate (`agendaqr.design-system.gradle.kts`) enforcementa AgendaMobile.
6. Iconografía: Lucide (ISC) se conserva, encapsulada en `AmIcons` (auditoría de licencia en `06-licencias-terceros.md`). Tipografía: familia de sistema (conservado de ADR-0012).

## Cambios

- `core/ui`: 24 archivos `Xauxa*` retirados; sistema `Am*` nuevo (15 archivos de componentes + tokens + tema + previews por plataforma).
- 45 archivos de `feature/`, `androidApp/` y `shared/` migrados al nuevo contrato (componentes, tokens, transiciones).
- Pruebas del sistema reescritas (Am*); suite de presentación y commonTest adaptadas; expectativas sin rebajar.
- `settings.gradle.kts` sin repos Node/Yarn/binaryen; `core/ui` sin target wasm; workflow sin paso de laboratorio.
- Documentación: contrato normativo nuevo (`00-agendamobile-contrato-normativo.md`), reglas de implementación, este ADR; ADR-0005/0011/0012 marcados como suplantados en su parte visual.

## Riesgos y mitigaciones

- **Regresión visual involuntaria**: mitigada con el pin de valores en `AmSchemeTest` + manifiesto de hashes renovado + capturas Roborazzi.
- **Contraste en pares nuevos**: todos los pares calculados y fijados por prueba antes de llegar a pantalla.
- **Patrón hoja inferior (Ant ActionSheet/Vant Picker)**: se evaluó con `ModalBottomSheet` para S08 y la asociación de comprobantes, y se retiró: la ventana de la hoja no despacha acciones semánticas bajo Robolectric (contrato de pruebas UI del repo), dejando el flujo inverificable en el gate. Los selectores permanecen como `AmDialog`. **Siguiente paso concreto**: reevaluar el patrón cuando el entorno de pruebas soporte acciones en la ventana de la hoja (o validar por instrumentados en dispositivo), con la misma suite de semántica.
- **Validación iOS**: solo compilación de targets en este entorno; el runtime requiere Xcode (`IOS_RUNTIME_CHECKLIST`).

## Criterios de aceptación

1. Todas las pantallas reales consumen AgendaMobile; ningún componente del sistema anterior en el árbol de fuentes.
2. Ningún flujo funcional desaparece; dominio y navegación intactos.
3. Cero referencias activas a Xauxa/Metro/laboratorio/wasm en código, build, CI y documentación normativa (las menciones históricas viven en ADRs y registros de auditoría).
4. Gates, compilaciones (Android + iOS) y pruebas disponibles en verde; bloqueos externos identificados explícitamente.
