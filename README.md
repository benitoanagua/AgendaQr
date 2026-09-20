# Agenda QR

Agenda QR es una agenda de destinos QR:

**guardar → organizar → buscar → mostrar → compartir/reutilizar**

## 1. Regla de precedencia

La implementación sigue una jerarquía explícita de fuentes de verdad:

1. **Agenda QR** — producto, dominio, requisitos, alcance, decisiones y reglas de negocio.
2. **Xauxa Design System + XauxaXcan** — UI/UX, tokens, componentes, estados y patterns.
3. **WaraWerse** — referencia técnica para Kotlin Multiplatform, Compose, modularización, build y prácticas de testing.

Si dos fuentes entran en conflicto, prevalece la fuente de mayor autoridad indicada arriba.

WaraWerse nunca introduce lógica de negocio en Agenda QR. XauxaXcan no puede sobreescribir una regla normativa explícita del Xauxa Design System.

## 2. Plataformas

```text
androidApp/   → host Android y validación inicial
shared/       → lógica compartida KMP
iosApp/       → host iOS/Xcode y boundary nativo
```

Android es la plataforma que se valida primero. iOS forma parte de la arquitectura desde el inicio, pero no se considera validado hasta ejecutarlo mediante Xcode/simulador/dispositivo.

## 3. Estructura

```text
AgendaQr-main/
├── androidApp/                 # host Android
├── iosApp/                     # host iOS
├── shared/                     # KMP compartido
├── core/                       # capacidades transversales
├── feature/
│   └── destinations/           # dominio, data y presentación
├── build-logic/                # convention plugins
├── gradle/                     # version catalog / wrapper
├── docs/                       # documentación normativa y de implementación
└── design-tokens.json          # snapshot exportable de tokens
```

## 4. Kotlin Way

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

## 5. Verificación

```bash
./gradlew verifyAgendaQrArchitecture
```

La existencia de código no equivale a validación de plataforma. Android e iOS se verifican por separado.

## 6. Documentación

La documentación está organizada por autoridad y ciclo de desarrollo. Comienza en [`docs/README.md`](docs/README.md).
