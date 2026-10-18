# ADR-0009 — Privacidad en pantalla (bloqueo de capturas) — PROPUESTO

**Estado:** PROPUESTO — requiere decisión de producto y verificación en dispositivo
**Área:** Área J de la auditoría ronda 2 · Android + iOS

## Contexto

Agenda QR muestra comprobantes (PDF/imágenes con montos, entidades y
posibles datos personales) y códigos QR con datos sensibles. Cualquier
pantalla puede aparecer en el selector de apps recientes y ser capturada
con screenshot sin que el usuario lo note.

## Propuesta

Bloquear la captura/preview de pantallas con contenido sensible:
visor de comprobante, visor de QR a pantalla completa (S05/S09
`FullscreenQr`) y adjuntos en detalle de actividad.

### Implementación Android (`FLAG_SECURE`)

En cada `Activity`/pantalla afectada (Compose: por composable con un
efecto sobre `LocalActivity` o por ruta en el host Android):
`window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)` al entrar y
`clearFlags` al salir. Coste: el selector de "apps recientes" muestra la
app en negro en ESAS pantallas; los screenshots del sistema quedan
deshabilitados solo ahí (toast del SO lo comunica).

### Implementación iOS

`UITextField.secureTextEntry`-style global no existe: el equivalente es
ocultar el contenido del snapshot del app switcher con un overlay en
`willResignActive` (SwiftUI/UIKit en el host iOS: pantalla neutra con el
logo). Requiere tocar el proyecto de Xcode (fuera de este repo hasta que
exista host iOS versionado).

### Interruptor de ajustes

Proponer un único ajuste "Ocultar contenido sensible en capturas"
(default ON), porque no existe pantalla de ajustes en V1: si se aprueba,
este ADR implica crear la superficie de ajustes (decisión de producto
adicional) o dejarlo siempre activo en las pantallas sensibles (sin
interruptor). Recomendación conservadora: **siempre activo, sin ajuste**
en V1.1 — cero superficie nueva y el caso de uso (privacidad) no tiene
contra-caso razonable en estas pantallas.

## Costes y riesgos

- Soporte: usuarios que reporten "no puedo hacer screenshot de mi
  comprobante" (Android) — mitigación: mensaje en la pantalla del visor.
- QA: probar interacción con el selector de apps, split-screen (Android)
  y el switcher de iOS; regression: ninguna en pantallas no-sensibles.
- Alternativa rechazada: watermarking de screenshots — no previene la
  captura, solo la rastrea (metadato que no queremos tocar en V1).

## Pasos para aprobar

1. Producto decide: siempre-activo vs ajuste (implica superficie nueva).
2. Implementar FLAG_SECURE por ruta sensible + tests Robolectric del flag
   (shadow de `WindowManager`).
3. Verificación manual (checklist de dispositivo): screenshot bloqueado,
   selector de apps negro solo en pantallas sensibles, share de archivos
   NO afectado (el compartir usa archivos, no la pantalla).
