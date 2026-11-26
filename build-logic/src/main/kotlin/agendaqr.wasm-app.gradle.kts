import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenExtension
import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootExtension
import java.io.File

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Toolchain wasmJs portable. Por defecto se deja el comportamiento del
// plugin Kotlin/Wasm (descarga Node/Yarn/Binaryen). En entornos con
// firewall que bloqueen nodejs.org / github.com/yarnpkg / github.com se
// pueden fijar overrides SIN tocar el repo, en `~/.gradle/gradle.properties`
// o como variables de entorno (ver docs/05-design-system/15-component-lab.md):
//
//   agendaqr.wasm.nodeDownload=false     AGENDQR_WASM_NODE_DOWNLOAD=false
//   agendaqr.wasm.yarn=/ruta/al/yarn     AGENDQR_WASM_YARN=/ruta/al/yarn
//   agendaqr.wasm.binaryen=/ruta/wasm-opt AGENDQR_WASM_BINARYEN=/ruta/wasm-opt
//
// Las rutas solo se aplican si el archivo existe; si no, se ignoran y el
// plugin usa su comportamiento por defecto.
private fun wasmToolchainSetting(name: String): String? {
    val propName = "agendaqr.wasm.$name"
    val envName = "AGENDQR_WASM_" + name.uppercase().replace('.', '_')
    return providers.gradleProperty(propName).orNull
        ?: System.getenv(envName)?.takeIf { it.isNotBlank() }
}

// Los setters `download`/`command` del toolchain wasm están deprecados a
// nivel ERROR en Kotlin 2.2 (retirada prevista en 2.3), así que no existe
// API pública usable en esta versión. La reflexión queda aislada aquí y
// tolerante a fallos: si Kotlin cambia la firma en 2.3, el override se
// ignora y el plugin vuelve a su comportamiento por defecto en lugar de
// romper la configuración del build. (Decisión documentada en ADR-0050.)
private fun javaSetter(target: Any, name: String, value: Any) {
    runCatching {
        val method = generateSequence(target.javaClass) { it.superclass }
            .flatMap { it.declaredMethods.asSequence() }
            .first { it.name == name && it.parameterCount == 1 }
        method.isAccessible = true
        method.invoke(target, value)
    }
}

gradle.projectsEvaluated {
    val nodeDownload = wasmToolchainSetting("nodeDownload")
    rootProject.extensions.findByType(WasmNodeJsRootExtension::class.java)?.let { extension ->
        if (nodeDownload.equals("false", ignoreCase = true)) {
            javaSetter(extension, "setDownload", false)
        }
    }
    val yarnPath = wasmToolchainSetting("yarn")
    if (yarnPath != null && File(yarnPath).exists()) {
        rootProject.extensions.findByType(WasmYarnRootExtension::class.java)?.let { extension ->
            javaSetter(extension, "setDownload", false)
            javaSetter(extension, "setCommand", yarnPath)
        }
    }
}

// Binaryen: mismo mecanismo; `agendaqr.wasm.binaryen` apunta al binario
// `wasm-opt` local (p. ej. el del paquete npm `binaryen`, que es la misma
// versión compilada a WebAssembly y corre con el node del sistema).
project.plugins.withType(BinaryenRootPlugin::class.java) {
    val binaryenPath = wasmToolchainSetting("binaryen")
    if (binaryenPath != null && File(binaryenPath).exists()) {
        val extension = project.extensions.getByType(BinaryenExtension::class.java)
        javaSetter(extension, "setDownload", false)
        javaSetter(extension, "setCommand", binaryenPath)
    }
}

// App wasmJs browser (Component Lab). A diferencia de
// `agendaqr.kmp-library`, declara SOLO el target web: el lab no compila
// para Android ni iOS. Requiere que el módulo fije `wasmJs { browser() }`
// y sus dependencias en su propio build.gradle.kts.
