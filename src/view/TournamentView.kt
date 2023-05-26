package view

import app.Profile
import javafx.geometry.Pos
import javafx.scene.control.Label
import socket.SocketHandler
import tornadofx.*

/**
 * Shows the tournament
 *
 * @author Mathis Grabert
 * @since 03.05.2023
 */
class TournamentView : View("Nope-Client-KI") {
    override val root = borderpane() {
        prefWidth = 1024.0
        prefHeight = 800.0
        var labelMessage: Label
        var labelID: Label
        var labelSize: Label
        var labelStatus: Label
        var labelMatches: Label
        var labelPlayers: Label
        var labelHost: Label
        var labelWinner: Label

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
                    item("Profile") {
                        action {
                            replaceWith<ProfileView>()
                        }
                    }
                }
            }
        }

        left {
            prefHeight = 800.0
            prefWidth = 100.0
            label("Message:")
            label("...")
            label("ID:")
            label("...")
            label("Tournament-Size:")
            label("...")
            label("Status:")
            label("...")
            label("Matches:")
            label("...")
            label("Players:")
            label("...")
            label("Host:")
            label("...")
            label("Winner:")
            label("...")
        }

        center {

        }

        bottom {
            hbox(spacing = 10, alignment = Pos.CENTER) {
                button("Leave Tournament") {
                    action {
                        Profile.isInTournament = false
                        Profile.isHost = false
                        SocketHandler.emit("tournament:leave")
                        replaceWith<MainView>()
                        currentWindow?.sizeToScene()
                    }
                }

                if (Profile.isHost) {
                    button("Start Tournament") {
                        action {
                            SocketHandler.emit("tournament:start")
                        }
                    }
                }
            }
        }
    }
}