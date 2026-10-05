# Checklist de validación iOS (runtime) — pendiente de Mac/Xcode

> U2 dejó la adquisición iOS IMPLEMENTADA y COMPILANDO (PHPicker único y
> múltiple, cámara AVFoundation con detección nativa de QR, escena activa
> en vez de `keyWindow`, canales de comprobante entrante y lote S12).
> El runtime queda **BLOCKED (sin Xcode en el entorno de esta pasada)**.
> Este es el checklist exacto para la primera máquina con macOS.

## Compilación previa (ya verificada en Linux)

- [x] `:shared`, `:feature:destinations:data`, `:feature:destinations:presentation`
      y `:core:ui` en `compileKotlinIosSimulatorArm64` → PASS (main tras U2).
- [x] `compileKotlinIosArm64` (presentation) → PASS.
- [x] Job `ios-compile` de CI → PASS.

## En Mac/Xcode

1. `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64` y abrir
   `iosApp/AgendaQR.xcodeproj` (o el workspace generado); Build & Run en un
   simulador iPhone.
2. **Auth**: signup → sesión persistente → sign-out → sign-in.
3. **Galería (S02)**: Añadir → Galería → elegir imagen → S09 con el asset
   (clasificación conservadora: imagen no-QR → revisión honesta).
4. **Galería (varios)**: picker múltiple → S12 con los elementos como
   pendientes → Revisar N pendiente(s) → Descartar.
5. **Comprobante recibido**: desde la galería propia, un elemento no-QR debe
   abrir "Comprobante recibido" → Guardar → bandeja.
6. **Cámara (S03)**: permiso → superficie de captura del SO con detección
   de QR nativa → al detectar, S09. Permiso denegado → banner contextual
   con REINTENTAR (T5).
7. **Share**: UIActivityViewController de QR/comprobante (verificar que la
   escena activa presenta el controlador; `keyWindow` ya no se usa).
8. **Local-first**: modo avión (Network Link Conditioner) → operación →
   "Pendiente" → reconectar → "Sincronizado".
9. **TalkBack/VoiceOver**: nodos únicos, heading en títulos, estados con
   texto (los tests Compose ya fijan el árbol semántico).

## Notas de la implementación

- `IosQrImportController` (PHPicker): resultados por `NSItemProvider`;
  clasificación conservadora — el QR solo se afirma por decodificación
  (en iOS la cámara usa la detección del SO; la galería pasa a revisión).
- `IosQrCameraPresenter` (AVFoundation): `AVCaptureMetadataOutput` +
  `AVMetadataObjectTypeQRCode`; el QR detectado viaja como asset al canal
  común `QrImportResult`.
- Escena activa: `UIApplication.connectedScenes` → `UIWindowScene` →
  `keyWindow.rootViewController` (fallback a la primera escena).
- `ImportBatchControls.ios` y `observeIncomingComprobantes()` ya no son
  stubs: alimentan S12 y el diálogo de recibido.
