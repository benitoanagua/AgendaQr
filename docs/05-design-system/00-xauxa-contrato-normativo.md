# Xauxa Design System — contrato normativo

**Versión:** 1.0  
**Estado:** Normativo para nuevas implementaciones y migraciones aprobadas por ADR-0010  
**Ámbito:** Android e iOS; Compose Multiplatform. El laboratorio es un entorno de validación, no una fuente alternativa de diseño.

Este documento es la autoridad normativa para la expresión visual y el comportamiento transversal de Xauxa. No reemplaza la especificación funcional de Agenda QR: la intención, los flujos, las reglas de negocio y la recuperación de errores siguen gobernados por los documentos de producto. Xauxa define cómo se representan y operan visualmente.

## Estado de conformidad

La ronda 8 añade cobertura de contrato para campos deshabilitados, de solo lectura y actualización dinámica de errores. Estas pruebas permanecen no verificadas hasta ejecutarse en el entorno de build.

La conformidad del sistema se registra por capas: (1) contrato/documentación, (2) gate estático, (3) pruebas automatizadas, (4) regresión visual y (5) validación en dispositivos. Un PASS de una capa no implica PASS de las siguientes. El estado de ejecución y los bloqueos vigentes se mantienen en los checklists de `docs/08-validacion/`.

## 1. Objetivo y principios no negociables

Xauxa adopta **Metro como lenguaje visual**, no como reproducción literal de Windows Phone ni como copia de assets. Debe sentirse plano, tipográfico, cromático, directo y dinámico; no debe leerse como Carbon ni como Material 3 tematizado.

1. **Contenido antes que cromo.** Eliminar decoración que no comunique jerarquía, estado, agrupación o interacción.
2. **La tipografía es estructura.** El tamaño, peso, espaciado y posición organizan el contenido; no se sustituye jerarquía por contenedores anidados.
3. **El color tiene semántica.** Acento, estado y superficie son funciones distintas. El color nunca es la única señal de estado.
4. **La geometría es deliberada.** Formas rectangulares, esquinas casi cuadradas y ausencia de elevación decorativa; no usar radios grandes ni tarjetas flotantes como patrón general.
5. **La interacción es visible.** Foco, selección, presión, deshabilitado, carga y error tienen reglas consistentes y comprobables.
6. **Una regla global, una implementación.** Un patrón visual compartido se expresa mediante tokens y componentes Xauxa, no mediante estilos ad hoc por pantalla.
7. **Accesibilidad como contrato.** Contraste, escalado, foco, semántica, targets y movimiento reducido son requisitos de aceptación, no refinamientos posteriores.
8. **Plataforma sin deriva visual.** Material/Compose y controles nativos pueden aportar infraestructura, pero no deciden por defecto la apariencia final.

## 2. Jerarquía normativa y precedencia

En caso de conflicto, aplicar este orden:

1. Requisitos legales, seguridad y accesibilidad aplicable.
2. Reglas funcionales y de dominio de Agenda QR.
3. ADR aprobados.
4. Este contrato normativo Xauxa.
5. Especificación UX/UI de Agenda QR para intención, composición funcional y flujos.
6. Implementación actual y capturas históricas.

La implementación nunca justifica una excepción por sí misma. Una excepción requiere ADR con motivación, alcance, impacto, alternativa descartada, pruebas y criterio de retirada. Si dos documentos normativos discrepan, no se elige silenciosamente uno: se corrige la fuente menos específica y se registra el cambio.

## 3. Arquitectura del sistema

### 3.1 Capas

- **Primitivos privados:** valores físicos de color, dimensión, tipografía y tiempos.
- **Tokens semánticos:** roles como `background`, `surface`, `textPrimary`, `accent`, `danger`, `focusRing`, `radiusBase`.
- **Componentes Xauxa:** API pública, estados, semántica accesible y presentación.
- **Patrones:** composición reutilizable de componentes para tareas comunes.
- **Features:** composición de patrones y componentes; no inventan estilos.
- **Lab y tests:** catálogo ejecutable, pruebas de contrato, accesibilidad y regresión visual.

### 3.2 Fuentes únicas

`XauxaTokens.kt` es la fuente de verdad de tokens. `XauxaRadius.Base` gobierna el radio estándar y `XauxaShape` es la forma canónica que deben consumir los componentes compartidos. `XauxaTheme.kt` es la única capa que adapta dichos tokens a MaterialTheme o a infraestructura de plataforma. Los componentes compartidos viven en `core:ui`; los features no definen colores, radios, elevaciones, tipografía, alturas ni animaciones de forma local. Los roles de compatibilidad Material 3 `secondary` y `tertiary` reutilizan el único acento de marca Xauxa; sus contenedores son neutrales y cambian con el tema. Los roles inversos usan neutros monocromos y un acento legible, sin tonos morados heredados de la paleta predeterminada de Material.

Los nombres exactos de tokens pueden evolucionar, pero cada rol visual debe tener una única definición semántica. No se permite mantener alias divergentes con valores distintos.

### 3.3 Prohibiciones

- No usar `MaterialTheme.colorScheme` como autoridad visual fuera de la adaptación Xauxa.
- No llamar componentes Material visuales directamente desde features cuando exista equivalente Xauxa.
- No introducir `RoundedCornerShape`, `CircleShape`, `RectangleShape`, colores hexadecimales, `dp`, `sp`, duraciones o curvas literales en features para resolver estilos. Las excepciones geométricas (círculos reales, recorte de medios) deben estar encapsuladas y justificadas.
- No usar sombras/elevación decorativa, bordes de reposo universales, superficies tipo tarjeta por defecto ni contenedores anidados sin semántica.
- No añadir sets de iconos, fuentes ni dependencias visuales sin revisión de licencia y ADR.
- No cambiar la misma regla de forma distinta en Android e iOS salvo diferencia de plataforma documentada y probada.

## 4. Fundamentos visuales

### 4.1 Geometría y radio

La geometría base es rectangular. `XauxaRadius.Base` es el token conmutable para el radio de superficies y controles estándar; su valor por defecto es **0 dp**. El rango permitido de exploración es **0–6 dp**. No se decide el radio componente por componente: el token es global y cualquier excepción debe figurar en la tabla de excepciones.

Los radios no se utilizan para simular elevación o suavidad de tarjetas. No aplicar radio a elementos que no son superficies rectangulares (p. ej., avatares circulares o indicadores circulares). El cambio del token exige revisión visual de todas las familias de componentes y capturas de ambos temas.

### 4.2 Superficies, bordes y elevación

- Fondo de pantalla y superficies de contenido se diferencian mediante valores tonales planos, no sombras.
- No hay borde en reposo salvo que sea necesario para percibir el límite de un control, una separación funcional o una agrupación semántica.
- Un borde de control no se elimina si su ausencia incumple contraste no textual de 3:1 o vuelve ambiguo el target.
- Los bordes de foco/error/selección son estados; no deben confundirse con decoración permanente.
- Los separadores se usan solo cuando ayudan a interpretar grupos o filas; no enmarcan automáticamente cada elemento.
- Diálogos y overlays son excepciones funcionales: deben conservar la jerarquía de foco y la distinción respecto al contenido subyacente sin recurrir a sombra decorativa como señal única.

### 4.3 Espaciado y densidad

La unidad base es **4 dp**. Tokens de espaciado y tamaño deben derivarse de esta escala salvo necesidades de plataforma o accesibilidad justificadas. Las reglas de densidad especifican mínimos, no alturas rígidas que recorten contenido. El layout debe poder crecer con texto, localización y escala de fuente.

### 4.4 Tipografía

- Archivo se utiliza para la identidad display/titular donde está disponible y licenciada; el cuerpo usa la familia de sistema para legibilidad e integración de plataforma.
- Los roles tipográficos semánticos definen familia, peso, tamaño, altura de línea y tracking como conjunto.
- Se preserva la capitalización editorial de los textos. No transformar todas las etiquetas a mayúsculas desde un componente.
- No codificar jerarquía solo con peso; combinar tamaño, posición y espacio.
- El escalado de fuente del sistema debe funcionar al 100 %, 150 % y 200 % sin recortes, solapamientos ni pérdida de acciones esenciales. El mínimo canónico de target interactivo es `XauxaMetrics.ControlMinSize = 48.dp`; cualquier excepción debe justificarse y probarse por plataforma.

### 4.5 Iconografía

Lucide es el único set permitido, encapsulado por `XauxaIcon`. Los tamaños y tintes salen de tokens. Iconos decorativos se excluyen del árbol semántico; los iconos que realizan acciones deben tener etiqueta visible cuando la acción no sea inequívoca por contexto y siempre una descripción accesible. No se permite depender solo del glifo para comunicar un estado.

## 5. Color y contraste

### 5.1 Roles

El sistema separa:

- **Acento de marca/sistema:** acciones primarias, foco y énfasis de navegación.
- **Acento de contexto:** identidad visual derivada determinísticamente del ID del contexto; no se persiste en dominio.
- **Colores de estado:** error, advertencia, éxito e información.
- **Superficies:** fondo, superficie, contenedores tonales y superficies inversas.
- **Contenido:** texto primario/secundario, iconografía y contenido sobre acento.

Un color no se reutiliza con un significado diferente en un mismo contexto visual. Los acentos de contexto no sustituyen a los colores de estado.

### 5.2 Umbrales de aceptación

- Texto normal: contraste mínimo **4.5:1**.
- Texto grande: mínimo **3:1**.
- Límites de controles, indicadores de foco y gráficos necesarios para entender la interfaz: mínimo **3:1** respecto a colores adyacentes relevantes.
- Los estados deben incluir una señal adicional al color: texto, icono, forma, posición o semántica accesible.
- Todos los pares se calculan para tema claro y oscuro. No basta con probar el color contra blanco si también aparece sobre contenedores, overlays o estados deshabilitados.

El test automatizado debe cubrir pares reales de foreground/background, incluidos estados, botones, chips, banners, toast, inputs, foco y texto secundario. Un valor de contraste que pasa en un único componente no autoriza su uso universal.

### 5.3 Temas

Tema claro y oscuro son implementaciones del mismo contrato semántico. No se permite invertir colores mecánicamente si el resultado rompe contraste o jerarquía. El modo oscuro puede usar negro puro cuando lo establezca el token normativo, pero no debe introducir elevación o tonalidad arbitraria para distinguir superficies.

## 6. Componentes y estados

Cada componente público debe documentar: propósito, cuándo usarlo, cuándo no usarlo, API/slots, tokens consumidos, estados, semántica, comportamiento de teclado/lector de pantalla, reglas responsive, ejemplos y pruebas requeridas.

Estados mínimos que correspondan a la función del componente: **reposo, presionado, foco, seleccionado, deshabilitado, cargando, éxito y error**. No todos aplican a todos los componentes; los no aplicables deben declararse, no omitirse por accidente.

### 6.1 Acciones

- **Primaria:** bloque sólido de acento; una acción primaria dominante por superficie de intención.
- **Secundaria:** acción de texto, normalmente con icono si aporta información; sin caja decorativa por defecto.
- **Peligro:** usa tokens semánticos de danger y texto explícito; no se identifica solo por rojo.
- **Icon button:** solo cuando el contexto hace inequívoca la acción y la descripción accesible está presente. El target táctil cumple el mínimo de plataforma.
- Las etiquetas preservan capitalización de origen. Los estados de interacción no alteran la geometría de forma que mueva el contenido.

### 6.2 Entradas

- Campo plano con etiqueta fija encima, nunca etiqueta flotante como patrón predeterminado.
- El reposo utiliza superficie tonal con límite perceptible; el foco usa acento y anillo/borde consistente; error usa token danger y mensaje textual; deshabilitado tiene contraste suficiente para comprender estructura y estado.
- La ayuda, contador y error tienen orden semántico estable. Error no se comunica solo mediante borde.
- La búsqueda comparte el contrato de entrada, pero puede añadir affordances específicas de borrar/filtrar como acciones etiquetadas.

### 6.3 Filas, tiles y datos

- `XauxaTile` es una superficie de lanzamiento Metro: bloque plano, sin borde ni sombra en reposo, etiqueta visible y foco evidente.
- `XauxaListRow` no utiliza barra lateral ornamental. El acento solo aparece si representa un dato/contexto real; el estado añade texto/glifo semántico.
- Los datos destacados usan jerarquía tipográfica y alineación, no tarjetas anidadas ni contornos superfluos.
- La altura crece con contenido y font scale; se prohíben alturas fijas que recorten etiquetas o targets.

### 6.4 Feedback

- Banners y resultados inline son bloques tonales sin borde ornamental ni barra lateral. Deben ofrecer texto claro y acciones explícitas cuando existan.
- Toast es temporal y no puede ser el único canal de un error crítico o de una decisión persistente. El contraste se verifica en la superficie real de cada tono.
- Carga usa `XauxaDotProgress` o skeleton según la tarea. Evitar spinners genéricos si el componente de sistema cubre el caso.
- Reduced motion debe tener una representación estática informativa equivalente.
- Empty states y errores deben explicar qué ocurrió y la siguiente acción viable; no solo ilustración o estado técnico.

### 6.5 Diálogos y menús

Los diálogos se reservan para decisiones que necesitan interrupción o foco modal. No deben sustituir banners/feedback inline para estados no bloqueantes. El foco se confina y restaura correctamente; la acción destructiva se identifica de forma explícita. Menús y selectores siguen los mismos tokens, tipografía, foco y contraste que el resto del sistema.

## 7. Interacción, foco y movimiento

- Cada control interactivo ofrece respuesta perceptible sin depender del ripple Material.
- La indicación de pulsación es plana y coherente con la superficie; foco de teclado/lector de pantalla es visible y no se reemplaza por feedback de pulsación.
- El anillo de foco debe tener grosor, separación y contraste tokenizados; se verifica sobre cada color de tile/acento.
- Animaciones con propósito: turnstile de navegación, tilt de tile ≤150 ms y entrada escalonada de tiles de 30–50 ms por elemento, duración total ≤300 ms.
- No se admiten loops decorativos. Un live tile solo transiciona al cambiar su dato.
- Con reduced motion, flip/tilt/escalonado se desactivan y la transición es inmediata; no se pierde información.
- La duración, easing y distancia provienen de tokens; no hay números de animación locales en features.

## 8. Accesibilidad y adaptación

- Targets táctiles: mínimo 44 dp y preferencia por 48 dp donde la plataforma y la densidad lo permitan; si la geometría visual es menor, ampliar hit area sin solapamiento.
- Todo elemento tiene orden de lectura y foco lógico. No duplicar contenido decorativo en TalkBack/VoiceOver.
- Todos los campos tienen etiqueta persistente; los errores se muestran de forma visible y se exponen también en la semántica del nodo editable (`SemanticsProperties.Error`) cuando el componente dispone de esa API. Requisitos y estados deben quedar identificables sin depender solo del color.
- Soportar localización, cadenas largas, texto dinámico, orientación y tamaños de pantalla; no truncar instrucciones, errores o acciones esenciales.
- Pruebas de layout: anchos compactos (320 y 360 dp) y font scale 100/150/200 %. Verificar ausencia de recorte/solapamiento y acceso a acciones.
- Validación manual en dispositivos: TalkBack, VoiceOver, navegación por teclado, contraste en exteriores/alto contraste cuando esté disponible y reduced motion.
- No usar color como único canal. No ocultar etiqueta de acción en modo accesible.

## 9. Plataforma y comportamiento adaptable

Xauxa establece el contrato visual compartido; las diferencias de plataforma se limitan a convenciones necesarias de navegación, permisos, foco y sistema operativo. Una adaptación de plataforma debe mantener roles semánticos, estados, contraste, densidad y jerarquía. No se permite bifurcar la identidad completa por plataforma.

Los insets del sistema se gestionan explícitamente. La UI no se dibuja bajo barras de estado/navegación sin reservar o consumir los insets correctamente. La navegación atrás y restauración de estado son contratos funcionales, no detalles cosméticos.

## 10. Pruebas y puertas de calidad

Un cambio de Xauxa no se considera terminado hasta cumplir las puertas aplicables:

1. **Compilación:** módulos afectados y targets soportados.
2. **Tests de tokens/contraste:** todos los pares reales y ambos temas.
3. **Tests de componente:** estados, semántica, acciones y reglas de API.
4. **Integridad del laboratorio:** catálogo completo y sin duplicados.
5. **Layout/accessibility:** font scale, tamaños compactos, targets, foco y semántica.
6. **Regresión visual:** capturas/hash manifest de referencias actualizadas deliberadamente; revisar claro/oscuro y estados principales.
7. **Gate de repositorio:** `verifyDesignSystemCompliance` y CI correspondiente.
8. **Revisión en dispositivo:** cuando cambian insets, cámara, foco nativo, navegación, gestos, tipografía o motion.

Si una puerta no puede ejecutarse por falta de red, SDK o dispositivo, el estado se registra como **NO VERIFICADO**, nunca como PASS. Un gate estático no sustituye compilación, tests ni revisión visual.

## 11. Gobierno del sistema y proceso de cambio

Todo cambio transversal sigue este orden:

1. Identificar regla existente y contradicciones en spec, tokens, componentes, laboratorio y consumidores.
2. Definir el principio y la regla normativa antes de editar componentes.
3. Actualizar este contrato o el documento de fundamento correspondiente y registrar ADR cuando cambie el comportamiento visual aprobado.
4. Cambiar tokens y componentes base antes que las pantallas.
5. Migrar todos los consumidores afectados; no dejar variantes temporales ni aplicar el cambio solo al caso que motivó la tarea.
6. Añadir tests/gates que detecten la regresión específica.
7. Actualizar catálogo, documentación de componentes, capturas y manifiestos.
8. Ejecutar las puertas de calidad y reportar resultados reales, incluidos los no ejecutados.
9. Mantener una tabla de excepciones con propietario, razón, alcance y condición de retirada. Si no existe entrada, no existe excepción válida.

### Criterio de finalización

El sistema está alineado cuando el mismo rol visual tiene la misma apariencia y estados en todas las superficies, los features no contienen estilos paralelos, los tests protegen los contratos y la documentación refleja el comportamiento implementado. La semejanza con Metro se evalúa por composición, tipografía, color, geometría y movimiento en conjunto, no por un único radio o la ausencia aislada de bordes.

## 12. Registro de excepciones

Estado inicial: **sin excepciones visuales aprobadas**. Las diferencias obligatorias de plataforma deben registrarse en ADR o en la documentación del componente; no se consideran permiso para alterar tokens compartidos.


El gate estático pasa; la suite se ejecuta en CI.


## Cobertura de auditoría

### Ronda 10 — cobertura estática y responsive

El gate `verifyDesignSystemCompliance` amplía el recorrido de primitivas visuales a los source sets de producción de `core:ui` y la prohibición de Material 3 directo a `feature`, `shared/src` y `androidApp/src`. El nuevo test responsive de 360 dp complementa la cobertura de 320 dp. El gate estático no acredita compilación ni ejecución de pruebas: la suite de CI las ejecuta en cada push.
