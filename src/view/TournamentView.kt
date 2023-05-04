package view

import javafx.geometry.Pos
import socket.SocketHandler
import tornadofx.*

class TournamentView : View("Nope-Client-KI") {
    override val root = borderpane() {
        prefWidth = 1024.0
        prefHeight = 800.0

        top {
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
        bottom {
            hbox(spacing = 10, alignment = Pos.CENTER) {
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
}