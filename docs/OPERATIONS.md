# Agenda QR — Operaciones

Guía operativa: cómo construir y validar localmente, cómo se firma y distribuye
Android, y el checklist para validar el runtime de iOS. Consolida los antiguos
documentos operativos de `09-implementacion`.

## 1. Construcción y pruebas locales

Todas las tareas de `androidApp` requieren el flag de firma de candidato porque
el guard se evalúa en configuración:

```bash
./gradlew verifyAgendaQrArchitecture -PallowDebugSigningForRc=true
./gradlew :androidApp:assembleDebug -PallowDebugSigningForRc=true
./gradlew :androidApp:assembleRelease -PallowDebugSigningForRc=true
./gradlew :androidApp:lint -PallowDebugSigningForRc=true
```

Pruebas de módulos y de aceptación: ver
[QUALITY.md](QUALITY.md) §3.

Para runtime contra un Supabase local, además:

```bash
-PSUPABASE_URL=http://10.0.2.2:54321 -PSUPABASE_PUBLISHABLE_KEY=<anon>
```

Configuración de Supabase (claves, CI y requisitos): ver
[ARCHITECTURE.md](ARCHITECTURE.md) §10.

## 2. Android — firma y distribución

**Datos de la aplicación:** `applicationId` `com.agendaqr.app`, versión `0.1.0`,
`versionCode` 1.

**Firma RC:** `~/.android/debug.keystore` (androiddebugkey) vía
`build-logic/src/main/kotlin/agendaqr.android-application.gradle.kts`, con
`isMinifyEnabled=false`. Se usa para RC interno y pruebas de humo en emulador.

**Firma de producción:** se prepara mediante variables de entorno
`ANDROID_KEYSTORE_FILE`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS` y
`ANDROID_KEYSTORE_PASSWORD` en el mismo archivo. Si las variables están presentes
y el archivo existe, se usa el keystore de producción; si no, se cae al de debug
con advertencia. El material privado nunca se versiona (`.gitignore` bloquea
`*.jks`, `*.keystore`, `keystore.properties`).

**Artefactos:** `androidApp/build/outputs/apk/release/androidApp-release.apk` y
`androidApp/build/outputs/bundle/release/androidApp-release.aab`.

### Uso de `-PallowDebugSigningForRc=true`

`agendaqr.android-application` NO permite firmar release con el keystore de debug
de forma silenciosa: sin variables de producción, el build de release falla en
configuración con un `GradleException` explícito. El único camino para un RC
local/candidato es pedírselo de forma explícita con el flag.

- **CI** lo fija como `ORG_GRADLE_PROJECT_allowDebugSigningForRc: "true"`: el
  assembly de release del gate es un *candidato*, no una firma de producción;
  **PRODUCTION_RELEASE sigue BLOCKED** sin keystore real.
- **Local:** cualquier tarea de `androidApp` necesita el flag (incluso
  `verifyAgendaQrArchitecture` o `assembleDebug`).
- **El guard no se toca:** la exigencia de `ANDROID_KEYSTORE_*` para firma de
  producción permanece intacta.

### Google Play readiness — PENDING

**No listo para envío a Play** (sí para RC Android). Insumos de producto
pendientes:

- icono de app (adaptive icon);
- capturas / feature graphic;
- descripción corta y completa (ES);
- URL de política de privacidad (requerida para Data Safety);
- formulario Data Safety: recolección (Auth email, Storage), cifrado, borrado;
- clasificación de contenido, público objetivo y declaraciones;
- URL de eliminación de cuenta (si Play la exige para Auth);
- keystore de producción + secretos `ANDROID_KEYSTORE_*` en Play Console / CI.

**Seguridad:** no se incorpora ninguna `service_role`; solo `SUPABASE_URL` +
publishable key se exponen a la app. Storage privado
`comprobantes/<user_id>/`, RLS `auth.uid() = user_id`. `.gitignore` bloquea
keystores; CI lee credenciales de secretos, no hardcodeadas.

## 3. iOS — checklist de validación (runtime), pendiente de Mac/Xcode

La adquisición iOS está implementada y compila; el runtime queda
**BLOCKED (sin Xcode en este entorno)**. Este es el checklist para la primera
máquina con macOS.

### Compilación previa (verificada en Linux)

- `:shared`, `:feature:destinations:data`, `:feature:destinations:presentation`
  y `:core:ui` en `compileKotlinIosSimulatorArm64`.
- `compileKotlinIosArm64` (presentation).

### En Mac/Xcode

1. `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64` y abrir el proyecto
   Xcode en `iosApp/`; Build & Run en un simulador iPhone.
2. **Acceso local:** primer arranque → configurar PIN sin cuenta → reinicio →
   desbloqueo local.
3. **Galería (S02):** Añadir → Galería → elegir imagen → S09 con el asset
   (clasificación conservadora: imagen no-QR → revisión honesta).
4. **Galería (varios):** picker múltiple → S12 con los elementos como pendientes
   → Revisar N pendiente(s) → Descartar.
5. **Comprobante recibido:** un elemento no-QR desde la galería propia debe abrir
   «Comprobante recibido» → Guardar → bandeja.
6. **Cámara (S03):** permiso → superficie de captura del SO con detección de QR
   nativa → al detectar, S09. Permiso denegado → banner contextual con
   REINTENTAR.
7. **Share:** `UIActivityViewController` de QR/comprobante (verificar que la
   escena activa presenta el controlador; `keyWindow` ya no se usa).
8. **PDF (S02):** «Elegir archivo (PDF)» → `UIDocumentPickerViewController`
   (`IosPdfPicker`, asCopy) → el PDF llega al diálogo «Comprobante recibido»
   con extensión `pdf`. Cancelar el picker no pierde nada.
9. **Biometría:** con PIN configurado y Face/Touch ID inscrito, el lock
   muestra «Usar biometría» → `LAContext.evaluatePolicy` → éxito desbloquea;
   fallo/cancelación mantiene el bloqueo con el PIN visible
   (`LocalAuthentication`, mapeo LAError → contrato común).
10. **Nube opcional:** desde «Cuenta y respaldo», conectar una cuenta; modo avión
    → operación → «Pendiente» → reconectar → «Sincronizado».
11. **VoiceOver:** nodos únicos, heading en títulos, estados con texto (los tests
    Compose ya fijan el árbol semántico).

### Notas de la implementación

- `IosQrImportController` (PHPicker): resultados por `NSItemProvider`;
  clasificación conservadora — el QR solo se afirma por decodificación (en iOS la
  cámara usa la detección del SO; la galería pasa a revisión).
- `IosQrCameraPresenter` (AVFoundation): `AVCaptureMetadataOutput` +
  `AVMetadataObjectTypeQRCode`; el QR detectado viaja como asset al canal común
  `QrImportResult`.
- `IosPdfPicker` (UIDocumentPickerViewController): delegate retenido fuerte
  (UIKit lo pide weak), un solo PDF por selección, entrega por
  `IncomingComprobanteFeed` con mime `application/pdf`.
- `IosBiometricGate` (LocalAuthentication): `canEvaluatePolicy` para
  disponibilidad (`biometryType` distingue sin-hardware de no-inscrita),
  `evaluatePolicy` para el intento, `invalidate()` al cancelar; códigos
  `LAError` mapeados al contrato común (fail-closed: solo `Success`
  desbloquea).
- Escena activa: `UIApplication.connectedScenes` → `UIWindowScene` →
  `keyWindow.rootViewController` (fallback a la primera escena).
- `ImportBatchControls.ios` y `observeIncomingComprobantes()` alimentan S12 y el
  diálogo de recibido.
