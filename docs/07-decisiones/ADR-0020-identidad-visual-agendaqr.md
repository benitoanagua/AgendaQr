# ADR-0020 — Identidad visual propia de AgendaQr

**Estado:** SUPLANTADO — 2026-10-17 por ADR-0023; este documento conserva únicamente el registro histórico de la decisión.
> No usar las reglas visuales, iconos ni referencias de este ADR como norma vigente. La autoridad actual es ADR-0023 y la especificación UX/UI V1.13.
**Enmienda:** ADR-0014 y ADR-0017 (referencia visual obligatoria Ant Design Mobile).  
**Relaciona:** contrato normativo `docs/05-design-system/00-contrato-normativo-diseno.md`, especificación UX/UI V1.10, implementación `core/ui`.

## Contexto

Las rondas recientes mejoraron jerarquía, copy, banners, listas y estados, pero el sistema todavía trataba Ant Design Mobile como autoridad obligatoria de paleta, tipografía, radios y composición. Esa fidelidad no garantiza una identidad reconocible para AgendaQr y limita la posibilidad de componer una experiencia propia.

La intención aprobada es que AgendaQr se perciba como una aplicación móvil china contemporánea de consumo: directa, práctica, compacta sin ser apretada, de alta claridad funcional y con un acabado visual cuidado. No se busca copiar una superapp concreta ni utilizar símbolos chinos como decoración.

## Decisión

1. **La identidad visual pertenece a AgendaQr.** Ant Design Mobile, WeChat, Alipay, HyperOS y otros productos pueden servir como referencias de estudio, pero ninguno es la autoridad normativa ni se copia literalmente.
2. **Dirección cromática: azul tecnológico + superficies neutras azul-gris.** Claro: fondo `#F7F8FA`, superficie `#FFFFFF`, secundaria `#F1F4F8`, texto `#1F2937`, acento `#245FE5`. Oscuro: fondo `#10141B`, superficie `#171D26`, secundaria `#0C1118`, texto `#F1F5F9`, acento `#70A5FF` con tinta `#101B2E`. Los roles semánticos de estado se definen por separado.
3. **La accesibilidad es innegociable.** Texto ≥ 4.5:1; foco/indicadores funcionales ≥ 3:1; targets ≥ 48 dp; escalado de texto hasta 200 %; movimiento reducido = transición inmediata. Los pares exactos se fijan por pruebas.
4. **Composición de consumo, no panel administrativo.** Listas compactas y legibles como patrón principal, metadatos alineados, divisores discretos, tarjetas solo cuando agrupen contenido, una intención principal por pantalla y acciones frecuentes visibles sin duplicarlas.
5. **El QR es el elemento distintivo del producto.** El lienzo sigue blanco en ambos temas y conserva el contraste y la zona de silencio necesarios para el escaneo.
6. **Sin migración prematura de dependencias.** Se conserva Compose Multiplatform, el prefijo de código `Am*`, la familia de iconos Lucide y la fuente de sistema. MiSans Global y una familia monoespaciada quedan como candidatas para comparar en local; no se empaquetan fuentes en esta decisión.
7. **No se altera el comportamiento.** Navegación, reglas de negocio, persistencia, sincronización, flujos y copy funcional quedan fuera del alcance. Las convenciones de navegación siguen siendo las de Android/iOS.
8. **Validación visual local obligatoria.** Todo cambio de tokens actualiza `AmSchemeTest`, los contratos de contraste y las capturas Roborazzi/hashes. No se considera validado el rediseño hasta revisar las capturas claro/oscuro en Inicio, detalle QR, búsqueda, importación y formularios.

## Consecuencias

- ADR-0017 queda como decisión histórica; ya no gobierna la identidad visual activa.
- El contrato normativo pasa a versión 3.0 y deja de exigir fidelidad a Ant Design Mobile.
- Los tokens se actualizan a la paleta AgendaQr; no se añade ninguna dependencia de runtime.
- La geometría fina, la selección definitiva de tipografía y cualquier migración de iconos requieren revisión de capturas reales en local antes de ampliarse a todas las pantallas.
- La implementación debe conservar pruebas, contratos y hashes; no se deben actualizar hashes manualmente sin generar capturas reales.
