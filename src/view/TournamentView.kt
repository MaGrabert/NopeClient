package view

import socket.SocketHandler
import tornadofx.*

class TournamentView : View("Nope-Client-KI") {
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

            button("Leave Tournament") {
                action {
                    SocketHandler.leaveTournament()
                    replaceWith<MainView>()
                    currentWindow?.sizeToScene()
                }
            }
        }
    }
}