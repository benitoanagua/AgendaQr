# Component Lab web (`:core:ui-lab`)

Banco de trabajo navegable de los componentes `Aq*` del design system,
estilo storybook/playbook. Es una aplicación **wasmJs browser** que
depende solo de `:core:ui` (ADR-0049).

## Ejecutar

```bash
# Servidor de desarrollo con recarga (abre el navegador):
./gradlew :core:ui-lab:wasmJsBrowserDevelopmentRun -PallowDebugSigningForRc=true

# Build de producción y servicio estático:
./gradlew :core:ui-lab:wasmJsBrowserDistribution -PallowDebugSigningForRc=true
python3 -m http.server -d core/ui-lab/build/dist/wasmJs/productionExecutable 8000
# → http://localhost:8000
```

Requisito de toolchain: Node.js y Yarn del sistema. La convención
`agendaqr.wasm-app` (build-logic) aplica overrides **portables**: las
rutas se fijan solo si la propiedad de Gradle o la variable de entorno
está definida **y el archivo existe**; si no, queda el comportamiento por
defecto del plugin (descarga oficial de Node/Yarn/Binaryen).

Entorno normal (con acceso a las descargas oficiales): no hay que
configurar nada — el plugin usa sus defaults.

Entorno con firewall o toolchain propio (p. ej. Node de sistema + Yarn +
Binaryen npm), en `~/.gradle/gradle.properties` (**nunca en el repo**):

```properties
agendaqr.wasm.nodeDownload=false
agendaqr.wasm.yarn=/home/usuario/.local/bin/yarn
agendaqr.wasm.binaryen=/home/usuario/.local/share/kotlin-binaryen/wasm-opt
```

Equivalentes por variable de entorno: `AGENDQR_WASM_NODE_DOWNLOAD`
(`false`), `AGENDQR_WASM_YARN`, `AGENDQR_WASM_BINARYEN` (prefijo literal
del código). Si la ruta indicada no existe, el override se ignora y se usa
el default del plugin. La reflexión sobre el DSL de Binaryen está aislada
en `agendaqr.wasm-app.gradle.kts` con comentario (Kotlin 2.2 no expone API
pública equivalente).

## Qué ofrece

- **Lista de componentes** con búsqueda por nombre/categoría y filtro por
  las 10 familias del catálogo (Foundations…Mobile Interactions).
- **Selector de estado/variante** por componente (p. ej. AqButton:
  variantes, tamaños, estados, solo-icono, ancho completo).
- **Controles de entorno**: tema claro/oscuro, escala de fuente
  100/150/200 %, ancho de viewport Auto/360/412/768 dp, y acceso a la
  galería completa (`AqDesignSystemGallery`).
- **Log de eventos**: interacciones dentro de los previews (clicks,
  toggles) y navegación del propio lab.
- **Enlace profundo por hash**: `#AqButton` o `#AqButton/variantes`
  (id de estado). Reacciona a `hashchange`, así que se puede pegar la URL
  en otra pestaña o recargar sin perder el contexto.

## Fuente única de configuración (ADR-0053)

El Component Lab, la galería y producción **no son tres implementaciones**.
Los tres consumen los componentes públicos `Aq*` de `core/ui`, sus tokens
`AqTheme`/`AqTokens` y los estados/variantes declarados en
`AqComponentRegistry`. La galería ya no contiene composables de preview
propios: renderiza el mismo registro en modo lectura; el Lab agrega selección,
viewport, escala y log de eventos. Producción usa exactamente el mismo
componente y no debe introducir overrides visuales específicos.

Cualquier ajuste de estilo se realiza una vez en `core/ui` y se valida en
Lab, galería y capturas de producción. El gate
`verifySharedComponentConfiguration` falla si se separan las fuentes,
si la galería vuelve a declarar previews o si se deja de usar `AqTheme`.

## Añadir un componente al registro

Todo componente público `fun Aq*(...)` de
`core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/components/` **debe**
tener entrada en `AqComponentRegistry`
(`core/ui/.../theme/AqComponentRegistry.kt`); el gate
`verifyComponentRegistry` (dentro de `verifyAgendaQrArchitecture`) falla en
caso contrario, igual que si una entrada apunta a un componente
inexistente o no declara estados.

```kotlin
entry(
    "AqMiComponente", AqComponentCategory.Forms,
    state("normal", "Normal") { log ->
        AqMiComponente(/* … */, onClick = { log("click") })
    },
    state("error", "Error") { _ ->
        AqMiComponente(/* … */, isError = true)
    },
)
```

Convenciones:

- El `id` de `entry` y el `id` de cada `state` forman el hash
  (`#AqMiComponente/error`); usa identificadores estables en minúsculas.
- El contenido del estado recibe `log: (String) -> Unit` para registrar
  interacciones; los callbacks del componente deben invocarlo.
- Las previews asumen `AqTheme` ambiente (el lab la aplica); no la
  envuelvas dentro del estado.
- El copy de ejemplo es del propio lab (herramienta de desarrollo); la
  regla «sin copy hardcodeado» aplica a `components/`, no al registro.

## Límites conocidos

- `AqThumbnail` solo exhibe `Loading`/`Failure`: el estado `Success`
  requiere un `ImageBitmap` decodificado de un archivo, que no existe en
  wasmJs (la plataforma devuelve `null` por diseño).
- Las sombras usan la aproximación por elevación de Compose (E-22), igual
  que en iOS; la geometría `BoxShadow` exacta solo está en Android.
- No se versionan capturas del lab (ADR-0007): las evidencias visuales de
  verificación van a `build/`.
