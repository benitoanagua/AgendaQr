plugins {
    id("agendaqr.design-system")
}

val componentLabWebDistribution = ":core:ui:wasmJsBrowserDevelopmentExecutableDistribution"

tasks.register<Sync>("syncComponentLabWeb") {
    group = "design-system"
    description = "Copies the generated Wasm component lab distribution into the root build/ workspace."
    dependsOn(componentLabWebDistribution)

    from(project(":core:ui").layout.buildDirectory.dir("dist/wasmJs/developmentExecutable"))
    into(layout.buildDirectory.dir("web/component-lab"))
}

tasks.register("componentLabWeb") {
    group = "design-system"
    description = "Builds the AgendaQr Compose Multiplatform/Wasm component lab into build/web/component-lab/."
    dependsOn("syncComponentLabWeb")
}
