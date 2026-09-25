# AgendaQr — Lenguaje visual unificado

## Propósito

AgendaQr adopta un único sistema visual. Metro y Xauxa son referencias, no dos sistemas que conviven en la interfaz. La experiencia debe sentirse como un producto coherente y responder primero a las tareas reales de AgendaQr: administrar códigos QR, personas, obligaciones, vencimientos y comprobantes.

## Jerarquía de autoridad

1. **Producto y dominio AgendaQr:** define información, flujos, reglas de negocio y prioridades.
2. **Este contrato visual:** define los principios y reglas que gobiernan toda implementación de UI.
3. **Implementación existente de Xauxa en `core:ui`:** se reutiliza cuando cumple este contrato; se adapta o reemplaza cuando no.
4. **Metro:** referencia de composición y lenguaje visual sobrio; no se copian componentes de forma automática.
5. **Material/Compose:** infraestructura de renderizado y accesibilidad, no autoridad visual independiente.

No se crean tokens paralelos, temas por pantalla ni componentes duplicados “Metro” y “Xauxa”.

## Reglas no negociables

### 1. Una sola fuente de verdad

- Los valores visuales viven en la capa de tokens de `core:ui`.
- Los componentes compartidos consumen tokens semánticos; las pantallas de producto no definen colores, tamaños, radios o duraciones arbitrarias.
- Los componentes existentes se clasifican antes de cambiarlos: **conservar**, **adaptar**, **reemplazar** o **retirar**. Toda excepción debe registrar su necesidad de producto o accesibilidad.
- El laboratorio de componentes refleja los contratos y tokens realmente usados por producción; no mantiene una versión demostrativa divergente.

### 2. Identidad y color

- La interfaz base es monocromática, con fondo blanco o negro según el tema y texto de alto contraste.
- Existe un único color de acento de marca, aplicado por significado: acción primaria, selección activa, foco e información interactiva.
- Estados de éxito, advertencia y error pueden usar colores semánticos diferenciados solo cuando comunican un estado real; no son acentos de marca ni decoración.
- No se introducen gradientes, tintes de marca en superficies de fondo ni colores de texto por gusto.
- Los colores se validan con contraste accesible en ambos temas. No se sacrifica legibilidad para conservar un valor de referencia.

### 3. Geometría y superficies

- La composición usa una cuadrícula adaptable de cuatro columnas como referencia conceptual; el número de columnas se reduce según el ancho disponible.
- Márgenes de página y separaciones se basan en una escala común de múltiplos de 4 dp, con 8 dp como unidad habitual.
- Esquinas discretas: radio máximo de 4 dp salvo excepción justificada por plataforma o accesibilidad.
- Se prefieren estructura, alineación, tipografía y espacio en blanco para crear jerarquía.
- Sin sombras ni elevación decorativa; sin contenedores anidados que no aporten agrupación funcional.
- Listas muestran información útil para decidir y actuar (normalmente 3–4 datos clave), sin convertir cada fila en una tarjeta pesada.

### 4. Tipografía y contenido

- La jerarquía se expresa primero con tamaño, peso, espaciado y posición, no con una paleta de colores de texto.
- Se define una escala tipográfica breve y semántica; no se agregan tamaños locales en cada pantalla.
- Los textos de acción son directos y específicos. Se evita el lenguaje genérico cuando puede nombrarse la acción o el objeto.
- La densidad informativa se adapta al contexto: resumen escaneable en listas, detalle suficiente en la ficha y formularios con etiquetas persistentes y errores próximos al campo.

### 5. Controles y navegación

- Los botones son principalmente textuales; los iconos solo se usan cuando mejoran reconocimiento o accesibilidad y tienen etiqueta/semántica adecuada.
- Los campos usan una presentación sobria con indicador de foco claro, etiqueta persistente y estados de error/ayuda explícitos.
- Los pivots y pestañas mantienen una señal activa consistente; cuando se use la navegación inferior de estilo Metro, las etiquetas serán textuales en mayúsculas y el estado activo se marcará con acento y subrayado de 2 dp. No se reemplaza una navegación existente sin verificar los flujos y convenciones de plataforma.
- Las áreas táctiles respetan los mínimos accesibles de la plataforma, aunque el elemento visual sea compacto.
- Cada control debe tener estados definidos: reposo, foco, pulsado, deshabilitado, carga, error y selección cuando correspondan.

### 6. Movimiento y plataforma

- Toda animación debe explicar un cambio de estado, continuidad espacial o respuesta a una acción; no hay movimiento ornamental ni loops sin propósito.
- Duraciones de referencia: 150 ms para respuesta breve, 300 ms para transición estándar y 450 ms para transición amplia. Curva de referencia: cubic-bezier(0.1, 0.9, 0.2, 1).
- Se respeta la preferencia de movimiento reducido.
- Se respetan safe areas, insets, navegación atrás y patrones nativos. La adaptación Android/iOS no altera el lenguaje visual compartido ni fuerza controles incompatibles con la plataforma.

## Método de integración

1. **Auditar:** inventariar tokens, componentes, pantallas y usos; detectar duplicaciones y valores literales.
2. **Decidir:** documentar conservar/adaptar/reemplazar/retirar con referencia a estas reglas.
3. **Consolidar fundamentos:** ajustar una sola capa de tokens y el tema, preservando compatibilidad pública cuando sea viable.
4. **Validar componentes:** actualizar el laboratorio y probar estados, contraste, accesibilidad y comportamiento.
5. **Migrar pantallas por flujo:** aplicar patrones compartidos a Inicio/resumen, lista y detalle de QR, obligación/vencimiento, comprobante, contactos y formularios pertinentes, sin cambiar lógica de negocio.
6. **Verificar:** ejecutar pruebas de UI y arquitectura, revisión de valores visuales fuera de tokens y validación manual en tamaños compactos y amplios.

## Criterio de aceptación

Una pantalla cumple cuando utiliza la fuente única de tokens y componentes, conserva el comportamiento y los datos del dominio AgendaQr, respeta contraste y áreas táctiles, presenta estados completos, no introduce estilos paralelos y se mantiene coherente con el resto de la aplicación. No se considera terminada una migración solo porque compile: requiere inspección visual en los estados principales y de error.

## Registro de decisiones

- La elección del color de acento queda pendiente de auditoría de identidad y comprobación de contraste; no se fija por preferencia arbitraria.
- El contrato prioriza coherencia y accesibilidad sobre la copia literal de cualquiera de las referencias.
