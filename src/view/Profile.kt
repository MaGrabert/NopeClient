package view

import socket.SocketHandler
import tornadofx.*

/**
 * Shows the result of the matches
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
class Profile: View("Nope-Client-KI") {
    override val root = vbox {
        prefWidth = 1024.0
        prefHeight = 800.0

        menubar {
            menu("User") {
                item("Disconnect") {
                    action {
                        SocketHandler.disconnect()
                        replaceWith<SignUpView>()
                        currentWindow?.sizeToScene()
                    }
                }
            }
            menu("Game") {
                if(!SocketHandler.beInTournament) {
                    item("Current Move"){
                        action {
                            replaceWith<MainView>()
                        }
                    }
                } else {
                    item("Tournament"){
                        action {
                            replaceWith<TournamentView>()
                        }
                    }
                }

            }
        }
    }
}