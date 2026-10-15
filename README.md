# Agenda QR

Agenda QR es una agenda de destinos QR y respaldo de operaciones:

**guardar → organizar → buscar → mostrar → reutilizar/compartir**

Funciona **local-first**: los datos viven en el dispositivo bajo un perfil local
que se crea sin cuenta y sin red. Conectar una cuenta es opcional y solo habilita
respaldo y sincronización ([ADR-0016](docs/07-decisiones/ADR-0016-identidad-local-sin-cuenta.md)).

## 1. Regla de precedencia

La implementación sigue una jerarquía explícita de fuentes de verdad:

1. **Agenda QR** — producto, dominio, requisitos, alcance, decisiones y reglas de negocio.
2. **UX/UI V1 de Agenda QR** — contrato de interacción congelado (`docs/04-ux/02-especificacion-ux-ui-v1.md`).
3. **Referencia Ant Design Mobile** — lenguaje visual, tokens, componentes y patrones; la navegación sigue las convenciones de Android e iOS ([ADR-0014](docs/07-decisiones/ADR-0014-referencia-ant-design-mobile.md)).
4. **WaraWerse** — referencia técnica para Kotlin Multiplatform, Compose, modularización, build y prácticas de testing.

Si dos fuentes entran en conflicto, prevalece la de mayor autoridad. WaraWerse
nunca introduce lógica de negocio.

## 2. Alcance y límites

Agenda QR conserva destinos QR y evidencia de PAGO/COBRO. **No ejecuta pagos, no
mueve dinero y no verifica el dinero**: un comprobante recibido no significa
dinero verificado. Detalle en [`docs/PRODUCT.md`](docs/PRODUCT.md).

## 3. Plataformas

    androidApp/   → host Android y validación inicial
    shared/       → lógica compartida KMP
    iosApp/       → host iOS/Xcode y boundary nativo

Android se valida primero (JVM/Robolectric, gates, lint, ensamblado). iOS forma
parte de la arquitectura desde el inicio y se mantiene compilable, pero no se
considera validado hasta ejecutarlo con Xcode/simulador/dispositivo.

## 4. Estructura

    AgendaQr/
    ├── androidApp/                 # host Android
    ├── iosApp/                     # host iOS
    ├── shared/                     # KMP compartido
    ├── core/                       # capacidades transversales (incl. core/ui, tema)
    ├── feature/
    │   └── destinations/           # dominio, data y presentación
    ├── build-logic/                # convention plugins y gates
    ├── gradle/                     # version catalog / wrapper
    └── docs/                       # documentación normativa y de implementación

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

## 6. Build, verificación y pruebas

Los comandos requieren `-PallowDebugSigningForRc=true` (el guard de firma de
release se evalúa en configuración):

```bash
./gradlew verifyAgendaQrArchitecture -PallowDebugSigningForRc=true
./gradlew :androidApp:assembleDebug -PallowDebugSigningForRc=true
./gradlew :androidApp:testDebugUnitTest -PallowDebugSigningForRc=true
```

La lista completa de puertas, pruebas y validación por plataforma está en
[`docs/QUALITY.md`](docs/QUALITY.md). La existencia de código no equivale a
validación de plataforma: Android e iOS se verifican por separado.

## 7. Documentación

La documentación comienza en [`docs/README.md`](docs/README.md). Estado actual de
implementación y límites: [`docs/STATE.md`](docs/STATE.md). El contrato visual
vigente está en
[`docs/05-design-system/00-contrato-normativo-diseno.md`](docs/05-design-system/00-contrato-normativo-diseno.md).
