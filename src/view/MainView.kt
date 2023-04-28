package view

import socket.SocketHandler
import tornadofx.*

/**
 * Shows what the AI currently do
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
class MainView: View("Nope-Client-KI") {
    override val root = vbox {
        prefWidth = 1024.0
        prefHeight = 800.0

        menubar {
            menu("User") {
               item("Disconnect") {
                   action {
                       SocketHandler.disconnect()
                       replaceWith<SignInView>()
                       currentWindow?.sizeToScene()
                   }
               }
            }
            menu("Game") {
               item("Results"){
                   action {
                       replaceWith<ResultView>()
                   }
               }
            }
        }
    }
}