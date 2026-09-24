# Laboratorio de componentes UI (Compose)

## Propósito

Crear un espacio aislado para desarrollar, inspeccionar y validar componentes visuales antes de integrarlos en los flujos de Agenda QR. El laboratorio sirve como catálogo ejecutable de estados y variantes; no es una pantalla de producto ni introduce reglas de dominio.

## Autoridad y límites

La implementación debe respetar esta precedencia:

1. Agenda QR: producto, dominio, requisitos y reglas de negocio.
2. UX/UI V1 de Agenda QR: contrato de interacción y estados.
3. Xauxa Design System + XauxaXcan: tokens, lenguaje visual, componentes y patrones.
4. WaraWerse: referencia técnica para Kotlin Multiplatform, Compose, modularización y testing.

El laboratorio se construye dentro de Agenda QR. WaraWerse aporta patrones de arquitectura, no lógica ni modelos de negocio. No se deben duplicar tokens ni redefinir los componentes normativos de Xauxa.

## Alcance de la primera iteración

- Definir el laboratorio como herramienta de desarrollo, fuera de la navegación de producción.
- Preparar una galería de componentes reutilizables de UI con ejemplos de estados y variantes.
- Poder inspeccionar los componentes en tema claro y oscuro, y con tamaños de pantalla distintos.
- Mantener las muestras deterministas y alimentadas por datos ficticios locales.
- Evitar acceso a Supabase, persistencia, contactos, cámara, notificaciones u otros servicios de plataforma.
- No incluir todavía pantallas funcionales de Agenda QR ni alterar el contrato UX/UI V1.

## Organización propuesta

El laboratorio debe vivir en la capa de presentación/UI, no en dominio ni data. Reutiliza los componentes y tokens reales del proyecto; no mantiene copias paralelas de ellos.

Cada muestra debe tener:
- nombre y propósito del componente;
- variantes disponibles;
- estados relevantes (normal, presionado/seleccionado, deshabilitado, carga, vacío o error cuando aplique);
- comportamiento esperado y eventos observables;
- notas de accesibilidad (etiqueta, foco, contraste y tamaño de objetivo);
- referencia al token o patrón Xauxa aplicable.

## Reglas de arquitectura

- Kotlin idiomático: funciones pequeñas, estado inmutable y dependencias explícitas.
- Compose Multiplatform según las convenciones ya presentes en el repositorio.
- Las muestras no deben importar repositorios ni casos de uso de negocio.
- Los componentes reciben sus valores y callbacks desde parámetros; no acceden a singletons o servicios globales.
- Los estados interactivos se modelan explícitamente y se prueban sin depender de la UI de producción.
- Las dependencias de Android/iOS se mantienen en los boundaries de plataforma; el laboratorio no debe impedir compilar los targets compartidos.

## Validación

Para cada componente se debe comprobar:
1. Renderizado con tokens Xauxa, sin valores visuales duplicados innecesariamente.
2. Legibilidad y disposición en tema claro y oscuro.
3. Comportamiento de cada variante y estado.
4. Accesibilidad básica: semántica, descripción de controles y navegación por foco.
5. Que los callbacks emitan la acción esperada y no ejecuten lógica de negocio.
6. Que el módulo de producción no dependa del laboratorio.

La validación automatizada debe cubrir lógica/estado y compilación del target Android disponible. La inspección visual manual complementa las pruebas; no debe declararse validación iOS hasta ejecutar el proyecto en Xcode/simulador/dispositivo.

## Criterios de aceptación para habilitar la implementación visual

- El laboratorio está separado de la navegación y del comportamiento de producción.
- La galería usa los componentes/tokens reales, no una implementación paralela.
- Hay una ruta de desarrollo documentada para abrir y revisar las muestras.
- Se pueden revisar tema claro/oscuro y estados principales.
- Las pruebas de la capa UI pasan y el host Android compila.
- La integración de pantallas de producto sigue subordinada a la especificación UX/UI V1.

## Secuencia de trabajo

1. Auditar los módulos y convenciones Compose existentes antes de agregar código.
2. Determinar el punto de entrada de desarrollo más pequeño que no contamine la app de producción.
3. Implementar la galería y las primeras muestras reutilizando tokens y componentes.
4. Agregar pruebas de estados/eventos y documentar cómo ejecutar el laboratorio.
5. Validar Android; registrar explícitamente cualquier límite de verificación iOS.
