# ADR-0031 — Política de adopción y retiro de componentes Moon

## Estado

Aprobado — vigente para la integración de componentes en Agenda QR.

## Contexto

`core/ui` contiene componentes Moon portados y composiciones de producto. Para evitar una biblioteca visual paralela, componentes sin consumo real no deben acumularse por conveniencia, y cada componente exportado debe tener un estado explícito en el catálogo y en la galería.

## Decisión

### 1. Componentes sin uso

Un componente de `core/ui` sin uso en pantallas de producción se adopta o se retira.

- **ADOPTAR** si mejora un flujo real o cumple una función base transversal necesaria (por ejemplo, control de formulario, accesibilidad o composición interna).
- **RETIRAR** si no aporta valor verificable y no tiene dependientes necesarios.
- No mantener componentes únicamente como «disponibles por si acaso».
- Los componentes base sin uso directo en pantallas deben identificarse como primitivos de soporte, contar con pruebas y permanecer representados en la galería.

### 2. Criterios para componentes nuevos

Todo componente nuevo debe:

1. Tener un equivalente Moon en el catálogo o una justificación explícita de composición de producto cuando Moon no ofrece el patrón.
2. Implementarse en `core/ui` y consumir tokens semánticos.
3. Respetar anatomía, variantes, tamaños, estados, movimiento y accesibilidad de Moon.
4. Añadirse al catálogo `docs/05-design-system/13-catalogo-moon.md` y a `AqDesignSystemGallery`.
5. Incluir pruebas de contrato, geometría, estados y accesibilidad aplicables.
6. No introducir valores visuales locales, wrappers `AqMoon*`, componentes Material ni un sistema de tokens paralelo.

### 3. Patrones móviles

Cuando Moon proporciona un componente o patrón, se porta fielmente. Cuando no lo proporciona, se compone con piezas Moon y la mecánica mínima de Compose o de plataforma. La mecánica nativa no puede introducir una apariencia alternativa.

### 4. Límites

No añadir datos persistidos, red, permisos, telemetría ni cambios de seguridad como parte de un componente visual. El componente solo invoca contratos e intents existentes.

## Consecuencias

El catálogo Moon y `AqDesignSystemGallery` son el inventario normativo de componentes. Las pruebas y el gate de completitud impiden símbolos exportados sin documentación/estado. No se mantienen fichas de componente independientes.
