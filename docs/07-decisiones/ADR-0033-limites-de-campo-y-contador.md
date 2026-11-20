# ADR-0033 — Límites de campo y contador de caracteres

**Estado:** APROBADA por delegación del propietario  
**Fecha:** 2026-11-20  
**Área:** UX/UI V1 (formularios: destino, actividad, contexto, auth) / core:ui  
**Decisor:** agente de ingeniería (delegación del propietario de producto)

## Contexto

Los editores de texto libre (nombre, categoría, nota, concepto, persona/entidad, correo, contraseña, importe) no definen límite de longitud en el dominio ni en la UI. El usuario puede escribir sin referencia de cuánto cabe, y no hay feedback cuando se acerca a un tope. G2 del cierre de gaps obliga a definir límites de dominio, validarlos y mostrar un contador "n/max" accesible.

## Decisión

1. **Límites de dominio** (constantes en `com.agendaqr.destinations.domain.FieldLimits`, capa `domain`, sin dependencias de UI):
   - nombre: 80
   - categoría: 40
   - nota: 500
   - concepto: 120
   - persona o entidad: 80
   - correo (auth): 254 (estándar RFC)
   - contraseña (auth): 128
   - importe (actividad): 20

2. **Validación en ViewModel/casos de uso**: se valida al guardar. Los valores ya guardados que superen el límite se conservan y solo se avisa al editar (no se bloquea datos existentes).

3. **UI**: `AqTextField` y `AqMultilineTextField` reciben `maxLength: Int? = null`. Cuando se informa, se muestra el contador "n/max" (`AqTextStyles.Support`, accesible, anunciado al acercarse al límite) y `InlineError` al exceder. Se adopta en editores de destino, actividad y contexto, y en los campos de auth.

4. **Pruebas**: pruebas de dominio para los límites, pruebas de `core:ui` para el contador, pruebas de presentación donde se use.

## Alternativas

- Sin límites: rechazado, deja al usuario sin referencia y sin feedback.
- Límite solo en UI sin dominio: rechazado, la validación debe vivir cerca del dato para no depender de la pantalla.

## Consecuencias

- Los formularios ganan un contador discreto bajo cada campo con `maxLength`.
- Al exceder el límite, el campo se marca con error y Guardar no avanza hasta corregir.
- `AqFieldState` se extiende con `maxLength` y `lengthExceeded` para que la molécula sea dueña del mensaje, como ya lo es de `errorMessage`.
