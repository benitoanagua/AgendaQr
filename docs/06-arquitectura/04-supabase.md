# Supabase — fundación V1

Supabase es el backend elegido para el MVP. La primera integración se hace por capas y no sustituye de golpe los repositorios locales.

## Alcance de esta fase

- PostgreSQL para datos de usuario.
- Storage privado para archivos de comprobantes.
- Auth como identidad de usuario.
- RLS para que cada usuario solo pueda acceder a sus propios registros.
- Cliente Kotlin Multiplatform encapsulado en `feature:destinations:data`.

## Dependencias

Se usa `supabase-kt` 3.2.4 en esta primera fase porque la base actual del proyecto usa Kotlin 2.2.20. La rama 3.2.x documenta compatibilidad con Kotlin 2.2.20 y Ktor 3.3.x; no se fuerza todavía una actualización global del toolchain.

El SDK expone módulos para PostgREST, Auth y Storage. El cliente multiplataforma requiere un engine Ktor por target.

## Configuración

Android recibe `SUPABASE_URL` y `SUPABASE_PUBLISHABLE_KEY` como Gradle properties.

iOS lee las mismas claves desde `Info.plist`.

No se guarda una secret/service-role key en la aplicación. Las operaciones administrativas deben ejecutarse en un entorno confiable.

## Seguridad

Las tablas llevan `user_id` y RLS.

Los comprobantes se almacenan en el bucket privado `comprobantes`, bajo una ruta que comienza por el UUID del usuario. Las policies de Storage restringen acceso a esa primera carpeta.

El cliente móvil usa la publishable key. La autorización real de los datos depende de Auth + RLS.

## Evolución local-first

1. Cliente Supabase y configuración.
2. Auth como identidad del usuario.
3. Destinations remoto.
4. Operations remoto.
5. Comprobantes: metadata + Storage.
6. Cola durable y recuperación de sincronización.
7. Validación end-to-end offline/online.

La persistencia local no se retira después de alcanzar paridad: es parte de la arquitectura V1 local-first. Supabase complementa la experiencia con identidad, respaldo y sincronización.
