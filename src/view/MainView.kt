package view

import socket.Socket
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
                       replaceWith<SignInView>()
                       currentWindow?.sizeToScene()
                       Socket.disconnect()
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