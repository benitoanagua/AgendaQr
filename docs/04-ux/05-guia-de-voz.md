# Guía de voz de Agenda QR

Media página. Decisiones de la ronda 2 (ADR-0008); aplica a AppStrings y a
todo copy que el llamador provea a `core/ui`.

## Decisión: tuteo

La app le habla al usuario de **tú**, siempre. Antes convivían el tuteo
("Inicia sesión") y el usted ("Toque para seleccionar un archivo",
"Encuadre el código QR"). Se unifica en tuteo; estos textos no estaban en
la spec congelada (son copy de componente), y el cambio queda registrado
en ADR-0008.

## Reglas

1. **Voz activa y una acción por mensaje.** "No pudimos leer este QR.
   Elige otra imagen." — nunca descripciones pasivas solas.
2. **Todo error responde tres cosas:** qué ocurrió, qué pasó con los
   datos ("Tu información está guardada.") y qué hacer (acción visible
   con verbo en infinitivo/tuteo: REINTENTAR, "Elige otra imagen").
3. **Frases cortas.** Objetivo ≤ 60 caracteres por mensaje; las
   etiquetas de acción de 1–2 palabras ("Guardar", "Cancelar"). Si un
   mensaje necesita más, es una nota, no un banner.
4. **Números y estados con texto, nunca solo símbolos ni solo color**
   (spec §5/§11): "Sincronización pendiente: 1", no un puntito rojo.
5. **Sin jerga técnica.** "No pudimos sincronizar", no "HTTP 502".
   "Código QR detectado", no "payload decodificado".
6. **Sentence case** (ya vigente): solo la primera letra en mayúscula;
   las etiquetas de acción conservan su capitalización editorial.
7. **Español neutro**: sin regionalismos; "tú" implícito ("Elige",
   "Encuadra", "Toca").
