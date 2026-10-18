# ADR-0023 — Adopción fiel de TDesign Mobile (UI y UX)

**Estado:** ACEPTADO — 2026-10-17, autorización explícita del propietario
**Suplanta:** ADR-0020, ADR-0021 y ADR-0022 como autoridad visual; no altera sus registros históricos.
**Relaciona:** especificación UX/UI V1.13, contrato normativo de diseño, ADR-0007 (capturas), ADR-0008 (voz), ADR-0015 (jerarquía financiera).

## 1. Contexto

La implementación visual AgendaQr heredó decisiones de identidad propia y de dirección Super-App que ya no coinciden con el sistema de diseño deseado. El propietario autorizó expresamente cambiar la composición y los patrones de interacción de las pantallas a TDesign Mobile, manteniendo congeladas la intención, el modelo mental, las reglas de negocio, el orden de flujos, la navegación conceptual/back stack, el contrato local-first, el copy funcional, la guía de voz y la accesibilidad.

La fuente verificable es el paquete oficial `tdesign-mobile-vue@1.16.2`, en sus tokens y estilos de componentes Mobile. AgendaQr adopta esos valores y especificaciones en `core/ui`; TDesign no se añade como dependencia de ejecución.

## 2. Decisión

1. TDesign Mobile 1.16.2 es la única autoridad visual: paletas clara/oscura, roles semánticos, superficies, radios, espaciado, tipografía, motion, elevación y especificaciones de componentes.
2. `core/ui` contiene los tokens y componentes canónicos. Las pantallas solo componen sus APIs para adoptar los patrones descritos en la especificación UX/UI V1.13.
3. La geometría y los valores son los oficiales. El uso de Compose Multiplatform/material3 como puente de implementación no convierte sus defaults ni el tema de plantilla en fuente visual.
4. Se conserva el área táctil interactiva mínima de 48 dp, el foco de teclado/D-pad y la háptica de plataforma. El estado presionado usa los tokens activos de TDesign, sin gradiente, escala ni spring.
5. Se mantienen los cuatro destinos raíz, el back stack, las reglas de acceso local, los flujos, estados de dominio, copy funcional y accesibilidad existentes. No se incorporan gestos o componentes que añadan capacidad de producto.
6. Los iconos se migran al catálogo TDesign Icons a través de `AmIcon`/`AmIcons`, solo después de verificar la correspondencia de cada glifo. La licencia MIT y su atribución se registran antes de distribuir los recursos.

## 3. Desviaciones autorizadas

| ID | Desviación | Resolución |
|---|---|---|
| D-01 | `AmAccents` contiene 12 tonos deterministas para identidad de contexto; TDesign define cuatro familias cromáticas y grises. | Se conservan los 12 tonos y el hash FNV-1a. Es la única paleta cromática fuera de TDesign; sus pares AA no se alteran. |
| D-02 | Algunos colores oficiales no alcanzan contraste para texto, glifo significativo o relleno con texto encima. | Se usa el siguiente escalón de la misma paleta TDesign que cumple ≥4.5:1 para texto y ≥3:1 para elementos no textuales; no se crean colores. |
| D-03 | Los botones y otros controles TDesign pueden medir menos de 48 dp. | Se conserva su tamaño visual oficial y se amplía el target interactivo a ≥48 dp. |
| D-04 | El token oficial `body-extraSmall` es 10 sp, por debajo del piso de texto de AgendaQr. | El valor fuente se documenta; no se aplica a texto visible. El menor tamaño de texto compuesto es el siguiente tamaño oficial, 12 sp, manteniendo el piso de 11 sp. |
| D-05 | TDesign Mobile no ofrece un equivalente de rail adaptativo. | Se conserva `AmNavRail` en tablet/plegables, con tokens TDesign y sin alterar la navegación conceptual. |
| D-06 | CSS define sombras de múltiples capas y Compose no las reproduce directamente. | La elevación se aproxima con la capa dominante de TDesign o `drawBehind`; la diferencia se registra en el contrato y el gate. |
| D-07 | Las hojas nativas `Dialog`/`ModalBottomSheet` no son verificables de forma estable con Robolectric. | `AmBottomSheet` se implementa como overlay en el mismo árbol (scrim y panel animado). Si no se puede verificar, se mantiene `AmDialog` con lista y se documenta el caso. |
| D-08 | El set de fuentes de TDesign incluye familias específicas de plataforma. | Se usa la fuente del sistema; no se empaquetan fuentes ni se altera el texto. |

Los estados de error, éxito y advertencia mantienen contraste con la paleta oficial. El lienzo QR sigue siendo blanco. Las diferencias entre APIs de plataforma se limitan a los servicios nativos existentes y no cambian la intención ni el flujo.

## 4. Consecuencias

- ADR-0020, ADR-0021 y ADR-0022 quedan como historia y ya no autorizan cambios visuales actuales.
- La especificación UX/UI pasa a V1.13: §4 define las composiciones autorizadas y §12 los patrones y reglas compartidas. La numeración vigente de esa especificación termina en §14; su fuente actual no contiene §16. El apartado de referencias por pantalla §16 pertenece al contrato normativo y se actualizará allí, sin crear una sección duplicada en la especificación.
- El contrato normativo, sus excepciones/gate, los tests, los componentes, las pantallas y las licencias se migran en tareas separadas de esta decisión.
- `tdesign-mobile-vue` y `tdesign-icons-svg` no se añaden como dependencias de la app.
- Capturas visuales previas y su procedencia se conservan en `docs/04-ux/visual-hashes/before/`; capturas nuevas se revisan en claro/oscuro y se fijan por el mecanismo de ADR-0007.

## 5. Criterios de aceptación

1. Cada token visual adoptado coincide con `tdesign-mobile-vue@1.16.2` y su test cita el paquete como fuente.
2. Los pares de contraste claro/oscuro alcanzan los pisos AA establecidos usando solo la paleta TDesign, salvo D-01.
3. El gate de diseño pasa; cada excepción aparece tanto en el contrato normativo como en `exceptionRegistry`.
4. Todos los targets visibles e interactivos conservan ≥48 dp, el texto mantiene ≥11 sp y los layouts se validan con 320/360 dp y fuente 200 %.
5. Cada pantalla de §4 sigue el patrón indicado o tiene una desviación con causa; ninguna añade acción o cambia una regla congelada.
6. Se registran las licencias MIT de TDesign Mobile y TDesign Icons y no se empaquetan sus librerías Vue/CSS como dependencia de runtime.
7. Capturas antes/después por pantalla, tema, ancho y escala no muestran recortes ni solapes; las cuatro capturas de regresión del 16-oct se conservan y verifican.
8. Los tests de navegación/back stack pasan sin cambios funcionales.
9. CI, pruebas por plataforma y validación de runtime/dispositivo se reportan con su resultado real; una compilación no se presenta como validación runtime.
