import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenExtension
import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Toolchain local: el plugin Kotlin/Wasm descarga Node 22 desde
// nodejs.org/dist y Yarn 1.22 desde github.com/yarnpkg — ambos bloqueados
// por el firewall del entorno. Se desactiva la descarga y se usan los
// binarios del sistema (node en PATH; yarn 1.22.17 instalado en
// ~/.local/bin). Las propiedades `download`/`command` están marcadas como
// error de deprecación en Kotlin 2.2 (retirada en 2.3), así que se fijan
// por los setters Java para no usar la API deprecada. La configuración se
// aplica tras evaluar todos los proyectos: las extensiones raíz del
// toolchain wasm se registran de forma perezosa.
private fun javaSetter(target: Any, name: String, value: Any) {
    val method = generateSequence(target.javaClass) { it.superclass }
        .flatMap { it.declaredMethods.asSequence() }
        .first { it.name == name && it.parameterCount == 1 }
    method.isAccessible = true
    method.invoke(target, value)
}

gradle.projectsEvaluated {
    rootProject.extensions.findByType(WasmNodeJsRootExtension::class.java)?.let { extension ->
        javaSetter(extension, "setDownload", false)
    }
    rootProject.extensions.findByType(WasmYarnRootExtension::class.java)?.let { extension ->
        javaSetter(extension, "setDownload", false)
        javaSetter(extension, "setCommand", "/home/mamaya/.local/bin/yarn")
    }
}

// Binaryen: el binario oficial se descarga de github.com (bloqueado). Se
// usa el paquete npm `binaryen@123.0.0` (registry.yarnpkg.com, alcanzable),
// que empaqueta la misma versión compilada a WebAssembly y ejecutable con
// el node del sistema — funcionalmente equivalente para `wasm-opt`.
project.plugins.withType(BinaryenRootPlugin::class.java) {
    val extension = project.extensions.getByType(BinaryenExtension::class.java)
    javaSetter(extension, "setDownload", false)
    javaSetter(extension, "setCommand", "/home/mamaya/.local/share/kotlin-binaryen/wasm-opt")
}

// App wasmJs browser (Component Lab). A diferencia de
// `agendaqr.kmp-library`, declara SOLO el target web: el lab no compila
// para Android ni iOS. Requiere que el módulo fije `wasmJs { browser() }`
// y sus dependencias en su propio build.gradle.kts.
