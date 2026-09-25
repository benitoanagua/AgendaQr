# Agenda QR

Agenda QR es una agenda de destinos QR y respaldo de operaciones:

**guardar → organizar → buscar → mostrar → reutilizar/compartir**

## 1. Regla de precedencia

La implementación sigue una jerarquía explícita de fuentes de verdad:

1. **Agenda QR** — producto, dominio, requisitos, alcance, decisiones y reglas de negocio.
2. **UX/UI V1 de Agenda QR** — contrato de interacción congelado para la experiencia del producto.
3. **Xauxa Design System + Xauxa** — lenguaje visual, tokens, componentes y patterns.
4. **WaraWerse** — referencia técnica para Kotlin Multiplatform, Compose, modularización, build y prácticas de testing.

Si dos fuentes entran en conflicto, prevalece la fuente de mayor autoridad indicada arriba.

WaraWerse nunca introduce lógica de negocio en Agenda QR. Xauxa define por completo la UI/UX visual del producto.

## 2. UX/UI V1

La especificación UX/UI está congelada en `docs/04-ux/02-especificacion-ux-ui-v1.md`.

La suite de estados, eventos e invariantes está en `docs/08-validacion/02-suite-estados-eventos-v1.md`.

Esta congelación no es implementación: Kotlin/Compose debe adaptarse al contrato, no redefinirlo.

## 3. Plataformas

    androidApp/   → host Android y validación inicial
    shared/       → lógica compartida KMP
    iosApp/       → host iOS/Xcode y boundary nativo

Android es la plataforma que se valida primero. iOS forma parte de la arquitectura desde el inicio, pero no se considera validado hasta ejecutarlo mediante Xcode/simulador/dispositivo.

## 4. Estructura

    AgendaQr/
    ├── androidApp/                 # host Android
    ├── iosApp/                     # host iOS
    ├── shared/                     # KMP compartido
    ├── core/                       # capacidades transversales
    ├── feature/
    │   └── destinations/           # dominio, data y presentación
    ├── build-logic/                # convention plugins
    ├── gradle/                     # version catalog / wrapper
    ├── docs/                       # documentación normativa y de implementación
    └── core/ui/.../theme/XauxaTokens.kt  # fuente canónica de tokens (Kotlin)

## 5. Kotlin Way

- modelos inmutables;
- interfaces pequeñas;
- constructor injection;
- use cases con `operator fun invoke()`;
- `Flow`/`StateFlow` para estado observable;
- `sealed interface` para acciones y estados cerrados;
- dependencias explícitas entre módulos;
- dominio independiente de Android, iOS y Compose;
- `expect`/`actual` solamente en boundaries de plataforma;
- persistencia encapsulada en `data`;
- convention plugins y version catalog;
- una responsabilidad clara por módulo y componente.

## 6. Verificación

    ./gradlew verifyAgendaQrArchitecture

La existencia de código no equivale a validación de plataforma. Android e iOS se verifican por separado.

## 7. Documentación

La documentación está organizada por autoridad y ciclo de desarrollo. Comienza en `docs/README.md`.
