# ADR-0026 — Cierre de la migración de `core/ui` a Polaris

**Estado:** ACEPTADO — 2026-11-17
**Relaciona:** ADR-0025 (adopción de Polaris), contrato normativo de diseño
v8.0, informe de auditoría de fidelidad.

## 1. Decisión

Se declara cerrada la migración de `core/ui` a la reimplementación de Polaris
decidida en ADR-0025. La autoridad visual es la instantánea versionada
`@shopify/polaris-tokens@9.4.2`; los iconos proceden de
`@shopify/polaris-icons@9.3.1`.

El cierre comprende la sustitución de los tokens de color, tipografía, sombras
e iconos; la corrección del tema por defecto; la eliminación de los
identificadores heredados de la familia `Am*`; y la retirada de las menciones
activas a sistemas visuales anteriores. El contrato normativo alcanza la
versión 8.0 y el informe de auditoría registra el estado final.

## 2. Alcance

No se modifica el contrato funcional de la especificación UX/UI V1: ni
flujos, ni reglas de negocio, navegación, local-first, copy ni voice guide.
No se tocan el dominio ni la sincronización. Los ADR históricos se conservan
íntegros como registro de las decisiones que ADR-0025 sustituyó.

## 3. Estado de las desviaciones

Permanecen registradas y vigentes en el contrato: X-01…X-07, PV-01…PV-08,
D-10 y la anulación de `shadow-100` en `light-mobile`. Las siete extensiones
de iconos sin equivalente oficial conservan el verificable más cercano de la
misma versión del paquete.

Los pares de acento y las primitivas añadidas tienen origen trazable en la
instantánea o, en su defecto, una desviación documentada con su justificación.
`AqTheme` sigue el tema del sistema; el valor por defecto del local de esquema
es la base clara, de modo que una composición sin proveedor no hereda oscuro.

## 4. Verificación

- `verifyAgendaQrArchitecture`, `verifyDocs` y las pruebas de `core/ui` pasan.
- `:androidApp:testDebugUnitTest` no tiene fuentes; la cobertura de la capa
  visual se ejecuta en `:core:ui:testDebugUnitTest`.
- El ensamblado de `androidApp` se ejecuta una vez cerradas todas las
  entregas, no como parte de cada una.
- La plataforma iOS y la distribución de los avisos de licencia en iOS no se
  pueden comprobar en este entorno, sin macOS/Xcode, y quedan explícitamente
  **no verificadas**. Tampoco se ha ejecutado el checklist manual en
  dispositivo.

## 5. Consecuencias

El sistema visual tiene una sola fuente de verdad y un único vocabulario de
nombres. Las futuras incorporaciones visuales deben incorporarse al contrato
normativo y a la instantánea antes de consumirse en componentes; cualquier
valor no verificable sigue la regla de ADR-0025: usar el verificable más
cercano, registrar la desviación y continuar.
