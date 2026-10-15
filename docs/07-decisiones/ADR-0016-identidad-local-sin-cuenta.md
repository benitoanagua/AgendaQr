# ADR-0016: Identidad local sin cuenta y acceso con PIN

**Estado:** Aceptada · **Fecha:** 2026-10-14 · **Relaciona:** ARCHITECTURE.md (§5), ADR-0014, ADR-0015

## Contexto

La app prometía ser "100% local", pero la implementación condicionaba el arranque a una sesión de Supabase:

- `AgendaQrApp` construía el árbol de UI solo tras `AuthState.SignedIn`;
- `userScopedKey(prefix)` lanzaba `error("Authentication required")` si no había `currentUserOrNull()`;
- los directorios de comprobantes y el historial iban namespaced por `user_id` remoto.

Consecuencia práctica: sin cuenta (o con la sesión remota borrada) el usuario
perdía el acceso a su propio dispositivo. El identificador de un servicio
externo no puede ser la raíz de la identidad local.

## Decisión

1. **Identidad local independiente.** Un `LocalProfile` (UUID aleatorio propio,
   `createdAt`, `schemaVersion`) es la raíz del namespace de almacenamiento.
   Se crea con `getOrCreate()`: idempotente, sin conexión, sin cuenta, sin
   correo, sin contraseña y sin identificador de hardware. Nunca se deriva de
   `currentUserOrNull()`, del PIN ni de la sesión remota.
2. **El login remoto deja de ser obligatorio.** La puerta de entrada es la
   protección local (`LocalOnboardingScreen` → `LocalLockScreen` →
   `LocalAccessMode.Unlocked`). La superficie remota (`AuthScreen`) se
   conserva, pero ahora vive detrás de la opción **Cuenta y respaldo**
   (`AppRoute.Account`) y se compone de forma perezosa: si el usuario no entra
   allí, no se crea ningún repositorio remoto.
3. **Namespace por perfil.** Las claves pasan de `prefix.<user_id>` a
   `prefix.local.<profile_uuid>` (`profileScopedKey`). El segmento `.local.`
   impide que un id remoto arbitrario se confunda con identidad local.
4. **Repositorios remotos resueltos al usarlos, no al construirlos.** Los
   cuatro repositorios remotos reciben un proveedor de cliente (`clientProvider`)
   en lugar de instanciar `AgendaQrSupabase.client` como argumento por defecto.
   Así el grafo local se monta con Supabase sin configurar. La ausencia de
   configuración es un estado soportado, no un error.
5. **Arranque sin cliente remoto.** La detección de instalaciones heredadas se
   hace escaneando las claves locales (`legacyLocalProfileIdOrNull`), no
   leyendo la sesión remota persistida. El arranque no toca la red ni crea el
   cliente Supabase.
6. **Migración de instalaciones existentes.** `LegacyLocalDataMigration` copia
   `<prefijo>.<user_id>` → `<prefijo>.local.<profile>` para los seis prefijos
   (contextos, destinos, operaciones, comprobantes, historial borrado y cola de
   sync). Es idempotente, nunca borra el origen y se ejecuta ANTES de construir
   el grafo (los repositorios locales cargan su estado al construirse).
7. **PIN como credencial local.** `LocalProtectionRepository` persiste únicamente
   un verificador PBKDF2-HMAC-SHA256 (120 000 iteraciones, sal aleatoria de
   16 bytes, clave de 32 bytes) con límite de intentos y retardo progresivo.
   El PIN nunca se guarda en claro y no es la clave de cifrado de los datos
   (ver limitaciones abajo).
8. **La nube es opcional.** Sin cuenta: la escritura local termina igual, la
   cola puede acumular mutaciones pendientes, pero el motor de sync no se
   arranca y su indicación se oculta. Con cuenta y sync habilitado, el motor
   opera como hasta ahora.

## Consecuencias

- El dispositivo tiene, en V1, un único perfil local. El aislamiento entre
  perfiles es una propiedad de diseño (`profileScopedKey` + `LocalProfileScope`)
  verificada por pruebas, aunque V1 no expone crear un segundo perfil.
- Cambió el contrato de almacenamiento. Las instalaciones previas se migran en
  el primer arranque; las claves antiguas se conservan hasta validar.
- El estado de sesión remota deja de ser una condición de compilación para la
  sesión local: `AuthState` y `LocalAccessMode` son estados distintos y no se
  mezclan.

## Limitaciones y riesgos abiertos

- **Los datos NO están cifrados en reposo con el PIN.** El PIN es una puerta de
  acceso local, no una clave derivada de los datos. `LocalProtectionRepository.clear()`
  solo retira el desbloqueo. Cifrar datos con una clave derivada del PIN
  implica un diseño de recuperación (olvidó el PIN) y de destrucción de claves
  que debe resolverse por separado; no se puede prometer recuperación de datos
  que nadie puede descifrar.
- **Biometría nativa.** `BiometricPrompt` (Android) y `LocalAuthentication`
  (iOS) quedan pendientes; el PIN es el mecanismo de desbloqueo implementado.
  La taxonomía de errores específica de biometría (cancelada por el usuario,
  no inscrita, bloqueo temporal del sensor, error recuperable o no) tampoco
  está implementada: `LocalAccessError` hoy solo cubre la política del PIN.
  Deberá extenderse junto con el desbloqueo biométrico.

  > **Actualización (2026-10-15):** parcialmente implementado. La taxonomía
  > existe ya en `BiometricAvailability`/`BiometricAuthResult` (dominio) y la
  > disponibilidad real se consulta en Android vía `BiometricManager`
  > (`platformBiometricGate`). El atajo aparece en `LocalLockScreen` solo
  > cuando está disponible e inscrita, con el PIN como vía principal y
  > fallback ante cualquier fallo (fail-closed). Sigue pendiente el prompt
  > ligado a la Activity (`authenticate()` responde `Unavailable`) y
  > `LocalAuthentication` en iOS; ambos requieren dispositivo físico. El
  > estado autoritativo vive en [STATE.md](../STATE.md).
- **Directorio heredado de comprobantes.** La migración copia las colecciones
  serializadas, no los bytes de los archivos de comprobantes de la ruta
  antigua (`<base>/comprobantes/<user_id>/`), que ahora es
  `<base>/comprobantes/<profile_uuid>/`. Se conserva la ruta antigua.
- **Vínculo de cuenta.** La opción Cuenta y respaldo permite entrar/salir de la
  cuenta remota sin tocar los datos locales; el enlace explícito perfil local ↔
  cuenta remota (con su semántica de respaldo por dispositivo) no está
  implementado. Desconectar no borra datos locales.
- **Sin recuperación del PIN.** Si el usuario olvida el PIN no hay canal de
  recuperación: reinstalar la app vuelve a crear un perfil local (no accede a
  los datos anteriores cifrados, que además no lo están, pero sí se inicia en
  limpio). Documentado en pantalla.
