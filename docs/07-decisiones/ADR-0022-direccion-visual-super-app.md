# ADR-0022 — Dirección visual Super-App de alta densidad (histórico)

**Estado:** SUPLANTADO — 2026-10-17 por ADR-0023; se conserva para trazabilidad histórica.
> No implementar esta dirección como norma vigente. La única autoridad visual actual es ADR-0023 (TDesign Mobile 1.16.2) y la especificación UX/UI V1.13.
**Suplanta:** ADR-0020 en su **paleta** y su **escala tipográfica**; ADR-0021 en el reparto de referencias por pantalla y en la prohibición de la rejilla de Inicio.
**Enmienda:** especificación UX/UI §4 S01 (Inicio gana rejilla de acciones existentes) y §12; contrato normativo (`docs/05-design-system/00-contrato-normativo-diseno.md`) a versión 4.0.
**Referencia histórica:** `docs/05-design-system/08-referencia-super-app.md` (Anexo A del encargo), conservado como registro de la dirección sustituida.
**Relaciona:** ADR-0007 (capturas y hashes), ADR-0008 (guía de voz), ADR-0015 (jerarquía financiera), ADR-0021 (referencias por pantalla).

## Contexto

Las etapas anteriores dejaron una identidad azul tecnológico propia (ADR-0020) con una escala 17/15/13 y una auditoría de brechas G1–G9 por pantalla (ADR-0021). El propietario decidió que la **especificación Super-App China de alta densidad** —densidad, vibrancia, gradientes, rejilla de accesos y gamificación visual— es la autoridad visual **final**: donde contradice una fuente normativa anterior, prevalece este ADR-0022 («lo último prevalece»).

La dirección de alta densidad choca con invariantes de accesibilidad y de plataforma. La resolución no es quitar el estilo, sino **ajustar valores**: el estilo se adopta y los pisos que protegen a la persona usuaria (contraste, foco, objetivo táctil, escalado de fuente, movimiento reducido, movimiento detenible) se conservan. Lo que el producto no puede sostener con datos reales no se simula.

## Decisión

1. **La dirección visual vigente es la Super-App de alta densidad** descrita en `docs/05-design-system/08-referencia-super-app.md`. Suplanta la paleta y la escala tipográfica de ADR-0020 y la prohibición de la rejilla de Inicio de ADR-0021. Se conserva de ADR-0020/0021 todo lo no suplantado: navegación de plataforma, lienzo QR blanco, Lucide, fuente de sistema, prefijo `Am*`, flujos, dominio y copy funcional.

2. **Tabla de resolución de conflictos (decisiones vinculantes).** Estas resoluciones son normativas; no se reinterpretan por pantalla.

| El Anexo A pide | Conflicto | Resolución adoptada |
|---|---|---|
| Rojo `#FF2442` → naranja `#FF6A00` con texto blanco | Blanco/rojo 3.76:1 y blanco/naranja 2.87:1 (< 4.5:1) | Gradiente con extremos oscurecidos hasta ≥ 4.5:1 con texto blanco: claro `#E4001F → #D12810 → #BE4F00`; oscuro con extremos aclarados (`#FF5A70`/`#FF8A3D`) para texto oscuro ≥ 4.5:1. Valores fijados por prueba. |
| Rojo como acción primaria | El tono de peligro también es rojo | Primaria = siempre píldora con gradiente. Destructiva = nunca rellena con gradiente: contorno o tono + glifo de papelera/aviso + etiqueta específica de consecuencia + confirmación. |
| Sombras E-1/E-2/E-3 | El gate prohíbe `shadow(` | Se permiten mediante el token `AmElevation` y excepciones E-05/E-06 registradas a la vez en contrato §12 y en `exceptionRegistry` del gate (el gate se extiende para interpretar reglas `shadow:`). El `RoundedCornerShape` sigue siendo exclusivo de la capa de tokens. |
| Rejilla "King Kong" de 4–5 columnas | Spec S01: "sin acciones rápidas duplicadas" | Se implementa en Inicio con **solo las acciones que ya existen** (las del hub del botón flotante) y se retira el botón flotante con hub **de Inicio** para no duplicar. Enmienda a S01. |
| Etiqueta ≤ 4 caracteres (2 chinos) | Español y escalado al 200 % | Etiqueta corta de una línea (≤ 12 caracteres) a 12 sp; con fuente grande o 320 dp la rejilla pasa a menos columnas o a dos líneas, sin recortes. |
| Precio rojo en e-commerce | Aquí no hay precios comerciales; el rojo en finanzas se lee como egreso/peligro | El **Importe** adopta la tipografía del Anexo A (28–32, peso 800, tabular, símbolo de moneda más pequeño) y conserva color neutro con signo. La adaptación de dominio queda documentada; pintarlo rojo es un cambio de un token, no lo hace este ADR. |
| Cuerpo 14, H1 18–20, H2 16, Caption 12 | Escala previa 17/15/13 | Se adopta la escala del Anexo A. Mínimo absoluto 11 sp (badges 12 → 11–12). Todo en `sp`, validado al 200 %. |
| Estado de foco "no aplica" | Teclado, D-pad, Switch Access | Sin anillo permanente en táctil; el indicador `AmColor.Focus` se conserva para foco por teclado/D-pad/Switch Access. El estado táctil es escala + háptica. |
| Easing spring `cubic-bezier(0.34,1.56,0.64,1)`, 200–300 ms, escala 0.95 | Gate: sin duraciones literales; movimiento reducido | Se adopta como tokens de `AmMotion`. Movimiento reducido = duración 0 y escala 1. |
| Brillo (shimmer) en banners en reposo | Contenido en movimiento > 5 s debe poder detenerse | Brillo de entrada de ≤ 5 s y luego estático; en esqueletos de carga puede repetirse mientras dure la carga. Movimiento reducido = estático. |
| Háptica obligatoria al tocar; doble vibración en éxito | Dependencia de plataforma | La háptica por evento usa `AmFeedbackEvent`; en la confirmación S12, `AmSuccessCheck` da el doble pulso y `AmFeedbackEvent` desactiva el pulso simple para evitar una tercera vibración. Ambos respetan el feedback de plataforma. |
| Confeti en éxito, cohete en pull-to-refresh | Decoración | Confeti breve y solo en el resultado de importación completada (S12); respeta movimiento reducido. Pull-to-refresh personalizado solo si la pantalla ya lo tiene; si no, no se añade. |
| Carrito volador, cuentas regresivas, monedas, niveles, "HOT/限时/-50 %", servicio al cliente flotante, insignias de conteo en la barra inferior | Requieren funciones o datos que el producto no tiene | **No aplicable**; no se inventan datos. Las insignias tipo cinta sí existen como componente, con textos reales de estado ("Nuevo", "Duplicado", "Pendiente"). |
| Fuentes PingFang SC / Noto Sans SC | No se empaquetan fuentes | La fuente de sistema ya resuelve CJK (Noto en Android, PingFang en iOS); no se empaqueta nada. |
| Verde `#00B578`, azul `#1677FF`, gris `#999999` | Contraste como texto o glifo | Fondos y rellenos con el valor del Anexo; texto, enlaces y glifos significativos con variante oscurecida (éxito `#008156`, azul enlace `#0067F7`, claro); `#999999` solo para estado deshabilitado. |
| Divisor `1px #EEEEEE` | 1.16:1 | Válido como separador decorativo entre filas; todo límite funcional (campo, foco, tarjeta interactiva) ≥ 3:1. |
| Márgenes 12, gutters 8–12, padding de tarjeta 12, filas comprimidas | Objetivo táctil | Se adoptan; el área táctil sigue ≥ 48 dp (padding o tamaño mínimo interactivo). |

3. **No aplicable (no se simula).** Carrito volador, cuentas regresivas de oferta, monedas virtuales, niveles, etiquetas "HOT/限时/-50 %", servicio al cliente flotante e insignias de conteo en la barra inferior quedan **fuera de alcance** porque exigen funciones o datos que el producto no tiene; no se inventan datos. El brillo animado infinito, el confeti fuera de S12 y las mascotas animadas también quedan fuera o acotados según la tabla. Las bandas tipo cinta se admiten con textos reales de estado.

4. **Piso de accesibilidad innegociable (se ajustan valores, no se quita el estilo):** texto ≥ 4.5:1; componentes y bordes funcionales ≥ 3:1 en claro y oscuro; objetivos ≥ 48 dp; escalado de fuente hasta 200 % sin recortes a 320 y 360 dp; ningún estado solo por color; movimiento reducido = inmediato; contenido en movimiento detenible. El lienzo QR permanece blanco en ambos temas.

5. **Excepciones nuevas del gate** (registradas a la vez en contrato §12 y en `exceptionRegistry`; el gate se extiende para interpretarlas):
   - **E-05** — Sombra E-1/E-2 de superficies elevadas (tarjetas de lista y tarjetas destacadas) sobre fondo gris.
   - **E-06** — Sombra E-3 de color de la acción primaria flotante.

6. **No cambia el comportamiento.** Navegación, flujos de negocio, reglas de PIN, persistencia, sincronización, modelos de dominio y copy funcional quedan intactos. Excepción única autorizada: la rejilla de Inicio (§4 S01), que reorganiza acciones existentes sin crear funciones nuevas ni duplicarlas.

7. **Validación visual local obligatoria.** Todo cambio de tokens actualiza `AmSchemeTest`, los contratos de contraste y tipografía, y exige regenerar capturas y hashes (ADR-0007) con revisión humana previa. No se declara validado el rediseño sin capturas claro/oscuro revisadas.

## Consecuencias

- El contrato normativo pasa a **versión 4.0**; la especificación UX/UI a **V1.12**.
- En el momento de aprobar este ADR, ADR-0020 quedó parcialmente sustituido en paleta/escala y ADR-0021 en referencias/rejilla. ADR-0023 los suplanta ahora como autoridad visual e iconográfica; su contenido restante es histórico y no es una norma vigente.
- Se retira el botón flotante con hub **de Inicio** (no de otras pantallas); las pruebas que dependían de él se actualizan en el mismo cambio.
- Al ser una reforma de tokens, los escenarios de `LayoutReferenceTest` se revisan con la nueva paleta (ADR-0007).

## Riesgos

- **Contraste con texto blanco sobre el gradiente:** oscurecer ambos extremos vuelve el gradiente rojo profundo → naranja quemado. Mitigación: valores AA de partida fijados por prueba; la alternativa de brillo (texto oscuro sobre el gradiente original) exige decisión del propietario.
- **Parecido con una app concreta:** se mitiga con la regla de imitar patrones, nunca activos (iconos, ilustraciones, logotipos, textos, colores de marca, composiciones calcadas).
- **Escala 14/12 y densidad:** se prueba a 320/360 dp y 200 % de fuente; el layout cede antes que la legibilidad.
- **Sombras en Compose Multiplatform:** la aproximación de las sombras CSS del Anexo a `shadow` de Compose se documenta; no hay sombras de color exactas y se aproximan con color ambiental/puntual.

## Consolidación (2026-10-17)

La revisión de limpieza conservó las decisiones históricas y los componentes
protegidos por este ADR; «parcialmente suplantado» no convierte los ADR-0020 y
ADR-0021 en documentación descartable. La guía de implementación y los
comentarios de la capa `core/ui` se alinearon con ADR-0022 y el contrato
normativo.

La importación completada en S12 compone `AmSuccessCheck` y `AmSuccessBurst`.
El check es decorativo: `AmFeedbackEvent` ya anuncia el resultado, por lo que
no se añade un segundo `liveRegion`. Ambos respetan movimiento reducido. No se
archivaron ADRs ni se eliminó documentación histórica.

La prueba de arranque reveló etiquetas duplicadas para «Traer un QR» en Inicio:
la rejilla de acciones y el estado vacío ofrecían la misma acción. Se quitó la
acción redundante del estado vacío; la rejilla existente conserva el acceso y
el contrato de no duplicar acciones.
