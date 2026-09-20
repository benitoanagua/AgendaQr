import SwiftUI
import UIKit
import AgendaQrShared

struct ComposeRootView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@main
struct AgendaQrIOSApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeRootView()
        }
    }
}
