# Sincronización local-first — seguridad de identidad

La identidad local (perfil local) y la identidad remota (cuenta Supabase) son
dos cosas distintas. La raíz de las claves de almacenamiento local es el
**perfil local**, nunca el `user_id` de un servicio externo: la app debe abrirse
y usarse en el dispositivo sin cuenta y sin red (ADR-0016).

## Namespace por perfil local

Cada colección local se guarda bajo una clave que incluye el identificador del
perfil local activo y el segmento distintivo `.local.`:

`<prefijo>.local.<profile_id>`

- `agendaqr.destinations.v1.local.<profile_id>`
- `agendaqr.contexts.v1.local.<profile_id>`
- `agendaqr.operations.v1.local.<profile_id>`
- `agendaqr.comprobantes.v1.local.<profile_id>`
- `agendaqr.deleted_operations.v1.local.<profile_id>`
- `agendaqr.sync.queue.v1.local.<profile_id>`

La resolución la hace `profileScopedKey(prefijo)` sobre `LocalProfileScope`,
que se activa una sola vez al arrancar, antes de construir cualquier
repositorio. Esto evita que:

- un dispositivo muestre los datos de otro perfil;
- un identificador remoto arbitrario se confunda con identidad local;
- las colecciones queden mezcladas en un espacio de nombres global.

El segmento `.local.` también permite distinguir, en un mismo almacén, las
claves heredadas (`<prefijo>.<user_id>`) de las actuales.

## Archivos locales de comprobantes

Los bytes de los comprobantes se guardan en un directorio por perfil local:

`<archivos de la app>/comprobantes/<profile_id>/<comprobante_id>.<extensión>`

Dos perfiles no comparten nombres de archivo.

## Cuenta remota opcional

Los datos locales viven bajo el perfil local **con o sin cuenta**. Conectar una
cuenta habilita el respaldo y la sincronización; no mueve los datos. El
aislamiento del lado servidor sigue determinado por las políticas RLS de
Supabase (`auth.uid()`), que no se debilitan con este cambio.

## Migración de instalaciones existentes

Las instalaciones anteriores guardaban sus colecciones bajo
`<prefijo>.<user_id>`. El primer arranque detecta esos datos **escaneando las
claves locales** (sin crear el cliente remoto, sin red y sin depender de que la
sesión antigua siga viva) y los copia al namespace del perfil local.

Reglas de la migración (`LegacyLocalDataMigration`):

- idempotente: se puede ejecutar más de una vez;
- no borra el origen: las claves heredadas se conservan hasta validar;
- no pisa un destino que ya tenga datos;
- se ejecuta antes de construir el grafo, porque los repositorios locales
  cargan su estado en la construcción.

Los directorios heredados de archivos de comprobantes NO se copian: se
conservan en su ruta antigua (limitación documentada en ADR-0016).

## Política de bloqueo

La app se bloquea al pasar a segundo plano (`Lifecycle.Event.ON_STOP`) y vuelve
a pedir el PIN al reanudarse. Sin PIN configurado no hay nada que proteger y el
primer arranque no se ve afectado.

## Importante

- **Los datos locales NO están cifrados en reposo con el PIN.** El PIN es una
  puerta de acceso local, no una clave derivada de los datos; el verificador
  guardado es PBKDF2-HMAC-SHA256 con sal aleatoria (ver `LocalProtectionStore`).
- Desconectar la cuenta remota no borra datos locales: siguen bajo el perfil
  local del dispositivo.
- No se garantiza el borrado criptográfico de los archivos: eliminar un
  comprobante quita la referencia local y el archivo, sin garantía criptográfica
  de irreversibilidad.
