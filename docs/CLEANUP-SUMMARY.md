# Resumen de consolidación UI/UX

**Fecha:** 2026-10-17  
**Rama:** `consolidate/cleanup-20261017`  
**Autoridad visual:** ADR-0022 y `05-design-system/00-contrato-normativo-diseno.md`

## Estadísticas

- **Archivos de código muerto eliminados:** 0
- **Líneas de código muerto eliminadas:** 0
- **Líneas de acción duplicada eliminadas:** 2
- **Marcadores técnicos de deuda resueltos:** 0
- **Documentos archivados o eliminados:** 0
- **Acciones duplicadas corregidas:** 1
- **Pruebas comunes corregidas para portabilidad KMP:** 1

No se contabiliza como deuda la palabra española «TODO» usada con el sentido
de «todo/completo», los nombres de dominio `MAX_ATTEMPTS` ni menciones
históricas a versiones previas.

## Categorías revisadas

- **Código muerto:** la búsqueda de composables públicos de `core/ui` encontró
  dos elementos sin caller (`AmSuccessCheck`, `AmSuccessBurst`). El contrato
  vigente los reserva para el feedback de éxito S12, así que se conservaron.
  No se eliminó código sin confirmar que fuera ajeno a un contrato activo.
- **Código transitorio:** no se encontraron marcadores técnicos de deuda activos en
  Kotlin dentro de `core`, `feature`, `shared` o `androidApp`.
- **Código huérfano:** no se retiraron implementaciones por el solo hecho de
  que ADR-0020/0021 estén parcialmente suplantados; ADR-0022 conserva
  expresamente sus decisiones no reemplazadas.
- **Código zombie:** no se encontraron `NotImplementedError`,
  `UnsupportedOperationException`, `@Ignore` ni `@Disabled` en archivos Kotlin
  de producción y pruebas.
- **Documentación:** se alinearon la guía de implementación y los comentarios
  de `core/ui` con ADR-0022. Los ADR históricos y la evaluación tipográfica no
  adoptada permanecen como contexto referenciado.

## Cambios funcionales relacionados

La suite encontró que Inicio exponía «Traer un QR» tanto en la rejilla como en
el estado vacío de una instalación nueva. Se retiró el CTA redundante del
estado vacío; la rejilla sigue siendo el acceso a la misma acción. Se conserva
la presentación del estado vacío y todos los flujos de negocio.

El build multiplataforma también reveló que `AmSchemeTest` dependía de
`javaClass`, no disponible en Kotlin/Native. El mensaje de aserción ahora usa
etiquetas explícitas «light»/«dark» sin reflexión; las verificaciones de
contraste no cambian.

La ejecución completa de `build` encontró además errores de lint preexistentes
en el acceso biométrico Android: se usaba el `BiometricManager` del framework
en vez del API de compatibilidad de AndroidX y faltaba declarar
`USE_BIOMETRIC`. Se cambió a `BiometricManager.from(context)` y se declaró el
permiso en el manifiesto del módulo de datos; `lintDebug` y la prueba de mapeo
de errores biométricos pasan.

## Validación

- Gates de arquitectura, documentación, cumplimiento del sistema de diseño,
  fixtures, scripts y área táctil: pasaron en la validación final.
- Tests de `:core:ui` y `:feature:destinations:presentation`: pasaron; incluye
  la suite completa de presentación, con 365 pruebas.
- `:androidApp:assembleDebug`: pasó.
- Compilación de tests comunes iOS (`:core:ui:compileTestKotlinIosArm64`):
  pasó después del ajuste portátil de `AmSchemeTest`.
- `:feature:destinations:data:lintDebug` y `BiometricPromptErrorMappingTest`:
  pasaron después de la corrección AndroidX.
- El primer intento de `verifyDocs` detectó que el propio informe repetía el
  nombre de un marcador que el gate prohíbe; se reformuló sin incluir patrones
  que parezcan deuda y el gate pasó en la validación final.
- Build completo (`./gradlew build`): pasó después de las correcciones KMP y
  de lint; lint reportó advertencias no bloqueantes.

## Alcance del inventario

La búsqueda estática cubrió patrones de deuda y excepciones en Kotlin y
referencias a las funciones públicas composables de `core/ui`. No equivale a
una prueba formal de alcanzabilidad para cada función/propiedad privada del
repositorio; por eso el informe no declara «cero código muerto» de forma
absoluta.
