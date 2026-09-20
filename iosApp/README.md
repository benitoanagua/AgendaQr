# iOS host

The Kotlin Multiplatform `:shared` module publishes the `AgendaQrShared` framework. The SwiftUI host consumes `MainViewController(filePath:)` and provides the platform-local application-support path.

Xauxa parity requires native integrations (camera/share/biometric/brightness) to be added at the platform boundary without changing the domain model.
