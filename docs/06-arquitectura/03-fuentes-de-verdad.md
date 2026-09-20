# Fuentes de verdad y precedencia

## 1. Agenda QR — autoridad de producto

Es la única fuente de verdad para:

- producto;
- requisitos;
- dominio;
- alcance y límites;
- decisiones aprobadas;
- flujos funcionales;
- reglas de negocio;
- comportamiento local/offline especificado.

No se importa comportamiento de negocio desde WaraWerse.

## 2. Xauxa — autoridad visual

`Xauxa Design System` y `XauxaXcan` definen la UI/UX.

El Design System es normativo. Cuando una implementación histórica de XauxaXcan difiere de una regla explícita del Design System, prevalece la especificación.

## 3. WaraWerse — referencia técnica

Se utiliza como referencia para:

- Kotlin Multiplatform;
- Compose Multiplatform;
- límites de módulos;
- convention plugins;
- Gradle/build logic;
- prácticas de testing.

Sus conceptos de producto o dominio no se copian a Agenda QR.

## 4. Kotlin Way

La implementación favorece:

- modelos inmutables;
- interfaces pequeñas;
- constructor injection;
- use cases con `operator fun invoke()`;
- `Flow`/`StateFlow`;
- `sealed interface`;
- dependencias explícitas;
- boundaries `expect`/`actual` solo donde son necesarios;
- dominio independiente de plataforma/UI;
- ausencia de estado global de negocio.
