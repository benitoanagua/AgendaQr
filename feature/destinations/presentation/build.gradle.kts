import java.io.File

plugins {
    id("agendaqr.compose-library")
}

android {
    namespace = "com.agendaqr.destinations.presentation"
    testOptions {
        // Robolectric necesita los recursos del módulo para las pruebas
        // de UI Compose.
        unitTests.isIncludeAndroidResources = true
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:destinations:domain"))
            implementation(project(":feature:destinations:data"))
            implementation(project(":core:ui"))
            implementation(compose.material3)
            implementation(compose.animation)
            // BackHandler/PredictiveBackHandler multiplataforma (CMP 1.8.x):
            // en Android delega en OnBackPressedDispatcher (gesto/back real),
            // en iOS/web en el dispatcher de la plataforma.
            // collectAsStateWithLifecycle en commonMain.
            implementation(libs.lifecycle.runtime.compose)
            implementation("org.jetbrains.compose.ui:ui-backhandler:${libs.versions.compose.get()}")
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.activity.compose)
            implementation(libs.core.ktx)
            implementation(libs.zxing.core)
            // S03: captura con CameraX + análisis continuo.
            implementation(libs.camera.core)
            implementation(libs.camera.camera2)
            implementation(libs.camera.lifecycle)
            implementation(libs.camera.view)
        }
        commonTest.dependencies {
            implementation(libs.coroutines.test)
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            // Pruebas de UI Compose sobre Robolectric (JVM): corren en el
            // gate existente (testDebugUnitTest) sin emulador en CI.
            implementation("androidx.compose.ui:ui-test-junit4:${libs.versions.androidx.compose.get()}")
            implementation("androidx.compose.ui:ui-test-manifest:${libs.versions.androidx.compose.get()}")
            implementation("org.robolectric:robolectric:${libs.versions.robolectric.get()}")
            // Capturas de referencia bajo demanda (ADR-0007). Solo dibujan
            // píxeles cuando se activa -Pagendaqr.capture=true; en el gate
            // normal la captura es un no-op y no se versiona ninguna imagen.
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
        }
    }
}

// Las capturas Roborazzi se generan sobre el mismo harness Robolectric
// (testDebugUnitTest), sin emulador:
//
//   ./gradlew :feature:destinations:presentation:testDebugUnitTest \
//       -Pagendaqr.capture=true
//
// Salida: feature/destinations/presentation/build/roborazzi/*.png
// (gitignored: ADR-0007 prohíbe versionar imágenes/hashes). Sin el flag,
// los tests siguen verificando el layout sin escribir nada.
tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    val capture = providers.gradleProperty("agendaqr.capture").orNull == "true"
    systemProperty("agendaqr.capture", capture.toString())
    if (capture) {
        systemProperty("roborazzi.test.record", "true")
        // Regeneración explícita: con el flag la suite corre siempre fresca
        // (los PNG son efectos laterales que Gradle no rastrea) y tras
        // limpiar las galerías, para no dejar restos de escenarios
        // renombrados o retirados (nada se versiona; ADR-0007).
        outputs.upToDateWhen { false }
        dependsOn("cleanCaptureGalleries")
    }
}

// Atajo: regenera las capturas y deja un índice navegable para revisarlas
// en el navegador. Requiere el flag explícito para no generar imágenes por
// accidente (ADR-0007, nada versionado).
// Limpieza previa: ambas galerías se regeneran completas, así que se
// retiran los restos de escenarios renombrados o retirados ANTES de que
// corran los tests (nada se versiona; ADR-0007). Con el flag, los tests
// la declaran como dependencia (ver tasks.withType<Test>).
tasks.register("cleanCaptureGalleries") {
    doFirst {
        if (providers.gradleProperty("agendaqr.capture").orNull != "true") {
            throw GradleException(
                "Ejecuta: ./gradlew :feature:destinations:presentation:captureScreenshots -Pagendaqr.capture=true",
            )
        }
        listOf("build/roborazzi", "build/captures/gallery").forEach { dir ->
            layout.projectDirectory.dir(dir).asFile
                .listFiles()?.forEach { if (it.isFile) it.delete() }
        }
    }
}

tasks.register("captureScreenshots") {
    group = "verification"
    description = "Regenera las capturas de referencia y escribe build/roborazzi/index.html (requiere -Pagendaqr.capture=true)."
    dependsOn("testDebugUnitTest")
    doLast {
        if (providers.gradleProperty("agendaqr.capture").orNull != "true") {
            throw GradleException(
                "Ejecuta: ./gradlew :feature:destinations:presentation:captureScreenshots -Pagendaqr.capture=true",
            )
        }
        val dir = layout.projectDirectory.dir("build/roborazzi").asFile
        val images = dir.listFiles { f -> f.extension == "png" }?.sortedBy { it.name }.orEmpty()
        val cards = images.joinToString("\n") { img ->
            val name = img.name
            """  <figure><img loading="lazy" src="$name" alt="$name"><figcaption>$name</figcaption></figure>"""
        }
        File(dir, "index.html").writeText(
            """
            <!doctype html>
            <meta charset="utf-8">
            <title>Capturas de referencia AgendaQr (${images.size})</title>
            <style>
              body{font-family:system-ui,sans-serif;margin:16px;background:#fafafa}
              .grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(220px,1fr));gap:12px}
              figure{margin:0;background:#fff;border:1px solid #ddd;border-radius:8px;padding:8px;text-align:center}
              img{max-width:100%;height:auto;image-rendering:auto}
              figcaption{font-size:11px;color:#555;margin-top:6px;word-break:break-all}
            </style>
            <h1>Capturas de referencia (${images.size})</h1>
            <p>Generadas bajo demanda con Robolectric + Roborazzi. No versionadas (ADR-0007).</p>
            <div class="grid">
            $cards
            </div>
            """.trimIndent(),
        )
        // Galería estricta por superficie (ADR-0007): índice propio
        // cuando el test la produjo con el mismo flag.
        val gallery = layout.projectDirectory.dir("build/captures/gallery").asFile
        val galleryImages = gallery.listFiles { f -> f.extension == "png" }?.sortedBy { it.name }.orEmpty()
        if (galleryImages.isNotEmpty()) {
            val galleryCards = galleryImages.joinToString("\n") { img ->
                val name = img.name
                """  <figure><img loading="lazy" src="$name" alt="$name"><figcaption>$name</figcaption></figure>"""
            }
            File(gallery, "index.html").writeText(
                """
                <!doctype html>
                <meta charset="utf-8">
                <title>Galería estricta AgendaQr (${galleryImages.size})</title>
                <style>
                  body{font-family:system-ui,sans-serif;margin:16px;background:#fafafa}
                  .grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(220px,1fr));gap:12px}
                  figure{margin:0;background:#fff;border:1px solid #ddd;border-radius:8px;padding:8px;text-align:center}
                  img{max-width:100%;height:auto;image-rendering:auto}
                  figcaption{font-size:11px;color:#555;margin-top:6px;word-break:break-all}
                </style>
                <h1>Galería estricta por superficie (${galleryImages.size})</h1>
                <p>Esquema <superficie>--<estado>--<ancho>dp--<tema>--font<escala>. Validada por <code>verifyScreenContract</code>; manifiesto en <code>build/captures/manifest.json</code>.</p>
                <div class="grid">
                $galleryCards
                </div>
                """.trimIndent(),
            )
            logger.lifecycle(
                "captureScreenshots: galería estricta con ${galleryImages.size} capturas; " +
                    "abre ${gallery.resolve("index.html")}",
            )
        }
        logger.lifecycle(
            "captureScreenshots: ${images.size} capturas; abre ${dir.resolve("index.html")}",
        )
    }
}
