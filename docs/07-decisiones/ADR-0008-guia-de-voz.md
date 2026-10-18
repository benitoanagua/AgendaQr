# ADR-0008 — Guía de voz (tuteo) y copy de componente

**Estado:** APROBADA (ronda 2 — 2026-10-09)
**Área:** Área H de la auditoría · UX copy

## Contexto

El copy vivía en dos capas: AppStrings (feature) y defaults hardcodeados
en `core/ui` (que además quedaban fuera del gate D2). Convivían dos
registros: tuteo ("Inicia sesión", "Ingresa un correo válido") y usted
("Toque para seleccionar un archivo", "Encuadre el código QR").

## Decisión

1. **Tuteo en toda la app** (guía: `docs/04-ux/05-guia-de-voz.md`).
   Los textos afectados NO están en la spec congelada (§4/§10 no los fija;
   son copy de componente/hints): "Toque para…" → "Toca para…",
   "Encuadre el código QR" → "Encuadra el código QR" (valor de
   `AppStrings.EncuadreElCodigoQr`; el identificador no cambia).
2. **core/ui no hardcodea copy**: los defaults de texto desaparecen de
   los componentes (ScannerViewport, FileUpload, SearchBar, CommandBar);
   todo copy llega como parámetro y el feature lo provee desde AppStrings.
   El laboratorio pasa su copy de demo explícito.
3. El **gate** gana una regla con fixtures que detecta literales de copy
   en `core/ui/components` (exige parámetro o AppStrings del llamador).

## Consecuencias

- Quien consume un componente ve en compilación qué copy exige
  (`require` en runtime para los condicionales, p. ej. onClear →
  clearLabel).
- La guía de voz media página es la referencia para TODO copy nuevo.
