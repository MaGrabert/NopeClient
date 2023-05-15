package view

import app.Profile
import socket.SocketHandler
import tornadofx.*

/**
 * Shows the player profile
 *
 * @author Mathis Grabert
 * @since 07.04.2023
 */
class ProfileView : View("Nope-Client-KI") {
    override val root = borderpane() {
        prefWidth = 1024.0
        prefHeight = 800.0

        top {
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
                    if (!Profile.isInTournament) {
                        item("All Tournaments") {
                            action {
                                replaceWith<MainView>()
                            }
                        }
                    } else {
                        item("Match") {
                            action {
                                replaceWith<TournamentView>()
                            }
                        }
                    }
                }
            }
        }

        center {
            form {
                fieldset("Profile-details:") {
                    field("User-Name:") {
                        label(Profile.userName)
                    }
                    field("Is in tournament:") {
                        label(Profile.isInTournament.toString())
                    }
                }
            }
        }
    }
}