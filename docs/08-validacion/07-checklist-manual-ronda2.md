# Checklist manual — ronda 2 (dispositivos requeridos)

Todo lo que NO se puede validar desde el código/JVM. Cada item: pasos y
criterio de éxito. Marca con [ ] al validar y registra en
`06-resultados-y-evidencia.md`.

## TalkBack (Android, dispositivo físico)

1. [ ] Activar TalkBack; abrir S01: el orden de lectura es título →
   barra de búsqueda (un solo nodo "Buscar en Agenda QR", rol botón) →
   tiles (rol botón + etiqueta) → fila con su badge/acción.
   **Éxito:** cero nodos duplicados; cada tile anuncia su etiqueta.
2. [ ] Denegar el permiso de cámara con "No volver a preguntar": el
   banner anuncia el error y la acción "Abrir ajustes" abre los ajustes
   de la app. **Éxito:** reachable sin ver la pantalla.
3. [ ] Escanear un QR real: se oye "Código QR detectado" (liveRegion) y
   hay háptica ligera; el hint visible también cambia.
   **Éxito:** un solo anuncio por lectura.
4. [ ] Guardar un lote (S12 → Guardar reconocidos): al llegar a Saved se
   oye "Guardado" una única vez. Sincronizar con la cola pendiente y
   reconectar: al vaciarse se oye "Sincronización completada".
5. [ ] Eliminar un destino (diálogo): el FOCO inicial cae en "Cancelar"
   (destructive); confirmar anuncia "Eliminado".
6. [ ] Login con errores: "Ingresa un correo válido" se anuncia al
   intentar (liveRegion del error del campo).

## VoiceOver (iOS, requiere Xcode + dispositivo)

1. [ ] Mismos flujos de TalkBack (1–6). **Éxito:** paridad de anuncios
   (los anuncios viven en semántica común; iOS runtime sin validar hasta
   aquí).
2. [ ] Permisos de cámara iOS: pedir por primera vez (NotDetermined),
   denegar y reintentar: el banner ofrece "Abrir ajustes" y abre los
   ajustes del SO. **Nota:** el pedido explícito AVFoundation no está en
   los bindings precompilados de KMP (triage B7) — puede requerir
   wrapper nativo; validar con Xcode.
3. [ ] Escalado de texto Dynamic Type al máximo: S01/S07 sin recortes
   (equivalente iOS del test Round2FontScaleTest).

## Predictive back (Android 14+, dispositivo/emulador API 34+)

1. [ ] `enableOnBackInvokedCallback=true` ya está en el manifest. Con el
   gesto desde el borde: S04 → Inicio muestra la ANIMACIÓN de vuelta
   progresiva y el turnstile no la bloquea. **Éxito:** la vista previa
   del gesto funciona; el turno confirmado respeta reduced motion.
2. [ ] Durante un flujo con draft (S07 → S08): la vista previa del
   gesto NO consume el draft hasta soltar (el BackHandler interno
   decide). **Éxito:** Cancelar el gesto a mitad conserva el draft.
3. [ ] Raíz (S01): el gesto cierra la app sin animación rota.
4. [ ] Reduced motion activado: la transición es instantánea (ya
   verificado en runtime V1; re-validar con predictive back).

## Fuente al 200 % (dispositivo Android)

1. [ ] `adb shell settings put system font_scale 2.0`: recorrer
   S01/S02/S04/S06/S07/S09/S12 en claro y oscuro.
   **Éxito:** nada cortado ni solapado (los tests Round2FontScaleTest
   cubren componentes en 320/360dp; el dispositivo confirma pantallas
   completas y las capturas de referencia muestran el estado esperado:
   `./gradlew verifyVisualHashes`).

## Rotación (decisión ADR-0006: rotación libre)

1. [ ] S01 con una superficie abierta (p. ej. Contextos): rotar → la
   superficie se conserva (Saver) y el foco es utilizable.
2. [ ] S07 con el formulario lleno: rotar → los datos del draft
   (rememberSaveable del VM/estados) y los errores visibles se
   conservan.
3. [ ] S03 escaneando: rotar → la cámara se re-liga y sigue detectando.

## Xcode/iOS runtime (pendiente hasta que exista host accesible)

1. [ ] Compilar y arrancar (checklist existente en
   `IOS_RUNTIME_CHECKLIST.md`).
2. [ ] Áreas seguras en el lab de iOS si se habilita host; hoja de
   compartir (UIActivityViewController) con un comprobante: se abre,
   comparte y cierra sin crash (código compilado, runtime sin validar).
3. [ ] `isReduceMotionEnabled` respetado: con la opción activa, el
   turnstile/tilt son instantáneos (ReducedMotion.ios.kt ya lo expone).

## Rendimiento (opcional, cuando se cablee el reporte)

1. [ ] Añadir `compose.compiler` reports de estabilidad/skippability y
   comparar antes/después de la migración a
   `collectAsStateWithLifecycle` (triage E2): no se aplicaron
   @Stable/@Immutable sin evidencia — medir primero.
