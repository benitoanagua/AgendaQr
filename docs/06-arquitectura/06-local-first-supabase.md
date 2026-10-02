# Supabase + Local-first — configuración V1

Agenda QR funciona local-first. La persistencia local y la cola durable siguen siendo la base de la experiencia offline; Supabase aporta identidad, respaldo y sincronización cuando existe conectividad.

## Configuración local Android

No guardar credenciales en Git.

La integración Android acepta estas dos fuentes, en este orden:

1. Gradle properties: `SUPABASE_URL` y `SUPABASE_PUBLISHABLE_KEY`.
2. Variables de entorno con esos mismos nombres.

Para desarrollo local se recomienda usar `~/.gradle/gradle.properties` del usuario, no el `gradle.properties` del repositorio:

```properties
SUPABASE_URL=https://<project-ref>.supabase.co
SUPABASE_PUBLISHABLE_KEY=<publishable-key>
```

La clave debe ser la clave pública/publishable del proyecto. Nunca usar `service_role` en Android, iOS ni en el repositorio.

## Configuración de CI

El workflow de Android lee:

- `SUPABASE_URL` desde GitHub Actions Secret.
- `SUPABASE_PUBLISHABLE_KEY` desde GitHub Actions Secret.

Los secretos no se imprimen ni se escriben en archivos del repositorio.

## Requisitos de Supabase

Para la validación end-to-end se necesita un proyecto Supabase con:

- Auth habilitado.
- PostgreSQL con las tablas de Agenda QR.
- RLS habilitado y políticas por `user_id`.
- Storage privado para comprobantes.
- Policies de Storage restringidas al usuario.

## Estrategia offline

Sin conectividad, Agenda QR debe poder:

- consultar datos locales;
- crear y editar destinos;
- registrar PAGO/COBRO;
- guardar comprobantes;
- consultar historial;
- encolar mutaciones pendientes.

Con conectividad, la cola sincroniza hacia Supabase y reintenta las mutaciones pendientes.

La autenticación remota sigue siendo la fuente de identidad. No se implementa una identidad paralela independiente para V1.

## Validación

La validación se divide en dos niveles:

1. CI sin credenciales reales: compilación y pruebas unitarias.
2. Entorno configurado: autenticación real, RLS, PostgreSQL, Storage y sincronización offline/online.

El objetivo de esta fase no es sustituir el almacenamiento local por Supabase, sino comprobar que ambos trabajan como una arquitectura local-first.
