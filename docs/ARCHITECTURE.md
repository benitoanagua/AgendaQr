# Agenda QR — Arquitectura

Describe la implementación real: módulos, arranque local-first, identidad local
y protección con PIN, persistencia y namespaces, migración de datos heredados,
repositorios locales y remotos, cola de sincronización, Supabase, hosts de
plataforma y límites de seguridad. Consolida los antiguos documentos de
`06-arquitectura`.

## 1. Overview

Agenda QR es una aplicación Kotlin Multiplatform con dos hosts de plataforma y
una capa compartida.

```text
androidApp/  → aplicación Android
      │
shared/      → runtime/capa KMP compartida
      │
feature/*    → dominio, datos y presentación
      │
core/*       → capacidades transversales

iosApp/      → aplicación iOS/Xcode
```

Regla de dependencia:

```text
presentation → domain
presentation → data (solo mediante wiring permitido)
data → domain
platform host → shared/features
```

El dominio no conoce Compose, Android, UIKit, SwiftUI ni almacenamiento concreto.

`androidApp` e `iosApp` son deliberadamente simétricos como hosts de plataforma:
el primero es un módulo Gradle Android; el segundo es el proyecto/host Xcode que
consume el framework KMP. Android se valida primero; iOS se mantiene
compilable/estructuralmente soportado, pero no se declara validado hasta una
ejecución real con Xcode.

## 2. Fuentes de verdad y precedencia

1. **Agenda QR — autoridad de producto.** Única fuente para producto,
   requisitos, dominio, alcance, decisiones y reglas de negocio. Cubre también
   el comportamiento funcional UX (contrato UX/UI V1 congelado).
2. **Identidad visual propia de AgendaQr.** Define paleta, superficies, texto,
   estados, radios, densidad, controles, iconografía y feedback, con patrones
   de apps móviles chinas de consumo como referencia por pantalla. Android e
   iOS definen las convenciones de navegación y comportamiento. El contrato
   normativo es `docs/05-design-system/00-contrato-normativo-diseno.md`;
   [ADR-0020](07-decisiones/ADR-0020-identidad-visual-agendaqr.md) y
   [ADR-0021](07-decisiones/ADR-0021-referencias-apps-chinas-por-pantalla.md)
   lo respaldan. Ant Design Mobile y Vant no son referencia.
3. **WaraWerse — referencia técnica.** Solo para Kotlin Multiplatform, Compose,
   modularización, Gradle/build logic y prácticas de testing. No introduce
   lógica de negocio.

Matriz por área:

| Área | Fuente de verdad | Uso |
|---|---|---|
| Producto | Agenda QR | obligatorio |
| Dominio | Agenda QR | obligatorio |
| Requisitos | Agenda QR | obligatorio |
| Alcance y límites | Agenda QR | obligatorio |
| Decisiones | Agenda QR | obligatorio |
| UX funcional | Agenda QR | obligatorio |
| UI/UX visual | Contrato normativo AgendaQr | obligatorio |
| Tokens | Contrato normativo AgendaQr | obligatorio |
| Componentes y patrones | Contrato normativo AgendaQr | obligatorio |
| Accesibilidad y motion | Contrato normativo AgendaQr | obligatorio |
| Navegación y comportamiento | Android / iOS | obligatorio |
| Kotlin/KMP | WaraWerse | referencia técnica |
| Compose | WaraWerse | referencia técnica |
| Build logic | WaraWerse | referencia técnica |
| Testing practices | WaraWerse | referencia técnica |

Resolución de conflictos:

1. Negocio: Agenda QR gana.
2. UX funcional: Agenda QR gana.
3. Visual, componentes, tokens, motion y accesibilidad: el contrato normativo y
   los ADR aprobados ganan; las guías de implementación explican el cómo, no
   redefinen el qué.
4. Arquitectura técnica: WaraWerse es solo referencia.
5. Una implementación no puede introducir una variante visual local fuera de la
   referencia.

Regla de implementación: el código de producto combina reglas de negocio de
Agenda QR con la referencia visual vigente. Una discrepancia visual se resuelve
a favor de la referencia. Cuando una pantalla necesite una composición nueva, se
compone primero con los componentes existentes del sistema.

## 3. Kotlin way — política y excepciones

El repositorio NO contiene scripts shell/JS/TS/Python: toda la lógica de build,
verificación y testing es Kotlin/Gradle. La tarea `verifyNoScripts` (en
`verifyAgendaQrArchitecture`) lo garantiza.

Excepciones (no son scripts de producto):

| Archivo | Razón |
|---|---|
| `gradlew` | Wrapper estándar de Gradle (posix); requerido para ejecutar builds. |
| `gradlew.bat` | Wrapper estándar de Gradle (Windows). |

Mapeo de scripts eliminados → tareas Gradle:

| Antes (script) | Después (tarea Gradle) |
|---|---|
| gate de design system (script de verificación retirado con el sistema anterior; el gate vive hoy en `build-logic`) | `./gradlew verifyDesignSystemCompliance` + `verifyControlMinSize` |
| `docs/04-ux/visual-hashes/verify.sh` | `./gradlew recordVisualHashes` + `verifyVisualHashes` |
| `supabase/tests/run-acceptance.sh` | `DATABASE_URL=... ./gradlew :supabase:acceptance:test` |

## 4. Arranque local-first

Agenda QR funciona local-first. La persistencia local y la cola durable son la
base de la experiencia offline; la identidad del dispositivo es el **perfil
local**, que se crea sin cuenta y sin red. El arranque no espera red, sesión ni
token. Supabase aporta respaldo y sincronización cuando, y solo cuando, el
usuario conecta una cuenta opcional
([ADR-0016](07-decisiones/ADR-0016-identidad-local-sin-cuenta.md)).

Sin conectividad, Agenda QR puede: consultar datos locales; crear y editar
destinos; registrar PAGO/COBRO; guardar comprobantes; consultar historial; y
encolar mutaciones pendientes.

La conectividad es una condición para **sincronizar**, nunca para abrir la app
ni para escribir localmente. La cola se drena hacia Supabase con backoff y
reintentos **solo cuando hay una cuenta conectada y remoto configurado**; sin
cuenta, las mutaciones siguen encoladas en el dispositivo y la indicación de
sincronización se oculta para no sugerir un envío que no va a ocurrir.

## 5. Identidad local y seguridad de identidad

La identidad local (perfil local) y la identidad remota (cuenta Supabase) son
dos cosas distintas. La raíz de las claves de almacenamiento local es el
**perfil local**, nunca el `user_id` de un servicio externo.

### Namespace por perfil local

Cada colección local se guarda bajo una clave que incluye el identificador del
perfil local activo y el segmento distintivo `.local.`:

```text
<prefijo>.local.<profile_id>
```

- `agendaqr.destinations.v1.local.<profile_id>`
- `agendaqr.contexts.v1.local.<profile_id>`
- `agendaqr.operations.v1.local.<profile_id>`
- `agendaqr.comprobantes.v1.local.<profile_id>`
- `agendaqr.deleted_operations.v1.local.<profile_id>`
- `agendaqr.sync.queue.v1.local.<profile_id>`

La resolución la hace `profileScopedKey(prefijo)` sobre `LocalProfileScope`, que
se activa una sola vez al arrancar, antes de construir cualquier repositorio.
Esto evita que: un dispositivo muestre los datos de otro perfil; un
identificador remoto arbitrario se confunda con identidad local; o las
colecciones queden mezcladas en un espacio de nombres global.

El segmento `.local.` también permite distinguir, en un mismo almacén, las claves
heredadas (`<prefijo>.<user_id>`) de las actuales.

### Archivos locales de comprobantes

Los bytes de los comprobantes se guardan en un directorio por perfil local:

```text
<archivos de la app>/comprobantes/<profile_id>/<comprobante_id>.<extensión>
```

Dos perfiles no comparten nombres de archivo.

### Cuenta remota opcional

Los datos locales viven bajo el perfil local **con o sin cuenta**. Conectar una
cuenta habilita el respaldo y la sincronización; no mueve los datos. El
aislamiento del lado servidor sigue determinado por las políticas RLS de
Supabase (`auth.uid()`), que no se debilitan con este cambio.

### Migración de instalaciones existentes

Las instalaciones anteriores guardaban sus colecciones bajo
`<prefijo>.<user_id>`. El primer arranque detecta esos datos **escaneando las
claves locales** (sin crear el cliente remoto, sin red y sin depender de que la
sesión antigua siga viva) y los copia al namespace del perfil local.

Reglas de la migración (`LegacyLocalDataMigration`):

- idempotente: se puede ejecutar más de una vez;
- no borra el origen: las claves heredadas se conservan hasta validar;
- no pisa un destino que ya tenga datos;
- se ejecuta antes de construir el grafo, porque los repositorios locales cargan
  su estado en la construcción.

Los BYTES de los comprobantes heredados (`<base>/comprobantes/<user_id>/`)
también se migran (`LegacyComprobanteBytesMigration`), con las mismas reglas
de seguridad más la tolerancia a fallos por archivo:

- idempotente: un archivo que ya existe en el destino no se copia ni se
  pisa (la versión del usuario bajo el perfil local gana);
- no borra el origen: la ruta heredada se conserva intacta;
- fallo parcial sin aborto: un archivo que falla no detiene los demás y se
  recupera en el próximo arranque (reintento de la migración);
- copia atómica-en-el-destino (temporal + rename) para no dejar medios
  archivos ante una interrupción;
- nunca escribe fuera de los dos directorios implicados.

La lógica vive en común sobre el puerto `LegacyBytesFs` y está probada en
`LegacyComprobanteBytesMigrationTest`; las plataformas solo aportan rutas y
operaciones nativas (`java.io.File` / `NSFileManager`).

### Política de bloqueo

La app se bloquea al pasar a segundo plano (`Lifecycle.Event.ON_STOP`) y vuelve
a pedir el PIN al reanudarse. Sin PIN configurado no hay nada que proteger y el
primer arranque no se ve afectado.

### Biometría local (atajo opcional, fail-closed)

La biometría es un atajo de desbloqueo, nunca un mecanismo aparte de
protección. El contrato vive en el dominio (`BiometricGate`,
`BiometricAvailability`, `BiometricAuthResult`) y cada plataforma lo
implementa en `data`:

- **Android:** `androidx.biometric.BiometricPrompt` real, ligado a la
  `FragmentActivity` del host (`MainActivity` extiende `FragmentActivity`;
  `BiometricPromptHost` retiene la Activity en primer plano y se libera en
  `onDestroy` solo si sigue siendo la misma instancia). Disponibilidad vía
  `BiometricManager.canAuthenticate(BIOMETRIC_WEAK)`: clase 2+ (el atajo es
  conveniencia; el PIN es la puerta fuerte y exigir clase 3 dejaría sin atajo
  a dispositivos con biometría de clase 2). El prompt usa botón negativo
  «Usar PIN», confirmación explícita y guardia de un solo prompt en vuelo;
  la cancelación de la corrutina cancela el prompt.
- **iOS:** `LocalAuthentication`/`LAContext` real (`canEvaluatePolicy` +
  `evaluatePolicy`; `invalidate()` al cancelar). `biometryType` distingue
  sin-hardware de no-inscrita.

La UI ofrece el atajo en `LocalLockScreen` solo cuando la disponibilidad es
`Available`; el PIN es siempre la vía principal y el fallback visible ante
cualquier fallo, cancelación o ausencia de hardware. Solo
`BiometricAuthResult.Success` desbloquea: el mapeo de códigos de error se
verifica en JVM con la invariante «ningún error desbloquea»
(`BiometricPromptErrorMappingTest`). El prompt sobre hardware real
requiere dispositivo (ver STATE.md §2). La biometría no cifra datos ni
sustituye al verificador PBKDF2 del PIN.

### Modo de demostración aislado

El modo de demostración permite explorar la app con datos ficticios
deterministas (`DemoData`/`DemoSeeder`):

- Perfil fijo `demo-profile-v1`: todas las colecciones viven bajo
  `<prefijo>.local.demo-profile-v1` y nunca tocan el namespace del perfil
  real del usuario.
- Entrada explícita: botón «Explorar demostración» en onboarding/lock;
  nunca se carga automáticamente.
- Idempotente: `seedIfEmpty` no duplica al reentrar y no pisa datos
  preexistentes del namespace demo.
- Sin sincronización: el grafo de sesión fuerza `remoteConfigured = false`
  para el perfil demo (sin recovery, sin UI de sync, sin cola hacia
  Supabase) — los datos ficticios no salen del dispositivo.
- Sin PIN/cuenta: salir de la demostración restaura el flujo de acceso real
  (onboarding o lock) sin mezclar información.

### Importante

- **Los datos locales NO están cifrados en reposo con el PIN.** El PIN es una
  puerta de acceso local, no una clave derivada de los datos; el verificador
  guardado es PBKDF2-HMAC-SHA256 con sal aleatoria (ver `LocalProtectionStore`).
- Desconectar la cuenta remota no borra datos locales: siguen bajo el perfil
  local del dispositivo.
- No se garantiza el borrado criptográfico de los archivos: eliminar un
  comprobante quita la referencia local y el archivo, sin garantía criptográfica
  de irreversibilidad.

## 6. Supabase — fundación

Supabase es el backend opcional. La integración se hace por capas y no sustituye
a los repositorios locales.

- PostgreSQL para datos de usuario.
- Storage privado para archivos de comprobantes.
- Auth como identidad de usuario remota (opcional).
- RLS para que cada usuario solo pueda acceder a sus propios registros. La
  migración activa RLS explícitamente en TODAS las tablas cubiertas, incluida
  `storage.objects` (defense in depth). La prueba `rls_isolates_rows_between_two_users`
  en `supabase/acceptance` verifica con DOS usuarios y un rol NO superusuario:
  lecturas/escrituras cruzadas rechazadas, FK compuesto `(context_id, user_id)` y
  storage por carpeta de usuario.
- Cliente Kotlin Multiplatform encapsulado en `feature:destinations:data`.

Dependencias: se usa `supabase-kt` 3.2.4 porque la base actual usa Kotlin
2.2.20. El SDK expone módulos para PostgREST, Auth y Storage; el cliente
multiplatforma requiere un engine Ktor por target.

Seguridad: las tablas llevan `user_id` y RLS. Los comprobantes se almacenan en
el bucket privado `comprobantes`, bajo una ruta que comienza por el UUID del
usuario. El cliente móvil usa la publishable key; la autorización real depende de
Auth + RLS. **Nunca se incorpora una clave `service_role` a la aplicación.**

## 7. Repositorios remotos y espejo local-first

### Destinos

Los destinos usan una estrategia **local-first con espejo remoto**:

- el almacenamiento local sigue siendo la fuente inmediata para la UI;
- Supabase PostgreSQL conserva una copia por usuario;
- RLS limita el acceso a filas cuyo `user_id` coincide con `auth.uid()`;
- al crear el repositorio se intenta una sincronización remota → local;
- las altas, ediciones y eliminaciones se escriben primero localmente y luego se
  registran como mutación durable en la cola de sincronización;
- un fallo de red no bloquea la operación local;
- la próxima inicialización vuelve a intentar la descarga remota.

Conflictos: V1 usa la regla «gana el registro con `updatedAt` más reciente» para
la sincronización inicial; las mutaciones pendientes usan la cola durable. Una
escritura local realizada sin conexión queda encolada hasta su espejo remoto.

Mapeo actual: `Destination` contiene `QrAsset.encoded` (bytes de la imagen QR).
El esquema Supabase fue diseñado con `qr_raw_content` y `qr_kind`; mientras el
dominio no exponga esos campos, el adaptador remoto conserva `encoded` en
`qr_raw_content` y usa `UNKNOWN` para `qr_kind`. Es una compatibilidad explícita
del adaptador, no una reinterpretación del dominio.

Fuera de esta fase: resolución avanzada de conflictos, realtime y migración
destructiva del modelo QR.

### Comprobantes

Los comprobantes mantienen almacenamiento local para conservar el comportamiento
offline y, con sesión autenticada, se espejan en PostgreSQL (metadata) y en
Supabase Storage (archivo físico en el bucket privado `comprobantes`).

La ruta remota es `<user_id>/<comprobante_id>.<extension>`; la ruta local sigue
la misma convención por usuario. El acceso queda restringido por las policies de
Storage y por RLS de la tabla. No se genera URL pública: para descargar se usa el
acceso autenticado del bucket privado.

Flujo: (1) el archivo se guarda localmente; (2) se guarda el metadata local;
(3) se intenta subir el archivo a Storage; (4) se intenta guardar/actualizar el
metadata remoto; (5) un fallo remoto no elimina el respaldo local; (6) al iniciar
el repositorio, los comprobantes remotos se descargan y materializan en
almacenamiento local. Los reintentos usan la cola durable de mutaciones
pendientes.

## 8. Sincronización local-first

Agenda QR persiste primero en almacenamiento local y registra una mutación
durable para sincronización remota.

Estados:

- **PENDING:** pendiente de ejecución.
- **PROCESSING:** tomado por el procesador.
- **FAILED:** falló con error transitorio y espera el próximo intento (backoff
  exponencial acotado).
- **DEAD_LETTER:** error permanente (auth/RLS/validación) o sin reintentos; el
  drain no lo reclama — la recuperación requiere re-encolar (ADR-0004, propuesta).

Resolución de conflictos: `SyncConflictResolver` aplica Last-Write-Wins con
tolerancia de skew (1 s): gana el `updatedAt` mayor; en empate gana el servidor.
Un DELETE compite con su instante de borrado: solo se propaga si es más nuevo que
el remoto; si no, el registro remoto se aplica localmente (resurrección).
`syncFromRemote` nunca pisa entidades con mutaciones pendientes: el drain decide.

Recuperación: al iniciar la sesión, `SyncRecoveryCoordinator` ejecuta una
sincronización inmediata y luego revisa la cola periódicamente. Un elemento que
quedó en PROCESSING se recupera como PENDING antes de procesar.

Principio: la red no debe bloquear la captura de operaciones, destinos o
comprobantes. El dato local es la fuente inmediata para la UX; Supabase es la
réplica remota. La conectividad real del sistema operativo se integra mediante un
adaptador de plataforma; el coordinador funciona también sin depender de una API
de red específica.

## 9. Hosts de plataforma

**Android.** El host se denomina `androidApp/`, no `app/`, para hacer explícita
su responsabilidad y mantener simetría con `iosApp/`. `androidApp` contiene
únicamente responsabilidades propias de Android: lifecycle, `Activity`, intents,
permisos, camera/picker boundaries, Android Sharesheet, `FileProvider` e
inicialización de dependencias Android.

**iOS.** `iosApp` contiene el host SwiftUI/Xcode y los boundaries nativos de iOS.

Regla: ningún host de plataforma define reglas de negocio de Agenda QR. Esas
reglas permanecen en los módulos compartidos y en el dominio.

## 10. Configuración de Supabase

No guardar credenciales en Git. La integración Android acepta dos fuentes, en
este orden:

1. Gradle properties: `SUPABASE_URL` y `SUPABASE_PUBLISHABLE_KEY`.
2. Variables de entorno con esos mismos nombres.

Para desarrollo local se recomienda usar `~/.gradle/gradle.properties` del
usuario, no el `gradle.properties` del repositorio:

```properties
SUPABASE_URL=https://<project-ref>.supabase.co
SUPABASE_PUBLISHABLE_KEY=<publishable-key>
```

La clave debe ser la pública/publishable del proyecto. Nunca usar `service_role`
en Android, iOS ni en el repositorio. iOS lee las mismas claves desde
`Info.plist`. El workflow de Android lee `SUPABASE_URL` y
`SUPABASE_PUBLISHABLE_KEY` desde GitHub Actions Secrets; los secretos no se
imprimen ni se escriben en archivos del repositorio.

Requisitos de Supabase para la validación end-to-end: Auth habilitado;
PostgreSQL con las tablas de Agenda QR; RLS habilitado y políticas por
`user_id`; Storage privado para comprobantes; y policies de Storage restringidas
al usuario.

La validación se divide en dos niveles: (1) CI sin credenciales reales —
compilación y pruebas unitarias; (2) entorno configurado — autenticación real,
RLS, PostgreSQL, Storage y sincronización offline/online.

## 11. Límites de seguridad conocidos

- **Datos en reposo:** no cifrados con el PIN. El PIN es una puerta de acceso
  local (verificador PBKDF2-HMAC-SHA256 con sal aleatoria), no una clave
  derivada de los datos. La biometría tampoco cifra: es un atajo de
  desbloqueo sobre la misma puerta.
- **Prompt biométrico:** implementado en ambas plataformas; el runtime sobre
  hardware real (diálogo del sistema, éxito/fallo/cancelación) queda
  registrado como NO VERIFICADO hasta validar en dispositivo con biometría
  inscrita. El mapeo de errores y la invariante fail-closed sí están
  verificados en JVM.
- **Migración de archivos de comprobantes heredados:** implementada y probada
  (bytes + colecciones); el origen heredado se conserva intacto.
- **Cuenta remota:** el aislamiento remoto depende de RLS + Auth y no se
  debilita con la identidad local. Desconectar la cuenta no borra datos locales.
- **Modo de demostración:** aislado por namespace y sin sincronización; no
  constituye un perfil de usuario y no debe usarse para datos reales.

El estado de verificación por plataforma y las capacidades no implementadas se
mantienen en [STATE.md](STATE.md); las puertas y la estrategia de prueba, en
[QUALITY.md](QUALITY.md).
