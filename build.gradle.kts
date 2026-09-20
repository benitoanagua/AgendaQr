plugins {
    id("agendaqr.design-system")
}

tasks.named("check") {
    dependsOn("verifyAgendaQrArchitecture")
}
