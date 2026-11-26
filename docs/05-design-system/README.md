# Moon Design System — Agenda QR

**Estado:** Normativo. **Decisión vigente:** ADR-0044. **Implementación:** Kotlin Multiplatform / Compose en `core/ui`.

Agenda QR adopta Moon Design como **única autoridad visual**. Moon define los tokens, la anatomía de los componentes, tamaños, variantes, estados, tipografía, iconografía, radios, squircle, bordes, sombras, opacidades, movimiento y accesibilidad visual. La aplicación no mantiene una identidad visual paralela.

## Fuentes normativas únicas

| Archivo | Responsabilidad |
|---|---|
| [00-contrato-normativo-diseno.md](00-contrato-normativo-diseno.md) | Reglas obligatorias, adaptaciones móviles, gobernanza y excepciones del gate |
| [09-tokens-moon.md](09-tokens-moon.md) | Valores exactos de color, roles semánticos, tipografía e iconos |
| [13-catalogo-moon.md](13-catalogo-moon.md) | Componentes Moon, equivalencias `Aq*`, variantes y medidas |
| [moon-catalogo-referencia.md](moon-catalogo-referencia.md) | Referencia Moon por widget (API, variantes, tamaños, estados); consulta, no norma |
| [auditoria-fidelidad-moon.md](auditoria-fidelidad-moon.md) | Matriz de cumplimiento del ciclo de fidelidad (registro fechado, no fuente de verdad) |
| [06-licencias-terceros.md](06-licencias-terceros.md) | Licencias, atribución y distribución de recursos |

No hay otras especificaciones activas del Design System. Las fichas duplicadas, auditorías fechadas, matrices de gaps y decisiones visuales supersedidas se retiraron del árbol activo; la matriz de fidelidad vigente es la de `auditoria-fidelidad-moon.md`, exigida por el contrato de fidelidad y cerrada con 0 desviaciones (ADR-0047). Su historia, cuando sea necesaria, queda en Git.

## Fuente y port

- `moon_tokens` 0.0.6: `MoonColors.light/dark`.
- `moon_design` 1.1.0, commit `177adb7`: componentes, geometría, estados, sombras, opacidades y transiciones.
- `@heathmont/moon-icons` 10.7.4: iconos portados a vectores Compose.
- Inter 4.0 / SIL OFL 1.1: sustitución de Averta Std exclusivamente por redistribución/licencia.
- No se incorpora runtime Flutter, Web ni dependencia Moon de ejecución. El port mantiene la fuente y atribución MIT.

## Reglas de fidelidad

1. Reutilizar el componente Moon equivalente antes de crear una composición nueva.
2. Reproducir anatomía, variantes, tamaños, estados, padding, radios, iconos, tipografía, sombras y movimiento de la fuente versionada.
3. Consumir tokens semánticos desde `AqTokens.kt`; no añadir literales visuales en componentes o pantallas.
4. No inventar medidas. Toda adaptación de producto debe registrarse en el contrato y centralizarse en tokens.
5. `core/ui` implementa; `feature/*/presentation` compone. No crear wrappers `AqMoon*`, componentes duplicados ni estilos locales.
6. Las composiciones específicas (navegación, búsqueda, empty state, swipe, refresh, date/time) se construyen con piezas Moon y mecánica Compose/plataforma. No se utiliza Material como sistema visual ni como sustituto de componentes Moon.
7. Mantener temas claro/oscuro, contraste AA, semántica, foco, escala de fuente y reduced motion.
8. Los controles conservan su tamaño visual Moon; `AqTouchTarget` amplía la zona táctil a 48 dp sin alterar su aspecto.

## Cambio de especificación

Cualquier cambio visual se realiza únicamente en los cuatro archivos normativos anteriores y, cuando corresponda, en `core/ui`, la galería, las pruebas y los gates. No crear auditorías temporales ni matrices paralelas como fuente de verdad. Las capturas son artefactos de verificación, no especificaciones.

## Validación

Ejecutar las tareas reales de `docs/QUALITY.md` y los gates `verifyDesignSystemCompliance`, `verifyDesignSystemFixtures`, `verifyControlMinSize`, `verifyLayoutGeometry`, `verifyDocs` y `verifyScreenContract`. La fidelidad visual no se declara por compilación solamente: requiere pruebas de geometría/estados, revisión de la galería claro/oscuro y la evidencia pixel de [`evidencia/informe.md`](evidencia/informe.md) (218 pares Moon↔Aq, 0 desviaciones; comparador en `tools/moon-compare`, ADR-0048). El procedimiento completo de captura, comparación y triage está en [14-verificacion-pixel-moon.md](14-verificacion-pixel-moon.md).
